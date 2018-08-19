/////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////
//himaster 智能管家M1 数据监测模块   
//主要功能回传实时温度，湿度 ,光照等参数  到树莓派服务器中
//对应指令有CL 控制命令  ，DT 数据命令 ，ST模块状态命令
//也可以用远程控制模块上对应设备。
//作者:HHH
//日期：2018-07
////////////////////////////////////////////////////////////////////



#include "stm32f10x.h"
#include "includes.h"

#include "usart.h"
#include "led.h"
#include "beep.h"
#include "ds18b20.h"
#include "wifi.h"
#include "dht11.h"

extern u8 esp_8662_init(void);
extern void openled(void);
extern void closeled(void);
extern void ESP8266_Init(void);
extern void delay_init(void);
extern void delay_ms(u16 nms);
extern u8 client_init(void);
extern u8 client_send(u8 *cmd);
extern u8 receive_server(void);
extern u8 atk_8266_quit_trans(void);
extern u8 DHT11_Read_Data(u8 *temp,u8 *humi);
extern u8 DHT11_Init(void);


/////////////////////////UCOSII任务设置///////////////////////////////////
// start_task  用于创建各个进程  后挂起  挂起前不会给打断，但是优先级最低
//drSTATUS_TASK       用于反回设备的 运行状态 ，间隔10秒  delay时间 长，所以优先级最高。
//transmission_TASK     监控数据返回    
// Contrl_TASK          接收命令 并 控制    空闲时间最短   ， 优先级最低
// 高延迟 低优先级 容易给打短 所以 优先级要高。会等到他空闲时间在执行别的    正常工作  
//////////////////////////////////////////////////////////////////////////

//START 任务
//设置任务优先级
#define START_TASK_PRIO      			10 //开始任务的优先级设置为最低
//设置任务堆栈大小
#define START_STK_SIZE  				64
//任务堆栈	
OS_STK START_TASK_STK[START_STK_SIZE];
//任务函数
void start_task(void *pdata);	
 			   
//Contrl_task      接收+执行 CL控制命令
//设置任务优先级
#define Contrl_TASK_PRIO       			6 
//设置任务堆栈大小
#define Contrl_STK_SIZE  		    		64
//任务堆栈	
OS_STK Contrl_TASK_STK[Contrl_STK_SIZE];
//任务函数
void Contrl_task(void *pdata);


//transmission任务    // 日常回传数据任务
//设置任务优先级
#define transmission_TASK_PRIO       			5
//设置任务堆栈大小
#define transmission_STK_SIZE  					64
//任务堆栈
OS_STK transmission_TASK_STK[transmission_STK_SIZE];
//任务函数
void transmission_task(void *pdata);


//drSTATUS任务    // 10秒一次 ，发送设备状态
//设置任务优先级
#define drSTATUS_TASK_PRIO       			4 
//设置任务堆栈大小
#define drSTATUS_STK_SIZE  					64
//任务堆栈
OS_STK drSTATUS_TASK_STK[drSTATUS_STK_SIZE];
//任务函数
void drSTATUS_task(void *pdata);




//connectPISER 初始化函数
u8 connectPISER(){
		SystemInit (); // 配置系统时钟为72M 		
		while(1){              //等待连接成功
			atk_8266_quit_trans();//先退出穿透  确保初始化成功 （即ap指令可以正常发送）
			if(!esp_8662_init()){
				openled();	    //成功 开灯跳出
				break;
			}
		}
		atk_8266_send_cmd("AT+CIPMUX=0","OK",20);  //单链接模式
		while(1){              //等待创立客户端
			if(!client_init()){
				closeled();
				break;       //成功关灯跳出
			}
		}
				
		atk_8266_send_cmd("AT+CIPMODE=1","OK",20);	//开始透传	
		atk_8266_send_cmd("AT+CIPSEND","OK",20);
		client_send("MODULE1 CONNENT\n");
		return 0;
}
/////////////////////////////////////////////////////////////////////////////////////////
///设备状态位0 正常 1有问题
u8 ds18b20ST=0;
u8 DHT11ST=0;
u8 wifiST=0;
////////////////////////////////////////////////////////////////////////////////////////

void driverinit(){		
	
		NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);//设置中断优先级分组为组2：2位抢占优先级，2位响应优先级
		delay_init();	    //延时函数初始化
		uart_init(115200);   //uart1 PA9 PA10
		LED_init();					//LED_init
		BEEP_init();				//BEEP 蜂鸣器初始化
		if(DS18B20_Init())	//DS18B20初始化	
		{		
			
				//ds18b20ST=1;// 失败 设备状态位置1
				delay_ms(20);
		}	
		if(DHT11_Init())	//DHT11_Init初始化	
		{		
			
				DHT11ST=1;// 失败 设备状态位置1
				delay_ms(20);
		}	
		if(connectPISER())	//connectPISER初始化	 
		{		
			
				wifiST=1;// 失败 设备状态位置1
				delay_ms(20);
		}	
		
}

//注意数据统一 wifi发到树莓派   num  drivernum   dstatus(1/0)设备是否正常   run（1/0）是否在运行 
int main (void)
{		

	  driverinit(); 
		OSInit();   
		OSTaskCreate(start_task,(void *)0,(OS_STK *)&START_TASK_STK[START_STK_SIZE-1],START_TASK_PRIO );//创建起始任务
		OSStart();	 
}

	  
//开始任务
void start_task(void *pdata)
{
    OS_CPU_SR cpu_sr=0;
	pdata = pdata; 
  	OS_ENTER_CRITICAL();			//进入临界区(无法被中断打断)    
 	OSTaskCreate(Contrl_task,(void *)0,(OS_STK*)&Contrl_TASK_STK[Contrl_STK_SIZE-1],Contrl_TASK_PRIO);						   
 	OSTaskCreate(transmission_task,(void *)0,(OS_STK*)&transmission_TASK_STK[transmission_STK_SIZE-1],transmission_TASK_PRIO);	
  OSTaskCreate(drSTATUS_task,(void *)0,(OS_STK*)&drSTATUS_TASK_STK[drSTATUS_STK_SIZE-1],drSTATUS_TASK_PRIO);	
	OSTaskSuspend(START_TASK_PRIO);	//挂起起始任务.
	OS_EXIT_CRITICAL();				//退出临界区(可以被中断打断)
}

//Contrl 任务  用来接收CL命令并且控制
void Contrl_task(void *pdata)
{	 	
		while(1){	
			receive_server();
			delay_ms(10);
		}
	
}

//      transmission   日常发送任务
void transmission_task(void *pdata)
{	  //dht11_task return to 串口	
		
		u8 temperature;  	    
		u8 humidity; 
		delay_ms(500);
		while(1)
		{	    	    								  
			DHT11_Read_Data(&temperature,&humidity);			
			printf("DT+M1+HM+%d\n",humidity);			  //返回  湿度
			delay_ms(200);
			//temperature=DS18B20_Get_Temp();	
			//temperature=temperature;
			printf("DT+M1+TM+%d\n",temperature*10);			  //返回  温度   //精度：0.1C  返回值：温度值 （-550~1250） 
			delay_ms(5000);
		}	
}



//设备状态  任务
void drSTATUS_task(void *pdata)        //设置后  重启设备
{	  
		while(1)
		{	    
				if(ds18b20ST){printf("ST+M1+TM+FL\n");}	
				else{printf("ST+M1+TM+GD\n");}
				delay_ms(20);
				if(DHT11ST){printf("ST+M1+HM+FL\n");}	
				else{printf("ST+M1+HM+GD\n");}
				delay_ms(20);
				if(wifiST){printf("ST+M1+WF+FL\n");}	
				else{printf("ST+M1+WF+GD\n");}
				
				delay_ms(60000);

		}			
}

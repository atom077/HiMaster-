#include "stm32f10x.h"
#include "includes.h"

//#include "delay.h"
#include "usart.h"
#include "led.h"
#include "beep.h"
#include "wifi.h"

extern u8 doorST;
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
extern void STEPPER_init(void);
extern void steprightoneround(void);
extern void stepleftoneround(void);
extern u8  getkey(void);


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
		client_send("MODULE2 stepper CONNENT\n");
		return 0;
}
/////////////////////////////////////////////////////////////////////////////////////////
///设备状态位0 正常 1有问题
u8 stepST=0;
u8 wifiST=0;

////////////////////////////////////////////////////////////////////////////////////////

void driverinit(){		
	
		NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);//设置中断优先级分组为组2：2位抢占优先级，2位响应优先级
		delay_init();	    //延时函数初始化
		uart_init(115200);   //uart1 PA9 PA10
		LED_init();					//LED_init
		BEEP_init();				//BEEP 蜂鸣器初始化
		STEPPER_init();      //不经电机舒适化 
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
  OSTaskCreate(drSTATUS_task,(void *)0,(OS_STK*)&drSTATUS_TASK_STK[drSTATUS_STK_SIZE-1],drSTATUS_TASK_PRIO);	
	OSTaskSuspend(START_TASK_PRIO);	//挂起起始任务.
	OS_EXIT_CRITICAL();				//退出临界区(可以被中断打断)
}



//Contrl 任务  用来接收CL命令并且控制
void Contrl_task(void *pdata)
{	 	
		
		while(1){	
			receive_server();
//			printf("%d\n",getkey());
//			delay_ms(100);
			if(getkey()==1){ 	//读到低电平  开关按下 
					steprightoneround(); doorST=1;BEEP(ON); delay_ms(500);	BEEP(OFF) ; delay_ms(5);client_send("K M2 DOOR is ON now!!!!\n");
			}
			if(doorST == 1)    //如果门开   延时5 秒自动关
			{delay_ms(5000);stepleftoneround(); doorST=0; BEEP(ON); delay_ms(1000);	BEEP(OFF) ;	delay_ms(5);client_send("K M2  DOOR is OFF now!!!!\n");}
		
			
			}
	
}



//设备状态  任务
void drSTATUS_task(void *pdata)        //设置后  重启设备
{	  int i;
		while(1)
		{	    
				if(wifiST){printf("ST+M2+WF+FL\n");}	
				else{printf("ST+M2+WF+GD\n");}
				delay_ms(10);
				if(stepST){printf("ST+M2+STP+FL\n");}	
				else{printf("ST+M2+STP+GD\n");}
				for(i=0;i<5;i++)
					delay_ms(60000);                 //没30分钟返回一次
		}			
}

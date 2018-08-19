#include "stm32f10x.h"

#include "usart.h"
#include "wifi.h"
#include "switch.h"

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
////////////////////////////////////////////////
//HiMaster 智能插座模块
//2018-8-20
/////////////////////////////////////////////////////////////////////////////////////////
///设备状态位0 正常 1有问题
u8 wifiST=0;

//connectPISER 初始化函数
u8 connectPISER(){
		SystemInit (); // 配置系统时钟为72M 		
		while(1){              //等待连接成功
			atk_8266_quit_trans();//先退出穿透  确保初始化成功 （即ap指令可以正常发送）
			if(!esp_8662_init()){
				break;
			}
		}
		atk_8266_send_cmd("AT+CIPMUX=0","OK",20);  //单链接模式
		while(1){              //等待创立客户端
			if(!client_init()){
				break;       //成功关灯跳出
			}
		}
				
		atk_8266_send_cmd("AT+CIPMODE=1","OK",20);	//开始透传	
		atk_8266_send_cmd("AT+CIPSEND","OK",20);
		client_send("MODULE3 SWITCH CONNENT\n");
		return 0;
}

void driverinit(){
		NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);//设置中断优先级分组为组2：2位抢占优先级，2位响应优先级  ,usart接收中断
		SystemInit (); // 配置系统时钟为72M 	
		delay_init();	    	 //延时函数初始化	  
		uart_init(115200);
		SWITCH_init();
		if(connectPISER())	//connectPISER初始化	 
		{		
			
				wifiST=1;// 失败 设备状态位置1
				delay_ms(20);
		}	
}

//注意数据统一 
int main (void)
{		
		driverinit();
		if(wifiST){printf("ST+M3+WF+FL\n");}	
		else{printf("ST+M3+WF+GD\n");}
		while(1){
				
				receive_server();//循环读取
				delay_ms(100);
		}
}



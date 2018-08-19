#include "usart.h"		
#include "delay.h"
#include "wifi.h"
#include "led.h"
#include "switch.h"
extern u8  USART_TX_BUF[USART_MAX_SEND_LEN]; 
extern void openled(void); 
extern void closeled(void); 
/////////////////////////////////////////////////////////////////////////////////////////////////////////// 
//用户配置

//port ：6666  可改
const u8* portnum="8086";		 

//WIFI STA模式 客户端连接无线参数
const u8* wifista_ssid="fast";			//路由器号
const u8* wifista_encryption="wpawpa2_aes";	//wpa/wpa2 模式
const u8* wifista_password="hhh11223344"; 	//密码

//WIFI AP模式 服务器 可改
const u8* wifiap_ssid="module1";			//发出的路由
const u8* wifiap_encryption="wpawpa2_aes";	//wpa/wpa2 加密
const u8* wifiap_password="12345678"; 		//密码

/////////////////////////////////////////////////////////////////////////////////////////////////////////// 
//4模式
const u8 *ATK_ESP8266_CWMODE_TBL[3]={"STA模式 ","AP模式 ","AP&STA模式 "};	//ATK-ESP8266,3ÖÖÍøÂçÄ£Ê½,Ä¬ÈÏÎªÂ·ÓÉÆ÷(ROUTER)Ä£Ê½ 
//4工作方式
const u8 *ATK_ESP8266_WORKMODE_TBL[3]={"TCP server","TCP client"," UDP module"};	//ATK-ESP8266,4ÖÖ¹¤×÷Ä£Ê½
//5加密方式
const u8 *ATK_ESP8266_ECN_TBL[5]={"OPEN","WEP","WPA_PSK","WPA2_PSK","WPA_WAP2_PSK"};
/////////////////////////////////////////////////////////////////////////////////////////////////////////// 


//ESP8266发送命令后,检测接收到的应答
//str:期待的应答结果
//返回值:0,没有得到期待的应答结果
//    其他,期待应答结果的位置(str的位置)
u8* atk_8266_check_cmd(u8 *str)
{
	
	char *strx=0;
	if(USART_RX_STA&0X8000)		//接收到一次数据了
	{ 
		USART_RX_BUF[USART_RX_STA&0X7FFF]=0;//添加结束符
		strx=strstr((const char*)USART_RX_BUF,(const char*)str);
	} 
	return (u8*)strx;
}


//向ESP8266发送命令
//cmd:发送的命令字符串
//ack:期待的应答结果,如果为空,则表示不需要等待应答
//waittime:等待时间(单位:10ms)
//返回值:0,发送成功(得到了期待的应答结果)
//       1,发送失败
u8 atk_8266_send_cmd(u8 *cmd,u8 *ack,u16 waittime)
{
	u8 res=0; 
	USART_RX_STA=0;
	u_printf("%s\r\n",cmd);	//发送命令
	if(ack&&waittime)		//需要等待应答
	{
		while(--waittime)	//等待倒计时
		{
			delay_ms(10);
			if(USART_RX_STA&0X8000)//接收到期待的应答结果
			{
				if(atk_8266_check_cmd(ack))
				{
					break;//得到有效数据 
				}
					USART_RX_STA=0;
			} 
		}
		if(waittime==0)res=1; 
	}
	return res;
} 

//向ESP8266发送指定数据
//data:发送的数据(不需要添加回车了)
//ack:期待的应答结果,如果为空,则表示不需要等待应答
//waittime:等待时间(单位:10ms)
//返回值:0,发送成功(得到了期待的应答结果)luojian
u8 atk_8266_send_data(u8 *data,u8 *ack,u16 waittime)
{
	u8 res=0; 
	USART_RX_STA=0;
	u_printf("%s",data);	//·¢ËÍÃüÁî
	if(ack&&waittime)		//ÐèÒªµÈ´ýÓ¦´ð
	{
		while(--waittime)	//µÈ´ýµ¹¼ÆÊ±
		{
			delay_ms(10);
			if(USART_RX_STA&0X8000)//½ÓÊÕµ½ÆÚ´ýµÄÓ¦´ð½á¹û
			{
				if(atk_8266_check_cmd(ack))break;//µÃµ½ÓÐÐ§Êý¾Ý 
				USART_RX_STA=0;
			} 
		}
		if(waittime==0)res=1; 
	}
	return res;
}

//ESP8266退出透传模式
//返回值:0,退出成功;
//       1,退出失败
u8 atk_8266_quit_trans(void)
{
	while((USART1->SR&0X40)==0);	//等待发送空
	USART1->DR='+';      
	delay_ms(15);					//大于串口组帧时间(10ms)
	while((USART1->SR&0X40)==0);	//等待发送空
	USART1->DR='+';      
	delay_ms(15);					//大于串口组帧时间(10ms)
	while((USART1->SR&0X40)==0);	//等待发送空
	USART1->DR='+';      
	delay_ms(500);					//等待500ms
	return atk_8266_send_cmd("AT","OK",20);//退出透传判断.
}



//获取ESP8266模块的AP+STA连接状态
//返回值:0，未连接;1,连接成功
u8 atk_8266_apsta_check(void)
{
	if(atk_8266_quit_trans())return 0;			//退出透传 
	atk_8266_send_cmd("AT+CIPSTATUS",":",50);	//发送AT+CIPSTATUS指令,查询连接状态
	if(atk_8266_check_cmd("+CIPSTATUS:0")&&
		 atk_8266_check_cmd("+CIPSTATUS:1")&&
		 atk_8266_check_cmd("+CIPSTATUS:2")&&
		 atk_8266_check_cmd("+CIPSTATUS:4"))
		return 0;
	else return 1;
}

//获取ESP8266模块的连接状态
//返回值:0,未连接;1,连接成功.
u8 atk_8266_consta_check(void)
{
	u8 *p;
	u8 res;
	if(atk_8266_quit_trans())return 0;			//退出透传 
	atk_8266_send_cmd("AT+CIPSTATUS",":",50);	//发送AT+CIPSTATUS指令,查询连接状态
	p=atk_8266_check_cmd("+CIPSTATUS:"); 
	res=*p;									//得到连接状态	
	return res;
}


//sta init 
//连接成功返回0
//错误返回1
u8 esp_8662_init(void){
		u8 p[100];
	//	int i=200000;
		atk_8266_send_cmd("AT+CWMODE=1","OK",50);	//设置WIFI STA模式
		delay_ms(1000);
		sprintf((char*)p,"AT+CWJAP=\"%s\",\"%s\"",wifista_ssid,wifista_password);//设置无线参数:ssid,密码
//		while(i--){          //等待连接一段时间
//			if(!atk_8266_send_cmd(p,"WIFI GOT IP",500)){
//				return 0;
//				}
//		}		
		atk_8266_send_cmd(p,"WIFI GOT IP",500);//可以直接不要判断
		return 0;
}




//sta client 连接服务器
//连接成功返回0
//错误返回其他
u8 client_init(void){
		u8 p[100];
		//int i=1;
//		atk_8266_send_cmd("AT+CIPMUX=0","OK",20); //单链接
////		if(!atk_8266_send_cmd("AT+CIPMODE=1","OK",200)){  //透传模式
////				return 1;
////		}
//		while(1)
// 		{if(!atk_8266_send_cmd("AT+CIPMODE=1","OK",20))
//				break;}   //正确继续	
			sprintf((char*)p,"AT+CIPSTART=\"TCP\",\"%s\",%s","192.168.1.109","8086");    //连接到制定pi服务器
		
//		if(atk_8266_send_cmd(p,"CONNECT",200)){  //等待连接0.2秒
//				return 1;
//		}	
		        
			if(!atk_8266_send_cmd(p,"CONNECT",200))  //等待连接一段时间
				{	
					return 0;
				}
		
//			atk_8266_send_cmd(p,"OK",200);
//		while(1)
// 		{if(!atk_8266_send_cmd("AT+CIPMODE=1","OK",20))
//				break;}   //正确继续	
		
		//delay_ms(9000);//延时一会在开始做数据收发 
		return 1;
}
//client 发送数据到server
//成功返回0
//错误1
u8 client_send(u8 *cmd){
		//u8 p[100];
		
		//sprintf((char*)p,"AT+CIPSEND=%d",rlen);    //AT+CIPSEND=len,指定长度
//		if(!atk_8266_send_cmd("AT+CIPSEND=3","OK",20)){  //发送到esp
//				return 1;
//		}
		u_printf((char *)cmd);
		return 0;
//		while(1){
//			if(!atk_8266_send_cmd("AT+CIPSEND",">",20)){
//					u_printf((char *)cmd);	/////////////	
//					return 0;
//			}
		
}
//client 接收server的数据 
//成功返回0
//错误1
u8 receive_server(void){
		if(USART_RX_STA&0X8000)		//接收到数据
		{			
/////数据处理////////////////
			if(USART_RX_BUF[0]=='C'&&USART_RX_BUF[1]=='L'){  //检测是否CL命令
										if(USART_RX_BUF[3]=='M'&&USART_RX_BUF[4]=='3')      //检测是否控制的是M3
										{	
												if(USART_RX_BUF[6]=='S'&&USART_RX_BUF[7]=='W'&&USART_RX_BUF[8]=='H'){ //STEP 步进电机 控制代码块
															if(USART_RX_BUF[10]=='O'&&USART_RX_BUF[11]=='N')	{			SWITCH(1);	client_send("ST+M3+SWH+ON\n");}  
																else if(USART_RX_BUF[10]=='O'&&USART_RX_BUF[11]=='F'&&USART_RX_BUF[12]=='F')	{	SWITCH(0);	client_send("ST+M3+SWH+OFF\n");}
												} 
								
										}
							
					}	

					USART_RX_STA=0;
					memset(USART_RX_BUF,0,sizeof(USART_RX_BUF));
		}				
		return 0;	
}
//AP init 
//连接成功返回0
//错误返回其他

void ESP8266_Init(void){  
    
}  




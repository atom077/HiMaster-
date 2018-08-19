#ifndef  __wifi_H
#define	 __wifi_H

#include "stm32f10x.h"
#include "string.h"





/////////////////////////////////////////////////////////////////////////////////////////////////////////// 
#define AT_MODE_CONFIG		0				//0,²ÉÓÃES/RSTÓ²¼þ¿ØÖÆ½øÈëATÄ£Ê½;
											//1,²ÉÓÃ´®¿ÚÌØÊâÐòÁÐ(+++1B1B1B)½øÈëATÄ£Ê½
/////////////////////////////////////////////////////////////////////////////////////////////////////////// 
void atk_8266_init(void);

//u8 atk_8266_mode_cofig(u8 netpro);
//void atk_8266_at_response(u8 mode);
u8* atk_8266_check_cmd(u8 *str);
u8 atk_8266_apsta_check(void);
u8 atk_8266_send_data(u8 *data,u8 *ack,u16 waittime);
u8 atk_8266_send_cmd(u8 *cmd,u8 *ack,u16 waittime);
//u8 atk_8266_quit_trans(void);
u8 atk_8266_consta_check(void);


//用户配置参数
extern const u8* portnum;			//连接端口
 
extern const u8* wifista_ssid;		//WIFI STA SSID
extern const u8* wifista_encryption;//WIFI STA 加密方式
extern const u8* wifista_password; 	//WIFI STA 密码

extern const u8* wifiap_ssid;		//WIFI AP SSID
extern const u8* wifiap_encryption;	//WIFI AP 加密方式
extern const u8* wifiap_password; 	//WIFI AP 密码

extern const u8* ATK_ESP8266_CWMODE_TBL[3];
extern const u8* ATK_ESP8266_WORKMODE_TBL[3];
extern const u8* ATK_ESP8266_ECN_TBL[5];
extern void u_printf(char* fmt,...) ;
#endif

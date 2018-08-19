#include <stdio.h>
#include "usepython.h"
#include <stdlib.h>
#include <string.h>

#include "config.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>

#define  CLTXT "/home/pi/master/driverCL/CL.txt"
#define  OPATH "/home/pi/master/rebs.txt"
#define FLAGS O_WRONLY|O_CREAT|O_TRUNC
#define MODE 00777
int Jump=0;

//清空机器人说
void clean_rebs(){
	system("> /home/pi/master/rebs.txt");
}
//机器人说
void rebspeak(char* sentence){
	int fd;
	printf("%s",sentence);
	if((fd=open(OPATH,FLAGS,MODE))<0){
		printf("open rebs error!\n");
	}
	else{
		if(write(fd,sentence,strlen(sentence))<0) printf("write rebs.txt error\n") ;
		close(fd);
		
	}
	Py_Initialize();
	PyCall("mix","mixen","()");
	Py_Finalize();
	//system("play /home/pi/master/out.mp3");
}
//我要听歌
void singsong(){
	if(SONG){
		int key;
		Py_Initialize();
		key=PyCall("mix","singsong","()");
		Py_Finalize();
		if(key){
			rebspeak("好的主人，现在为您播放"); 
			system("play /home/pi/Desktop/xsycq.mp3");
			rebspeak("您的品味真高");
			Jump = 1;//跳出循环。不执行图灵回复。
		}
	}
}



//帮我开灯  M3 插座模块  随意控制
void ledON(){
	if(LED){
		int key;
		int CLfd;
		char p[20]="CL+M3+SWH+ON";
		Py_Initialize();
		key=PyCall("mix","ledON","()");
		Py_Finalize();
		if(key){
			//rebspeak("好的主人，正在打开"); 
			CLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE);
			if(write(CLfd,p,strlen(p))>10) //成功
				rebspeak("已经打开，请问还有什么可以帮到您"); 
			close(CLfd);
			Jump = 1;//跳出循环。不执行图灵回复。
		}
	}
}
//帮我关灯
void ledOFF(){
	if(LED){
		int key;
		int CLfd;
		char p[20]="CL+M3+SWH+OFF";
		Py_Initialize();
		key=PyCall("mix","ledOFF","()");
		Py_Finalize();
		if(key){
			//rebspeak("好的主人，正在关闭"); 
			CLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE);
			if(write(CLfd,p,strlen(p))>10) 
				rebspeak("已经关闭，请问还有什么可以帮到您"); 
			close(CLfd);
			Jump = 1;//跳出循环。不执行图灵回复。
		}
	}
}



//帮我开风扇  M3 插座模块  随意控制
void fanON(){
	if(FAN){
		int key;
		int CLfd;
		char p[20]="CL+M3+SWH+ON";
		Py_Initialize();
		key=PyCall("mix","fanON","()");
		Py_Finalize();
		if(key){
			//rebspeak("好的主人，正在打开"); 
			CLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE);
			if(write(CLfd,p,strlen(p))>10) //成功
				rebspeak("已经打开，请问还有什么可以帮到您"); 
			close(CLfd);
			Jump = 1;//跳出循环。不执行图灵回复。
		}
	}
}
//帮我关掉风扇
void fanOFF(){
	if(FAN){
		int key;
		int CLfd;
		char p[20]="CL+M3+SWH+OFF";
		Py_Initialize();
		key=PyCall("mix","fanOFF","()");
		Py_Finalize();
		if(key){
			//rebspeak("好的主人，正在关闭"); 
			CLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE);
			if(write(CLfd,p,strlen(p))>10) 
				rebspeak("已经关闭，请问还有什么可以帮到您"); 
			close(CLfd);
			Jump = 1;//跳出循环。不执行图灵回复。
		}
	}
}
//帮我烧水
void waterON(){
	if(WATER){
		int key;
		int CLfd;
		char p[20]="CL+M3+SWH+ON";
		Py_Initialize();
		key=PyCall("mix","waterON","()");
		Py_Finalize();
		if(key){
			//rebspeak("好的主人，正在关闭"); 
			CLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE);
			if(write(CLfd,p,strlen(p))>10) 
				rebspeak("赏你一口水喝，热水壶已打开"); 
			close(CLfd);
			Jump = 1;//跳出循环。不执行图灵回复。
		}
	}
}
//module 功能模块
void moduleinit(){
	Jump=0;   //为零时  使用tuling回复。
	singsong();		
	ledON();
	ledOFF();
	 fanON();
	fanOFF();
	waterON();
}

/*************************************************************************
    > File Name: master.c
    > Author: HHH
    > Mail: 305837300@qq.com 
    > Created Time: 2018年04月01日 星期日 16时47分36秒
 ************************************************************************/
#include "module.h"
#define  STATUSTXT "/home/pi/master/status.txt"
#define  MDTXT "/home/pi/master/driverCL/MD.txt"
#define MODE 00777
//int Status;
//主动唤醒。
void wake(){
	if (ACLOCK){
		//主动唤醒播放歌曲。
		system("play /home/pi/Desktop/xsycq.mp3");
		printf("timeout to ACLOCK");
	}
}
//被动唤醒
void bewake(){
	
}
int getstatus(){
	//
	int rfd,rlen,wfd;
	char buf[7];
	//if(access(MYPIPE,F_OK)<0)
	//	mkfifo(MYPIPE,O_CREAT | O_RDWR | 00777);
	
	if((rfd=open(STATUSTXT,O_RDONLY | O_CREAT,MODE))<0){
		printf("open status error!\n");
	}
	memset(buf,0,sizeof(buf));
	rlen=read(rfd,buf,7);
	if(rlen != 0 && buf[0] == 'p') {
		printf("read the status P %s\n",buf);
		wfd=open(MDTXT,O_WRONLY | O_TRUNC,00777);
		write(wfd,"actived",7);
		close(wfd);
		close(rfd);
		return 1;
		
	}
	else {
		close(wfd);
		close(rfd);
		return 0;
	}
	
	
}

int getmode(){
	//获取模式 并启动   
	int rmodefd,rlen;
	char bufr[20],bufmd[40];
	int mode=0;
	if((rmodefd=open(MDTXT,O_RDONLY | O_CREAT,MODE))<0){printf("open status error!\n");}
	memset(bufr,0,sizeof(bufr));
	if((rlen=read(rmodefd,bufr,20))>0){
		if(bufr[0] == 'M' && bufr[1] == 'D') {
			if(bufr[3] == 'H' && bufr[4] == 'M') {mode=1;rebspeak("正在开启回家模式\n");}
			else if(bufr[3] == 'S' && bufr[4] == 'F') {mode=2;rebspeak("正在开启安全模式\n");}
			else if(bufr[3] == 'S' && bufr[4] == 'P') {mode=3;rebspeak("正在开启睡眠模式\n");}
			sprintf(bufmd,"bash /home/pi/master/shell/Mode.sh %d",mode);
			system(bufmd);
			sleep(0.01);
			
		}
	}
	system("echo > /home/pi/master/driverCL/MD.txt");//读完指令清除
	close(rmodefd);
	return mode;

	
	
}


int main(){
	system("bash /home/pi/master/shell/init.sh");
	//system("python /home/pi/master/snowboy/examples/Python/demo.py /home/pi/master/snowboy/examples/Python/HiMaster.pmdl &");
	rebspeak("你好啊，主人!\n");
	//int wfd;
	//wfd=open(STATUSTXT,O_WRONLY |O_CREAT| O_TRUNC,00777);
	//write(wfd,"actived",7);
	//close(wfd);
	int Status=1;
	while(1)
	{	
		
		
		//Status=getstatus();
		if(Status){   //1被动唤醒
			getmode();
			getchar();
			//sleep(5);
			system("bash /home/pi/master/shell/iat.sh");//录制要识别的语句
		
			Py_Initialize();
			PyCall("mix","iaten","()");
			Py_Finalize();
			//每调用一次Python程序要打开关闭python接口
		
			moduleinit();//功能模块集成初始化。
			if(Jump==1) continue;
			Py_Initialize();
			PyCall("mix","tuling","()");
			Py_Finalize();
			//system("python /home/pi/master/snowboy/examples/Python/demo.py /home/pi/master/snowboy/examples/Python/HiMaster.pmdl &");
		}
		else {
			getmode();
			//主动唤醒
			//wake();
		}
		usleep(20000);//20ms
	}
	return 0;
}

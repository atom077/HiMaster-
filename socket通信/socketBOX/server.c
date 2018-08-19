#include "client.h"

#define  STTXT "/home/pi/master/driverCL/ST.txt"
#define  DTTXT "/home/pi/master/driverCL/DT.txt"
#define  CLTXT "/home/pi/master/driverCL/CL.txt"
#define  MDTXT "/home/pi/master/driverCL/MD.txt"
#define	HOMEMODE "MD+HM"
#define MODE 00777
#define MYPORT 8086    // the port users will be connecting to


#define BACKLOG 1888    // how many pending connections queue will hold
pthread_mutex_t mutex;
int Jump=0;
int sockfd, client_fd;  // listen on sock_fd, new connection on client_fd
    struct sockaddr_in my_addr;    // my address information
    struct sockaddr_in their_addr; // connector's address information

void recfun(void *Cfd){  //接受来自  节点的ST + DT
	int fd = *(int *)Cfd;  	
	char bufrec[20];	
	int  wCLfd,wDTfd,wSTfd,wMDfd;    	//文件写入标志
	int recw;//rec len of word
	while(1){
		bzero(bufrec,20);
		if ((recw=recv(fd, &bufrec, 20, 0)) == -1){ 	perror("rec error ");	}
		
		else if (recw >0){
			
			printf("Client:%s<<%s\n",inet_ntoa(their_addr.sin_addr),bufrec);
			if(bufrec[0] == 'M'&& bufrec[1] == 'D')  //接收命令信息
			{
				sprintf(bufrec,"%s\n",bufrec);
				if ((wMDfd=open(MDTXT,O_WRONLY | O_CREAT,MODE))<0){
                    printf("open MDTXT error!\n");
					continue;
                }//打开CL文件写入的CL语句
           		if(write(wMDfd,bufrec,20)<0){printf("WRITE in CL.txt ERROR");continue;}
           		close (wMDfd);
           		Jump=1;                     //jump =1 ，不发送 服务器的MD指令 消息 ，文件中指令不会消除
				usleep(1000);
			}
			
			if(bufrec[0] == 'C'&& bufrec[1] == 'L')  //接收命令信息
			{
				sprintf(bufrec,"%s\r\n",bufrec);
				if ((wCLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE))<0){
                    printf("open CLTXT error!\n");
					continue;
                }//打开CL文件写入的CL语句
           		if(write(wCLfd,bufrec,20)<0){printf("WRITE in CL.txt ERROR");continue;}
           		close (wCLfd);
           		if(bufrec[6] == 'S'&& bufrec[7] == 'T'&& bufrec[7] == 'P'){
           			if(bufrec[10] == 'O'&& bufrec[11] == 'N')
           				system("bash /home/pi/master/shell/Mode.sh 1");//开启回家模式
           		}
           		Jump=1;                     //jump =1 ，不发送 服务器的CL指令 消息 ，文件中指令不会消除
				usleep(1000);
			}

			if(bufrec[0] == 'D'&& bufrec[1] == 'T')  //接收到数据信息
			{
				if ((wDTfd=open(DTTXT,O_WRONLY | O_CREAT,MODE))<0){
                    printf("open DTTXT error!\n");
					continue;
                }//打开DT文件写入的DT语句
           		if(write(wDTfd,bufrec,20)<0){printf("WRITE in DT.txt ERROR");continue;}
           		close (wDTfd);
           		Jump=1;                     //jump =1 ，不发送 服务器的CL指令 消息 ，文件中指令不会消除
				usleep(1000);
			}
			
			else if (bufrec[0] == 'S'&& bufrec[1] == 'T') //接收到状态信息
			{
				if ((wSTfd=open(STTXT,O_WRONLY | O_CREAT,MODE))<0){
                    printf("open DTTXT error!\n");
					continue;
                }//打开ST文件(状态)写入的ST语句
           		if(write(wSTfd,bufrec,20)<0){printf("WRITE in ST.txt ERROR");continue;}
           		close(wSTfd);
           		Jump=1;                     //jump =1 ，不发送 服务器的CL指令 消息 ，文件中指令不会消除
				usleep(1000);
			
			}	
		}	
	}
}

void sendedfun(void *Cfd){
	int fd= *(int *)Cfd;
	char rbuf[20];
	int rfd,rlen,wMDfd;

	while(1){
		if(Jump){ Jump=0 ; continue;}  //多线程同步。 
		usleep(1000);
		if ((rfd=open(CLTXT,O_RDONLY | O_CREAT,MODE))<0){
			printf("open CLTXT error!\n");
		}//读出CL文件的CL语句
		memset(rbuf,0,sizeof(rbuf));
		if((rlen=read(rfd,rbuf,20))>0){
			close(rfd);  
			sprintf(rbuf,"%s\r\n",rbuf);
			if(rbuf[0] == 'C'&& rbuf[1] == 'L') {
				if(rbuf[6] == 'S'&& rbuf[7] == 'T'&& rbuf[8] == 'P'&& rbuf[10] == 'O'&& rbuf[11] == 'N'){
						if ((wMDfd=open(MDTXT,O_WRONLY | O_CREAT,MODE))<0){
	                    printf("open MDTXT error!\n");
						continue;
	                }//打开CL文件写入的CL语句
	           		if(write(wMDfd,HOMEMODE,20)<0){printf("WRITE in CL.txt ERROR");continue;}
	           		printf("senging master HOMEMODE\n");
	           		close (wMDfd);
				}
				system("echo > /home/pi/master/driverCL/CL.txt");//读完指令清除
				if (send(fd, &rbuf, 20, 0) == -1){
	                		perror("send");
				}else printf("sending to module %s\n",rbuf);
		
			}
		}
		close(rfd);  
	}		
}

int main(void)
{
    
    int sin_size;
    int yes=1;

    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("socket");
        exit(1);
    }

    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
        perror("setsockopt");
        exit(1);
    }
    
    my_addr.sin_family = AF_INET;         // host byte order
    my_addr.sin_port = htons(MYPORT);     // short, network byte order
    my_addr.sin_addr.s_addr = INADDR_ANY; // automatically fill with my IP
    memset(&(my_addr.sin_zero), '\0', 8); // zero the rest of the struct

    if (bind(sockfd, (struct sockaddr *)&my_addr, sizeof(struct sockaddr)) == -1) {
        perror("bind");
        exit(1);
    }

    if (listen(sockfd, BACKLOG) == -1) {
        perror("listen");
        exit(1);
    }


    while(1) {  // main accept() loop
        sin_size = sizeof(struct sockaddr_in);
        if ((client_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size)) == -1) {
            perror("accept");
            continue;  //没有阻塞就退出。
        }
        
        printf("server: got connection from %s\n",inet_ntoa(their_addr.sin_addr));
        
        pthread_mutex_init(&mutex,NULL);//clock
        pthread_t rec,sended;
        
       	pthread_create(&rec,NULL,(void *)recfun,(void *)&client_fd);
       	pthread_create(&sended,NULL,(void *)sendedfun,(void *)&client_fd);
       	

    }
	close(sockfd);
        close(client_fd);
    return 0;
}

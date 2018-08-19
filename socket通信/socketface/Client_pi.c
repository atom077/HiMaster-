#include "client.h"

////////在树莓派PIface上运行，发送开门指令到M2 门模块

#define PORT 8086 // the port client will be connecting to 
#define  CLTXT "/home/pi/himasterface/driver/drCL.txt"
#define  DTTXT "/home/pi/himasterface/driver/drDT.txt"

#define MODE 00777
#define MAXDATASIZE 100 // max number of bytes we can get at once 
int sockfdw;
pthread_mutex_t mutex;
struct sockaddr_in their_addr; 
int Jump=0;

/*
 * 
 * name: recfun()
 * @param :新的线程中，循环执行获取server发出的信息并且作出判断，是CL命令
 * 			还是其他，对应的保存在操作txt文件中。
 * 
 */
void recfun(){
	char bufr[20];
    int wCLfd;
	int recw;//rec len of word
	while(1){
		if ((recw=recv(sockfdw, &bufr, 20, 0)) == -1){
                perror("rec error ");
		}
		printf("%s\n",&bufr);
		usleep(100);
		/*bzero(bufr,20);
		if ((recw=recv(sockfdw, &bufr, 20, 0)) == -1){
                perror("rec error ");
		}
		else if (recw >0&& bufr[0] == 'C'&& bufr[1] == 'L'){
		      printf("服务器:received from %s:%s\n",inet_ntoa(their_addr.sin_addr),bufr);
			  if ((wCLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE))<0){
                        printf("open CLTXT error!\n");
						continue;
                }//打开CL文件写入的CL语句
				if(write(wCLfd,bufr,20)<0) //把接收到的CL命令保存在CL.txt中
				printf("WRITE in CL.txt ERROR"); 
                Jump=1;
		        usleep(100);
		}*/
	}	
	
	
}
/*
 * 
 * name: sendedfun()
 * @param：新的线程中循环发送信息文件txt，中的信息给HIMASTER PI 
 * @return
 * 
 */
void sendedfun(){
	char rbuf[20];//CL+M2+STP+ON
	int rlen;
	int rCLfd,rDTfd;
	while(1){
		if(Jump){ Jump=0 ; continue;}
		if ((rCLfd=open(CLTXT,O_RDONLY | O_CREAT,MODE))<0){
			printf("open CLTXT error!\n"); sleep(1);continue;
		}//读出CL文件的CL语句
		memset(rbuf,0,sizeof(rbuf));
		if((rlen=read(rCLfd,rbuf,20))>0){
			close(rCLfd);  
			sprintf(rbuf,"%s\r\n",rbuf);
			if(rbuf[0] == 'C'&& rbuf[1] == 'L') {
				system("echo > /home/pi/himasterface/driver/drCL.txt");//读完指令清除
				if (send(sockfdw, &rbuf, 20, 0) == -1){
	                		perror("send");
				}else printf("sending to module %s\n",rbuf);
		
			}
			
		}
		if ((rDTfd=open(DTTXT,O_RDONLY | O_CREAT,MODE))<0){
			printf("open DTTXT error!\n"); sleep(1);continue;
		}//读出CL文件的CL语句
		memset(rbuf,0,sizeof(rbuf));
		if((rlen=read(rDTfd,rbuf,20))>0){
			close(rDTfd);  
			sprintf(rbuf,"%s\r\n",rbuf);
			if(rbuf[0] == 'D'&& rbuf[1] == 'T') {
				system("echo > /home/pi/himasterface/driver/drDT.txt");//读完指令清除
				if (send(sockfdw, &rbuf, 20, 0) == -1){
	                		perror("send");
				}else printf("sending to module %s\n",rbuf);
		
			}
			
		}
		close(rCLfd);
		usleep(100);

	}	
	
	
}

int main(int argc, char *argv[])
{
    int sockfd, numbytes;  
    char buf[MAXDATASIZE];
    struct hostent *he;
struct sockaddr_in their_addr;  

    if (argc != 2) {
        fprintf(stderr,"usage: client hostname\n");
        exit(1);
    }

    if ((he=gethostbyname(argv[1])) == NULL) {  // get the host info 
        perror("gethostbyname");
        exit(1);
    }

    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("socket");
        exit(1);
    }

    their_addr.sin_family = AF_INET;    // host byte order 
    their_addr.sin_port = htons(PORT);  // short, network byte order 
    their_addr.sin_addr = *((struct in_addr *)he->h_addr);
    memset(&(their_addr.sin_zero), '\0', 8);  // zero the rest of the struct 

    if (connect(sockfd, (struct sockaddr *)&their_addr, sizeof(struct sockaddr)) == -1) {
        perror("connect");
        exit(1);
    }
    sockfdw=sockfd;
    pthread_mutex_init(&mutex,NULL);//clock
    pthread_t rec,sended;
        
    pthread_create(&rec,NULL,(void *)recfun,NULL);
    pthread_create(&sended,NULL,(void *)sendedfun,NULL);

    while(1);//因信号
    close(sockfd);

    return 0;
}

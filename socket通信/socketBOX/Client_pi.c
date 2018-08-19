#include "client.h"

////////在树莓派上运行，用来接收服务器传来的指令 并保持本地的文件。

#define PORT 8083 // the port client will be connecting to 
#define  CLTXT "/home/pi/master/driverCL/CL.txt"
#define  STTXT "/home/pi/master/driverCL/ST.txt"
#define  DTTXT "/home/pi/master/driverCL/DT.txt"


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
		bzero(bufr,20);
		if ((recw=recv(sockfdw, &bufr, 20, 0)) == -1){
                perror("rec error ");
		}
		else if (recw >0&& bufr[0] == 'C'&& bufr[1] == 'L'){
		      printf(" 服务器%s 发来 ---》 :%s \n",inet_ntoa(their_addr.sin_addr),bufr);
			  if ((wCLfd=open(CLTXT,O_WRONLY | O_CREAT,MODE))<0){
                        printf("open CLTXT error!\n");
						continue;
                }//打开CL文件写入的CL语句
				if(write(wCLfd,bufr,20)<0) //把接收到的CL命令保存在CL.txt中
				printf("WRITE in CL.txt ERROR");
                close(wCLfd);
                Jump=1;
		        usleep(1000);
		}
	}	
}
/*
 * 
 * name: sendedfun()
 * @param：新的线程中循环发送DT.txt和ST.txt，中的信息给server
 * @return
 * 
 */
void sendedfun(){
	char bufs[20];
    int rSTfd,rDTfd;
    int rlen;
	while(1){
		if(Jump){ Jump=0 ; continue;}
        if ((rSTfd=open(STTXT,O_RDONLY | O_CREAT,MODE))<0){
            printf("open STTXT error!\n");
        }//读出ST文件的ST语句
        memset(bufs,0,sizeof(bufs));
        
        if((rlen=read(rSTfd,bufs,20))>0){
            sprintf(bufs,"%s\r\n",bufs);          // 读文件 有数据，就加换行符 判断是否发送
            close(rSTfd);        //记得关闭文件
            if( bufs[0] == 'S'&& bufs[1] == 'T') {
                system("echo > /home/pi/master/driverCL/ST.txt");//读完指令清除
                if (send(sockfdw, &bufs, 20, 0) == -1){           //把读到的ST 和 DT发送到服务器
                            perror("send");
                }else printf("ST sending to server %s\n",bufs);
        
            }
        } 

        if ((rDTfd=open(DTTXT,O_RDONLY | O_CREAT,MODE))<0){
            printf("open CLTXT error!\n");
        }//读出DT文件的DT语句
        memset(bufs,0,sizeof(bufs));
        if((rlen=read(rDTfd,bufs,20))>0){    // 读文件 有数据，就加\r\n   判断是否发送
            sprintf(bufs,"%s\r\n",bufs);
            close(rDTfd);
            if ( bufs[0] == 'D'&& bufs[1] == 'T') {
                system("echo > /home/pi/master/driverCL/DT.txt");//读完指令清除
                if (send(sockfdw, &bufs, 20, 0) == -1){              //把读到的ST 和 DT发送到服务器
                            perror("send");
                }else printf("DT sending to server %s\n",bufs);
            }
        }
        close(rSTfd);
        close(rDTfd);
	usleep(1000);
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

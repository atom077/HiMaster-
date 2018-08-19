#*************************************************************************
#    > File Name: vocie.sh
#    > Author: HHH
#    > Mail: 305837300@qq.com 
#    > Created Time: 2018年03月14日 星期三 23时57分26秒
# ************************************************************************/
#!/bin/bash
cd /home/pi/master/bin/wav
arecord -d 3 -r 16000 -c 1 -t wav -f S16_LE iflytek02.wav -D "plughw:1,0"
#cd /home/pi/master/bin
#./iat_sample

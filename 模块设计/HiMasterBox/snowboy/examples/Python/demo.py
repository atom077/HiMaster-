#!/usr/bin/env python
#encoding:utf-8
import snowboydecoder
import sys
import os
import subprocess
import signal
import mix
import time
STATUSTXT = "/home/pi/master/status.txt"

interrupted = False
def passivewake():
    snowboydecoder.play_audio_file() #被动唤醒
     #创建有名管道，写管道进程
    #if os.path.exists(MYPIPE):
    #    os.remove(MYPIPE)
    #os.mkfifo(MYPIPE) 
    wfd = os.open(STATUSTXT , os.O_NONBLOCK | os.O_RDWR)
    if wfd < 0 :
        print "open error\n"
    if os.write(wfd,"passive") == -1 :
        print "write error"
    os.close(wfd)
    os.kill(os.getpid(),signal.SIGINT)

    
    #cmd = 'bash /home/pi/master/shell/iat.sh'
    #process = subprocess.Popen(cmd,shell=True)
    #process.terminate()
    #os.system("bash /home/pi/master/shell/iat.sh")
    #mix.tuling()


def mixmain():
    mix.recording()
    mix.tuling()

def signal_handler(signal, frame):
    global interrupted
    interrupted = True


def interrupt_callback():
    global interrupted
    return interrupted

if len(sys.argv) == 1:
    print("Error: need to specify model name")
    print("Usage: python demo.py your.model")
    sys.exit(-1)

model = sys.argv[1]

# capture SIGINT signal, e.g., Ctrl+C
signal.signal(signal.SIGINT, signal_handler)

detector = snowboydecoder.HotwordDetector(model, sensitivity=0.5)
print('Listening... Press Ctrl+C to exit')

# main loop
detector.start(detected_callback=passivewake,
               interrupt_check=interrupt_callback,
               sleep_time=0.03)

detector.terminate()

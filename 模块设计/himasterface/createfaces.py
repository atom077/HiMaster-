#/usr/bin/env python
#encoding:utf-8
import os
import picamera
import picamera.array
import cv2
from time import sleep
import time
''' 在已经有的ID中顺序生成新的id '''
def getid():
    #fd=os.open("/home/pi/himasterface/user/usrID.txt",os.O_RDWR)
    #文件比较小
    fd=open("/home/pi/himasterface/user/usrID.txt",'r')
    count = len(fd.readlines())
    str = 'echo %d >> /home/pi/himasterface/user/usrID.txt' %(count+1)
    os.system(str)
    fd.close()
    return count+1

def createfaces():
    detector = cv2.CascadeClassifier('/home/pi/opencv-3.3.0/data/haarcascades/haarcascade_frontalface_default.xml')
    sampleNum = 0
    t_start = time.time()
    fps = 0
    i = 0 
    '''idfd=os.open("./user/usrID.txt",os.O_RDONLY)
    while True:         ##循环检测是否输入整型 ID
        Id = os.read(idfd,3)
        if len(Id) > 0 and Id.isdigit():
            print Id
            print 'i see you !'
            os.system('echo > /home/pi/himasterface/user/usrID.txt')
            break'''
    
    #Id = raw_input('enter your id: ')  
    with picamera.PiCamera() as camera: # Picamera初始化
        camera.resolution = (320, 240)   # picamera 拍照尺寸
        #camera.rotation = 180            #picamera倒转
        font = cv2.FONT_HERSHEY_SIMPLEX 
        sleep(1)
        with picamera.array.PiRGBArray(camera) as output:  #指定输出格式为PiRGBArray 才能给 opencv 调用
            for frame in camera.capture_continuous(output, 'bgr', use_video_port=True):
                dst = cv2.cvtColor(output.array, cv2.COLOR_BGR2GRAY)
                rgb = frame.array 
                faces = detector.detectMultiScale(dst,1.3,5)
                for ( x, y, w, h ) in faces:
                    if i == 0 :
                        Id = getid()
                        print Id
                        i+=1    #获取id  只会获取一次
                    cv2.putText(rgb,'HiMaster',(10,40), font, 1.5,(0,0,0),2,cv2.LINE_AA)
                    cv2.rectangle( rgb, ( x, y ), ( x + w, y + h ), ( 100, 255, 100 ), 2 )
                    cv2.putText( rgb, "Face No." + str( len( faces ) ), ( x, y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
                    sampleNum = sampleNum + 1
                    cv2.imwrite("dataSet/User." + str(Id) + '.' + str(sampleNum) + ".jpg", dst[y:y + h+30, x:x + w])
                if cv2.waitKey(100) & 0xFF == ord('q'):
                    os.exit(0)
                elif sampleNum > 20 :
                    cv2.destroyAllWindows()
                    camera.close()
                    return 0    # created 
                cv2.imshow("frame", rgb)
                

                output.truncate(0)

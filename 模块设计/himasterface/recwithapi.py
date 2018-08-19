#/usr/bin/env python
#encoding:utf-8
import picamera
import picamera.array
import cv2
import numpy as np
from time import sleep
import time
import os
import operator
from createfaces import createfaces
from trainner import trianning
import facecontrast
import sys
sys.path.append('./driver')
from Pilight import getlight              

import sys
sys.path.append('./driver')
from Piled import Led

import sys
sys.path.append('./driver')
from Pikey import keyscan

anguang=0 
##### 获取4位密码 返回1 验证成功  返回0 timeout或者输错
def keyin(): 
	password=[4,3,2,1] 
	keyi=[]
	i=0
	gkey1 = keyscan()
	now = time.time() 
	while True: 
		tmp = gkey1.getkey()
		if tmp :
			print tmp
			keyi.append(tmp)
			i += 1
		if i == 4 :
			print "keyin is ===>",keyi
			print " Is t eq?",cmp(keyi,password)
			return cmp(keyi,password)
		if (time.time()-now) > 20  :
			return 0 

	# li = []
	# while(True):
	# 	for i in range(0,4):
	# 		tmp = gkey1.getkey()
	# 		li.append(tmp)
	# print(li)
	

   
def recognizerfun():
	ledR = Led()
	recognizer = cv2.face.LBPHFaceRecognizer_create()
	recognizer.read('trainner/training.yml')

	cascade_path = "/home/pi/opencv-3.3.0/data/haarcascades/haarcascade_frontalface_default.xml"
	face_cascade = cv2.CascadeClassifier(cascade_path)  
	font = cv2.FONT_HERSHEY_SIMPLEX    
	
	with picamera.PiCamera() as camera: # Picamera初始化
		camera.resolution = (320, 240)   # picamera 拍照尺寸
		#camera.rotation = 180            #picamera倒转
		with picamera.array.PiRGBArray(camera) as output:  #指定输出格式为PiRGBArray 才能给 opencv 调用
			for frame in camera.capture_continuous(output, 'bgr', use_video_port=True):
				rgb = frame.array
				gray = cv2.cvtColor(rgb, cv2.COLOR_BGR2GRAY)
				faces = face_cascade.detectMultiScale(gray, 1.3, 5)
				for (x, y, w, h) in faces:  #识别到人脸就出错
					cv2.rectangle(rgb, (x - 20, y - 20), (x + w + 20, y + h + 20), (225, 0, 0), 2)

					cv2.putText(rgb,'HiMaster',(10,40), font, 1.5,(0,0,0),2,cv2.LINE_AA)
					cv2.imwrite('./user/tempFace.jpg',gray)

					#####API 精准比对   #imgB是比的对象
					f=open('./user/usrID.txt','r')
					lines=f.readlines()  #返回的是一个list  内容都带\n
					id = range(len(lines))
					for (i,j) in zip(lines,range(len(lines))) : #第一个循环后退出，即有多少行 就写多少个
						id[j] = i[:-1]         #把\n去掉	
						imgB='./dataSet/User.%d.10.jpg' % int(id[j]) #对应id的图片
						facesample=facecontrast.face_contrast('./user/tempFace.jpg',imgB)
						if facesample > 80 :
							print facesample,id[j]
							ledR.allledON()
							if ( keyin() == 0 ):
								###发送开门指令 
								print "seng to M2  ===>   CL+M2+STP+ON"
								clfd=open("./driver/drCL.txt",'w')
								clfd.write("CL+M2+STP+ON")
								clfd.close()
								ledR.flashC()  #led1闪2下
								cv2.destroyAllWindows()
								camera.close()
								return 0                        
							else :
								ledR.flash()
								cv2.destroyAllWindows()
								camera.close()
								return 1 
							break
				cv2.imshow('rgb', rgb)
				cv2.waitKey(1) 
				output.truncate(0)

if __name__ == '__main__':

	#led实例化用作反馈  gkey用作功能输入，密码输入
	led = Led()   		
	gkey = keyscan()     
	
	while True: 
		anguang=getlight()
		key=gkey.getkey()
		if key :
			anguang=getlight() #  判断是否开启闪光灯  暗光开灯并且提高曝光度提高识别率
			##########按4创建识别对象并训练#######################
			if  key == 4 :   #长按 按键四启动  create学习模式
				time.sleep(1) 
				key=gkey.getkey()  
				if  key == 4 : 
					if anguang :       #测暗光并开灯
						led.allledON()
					else :            # 正常光只会开led1
						led.LED1(1)
					#createfaces()	
					if (createfaces()  == 0) :  #启动 create（）
						trianning()         #训练
						led.flashC()        # 成功led1闪烁2次
			
			############ 按1 正常识别 开门  #################################                
			elif  key == 1 :
				if anguang :       #测暗光并开灯
					led.allledON()
				else :            # 正常光只会开led1
					led.LED1(1)
				recognizerfun()
		led.allledOFF()      #把所有灯关掉 

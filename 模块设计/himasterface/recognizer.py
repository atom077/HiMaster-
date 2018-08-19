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
	password=['4','3','2','1'] 
	keyi=[]
	i=0
	gkey1 = keyscan()
	now = time.time() 
	while True: 
		tmp = gkey1.getkey()
		if tmp :
			keyi[i] = tmp
			i += 1
		if i == 3 :
			return operator.eq(keyi,password)
		if (time.time()-now) > 20  :
			return 0 

   
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
					img_id, conf = recognizer.predict(gray[y:y+h,x:x+w]) #获取当前人脸的数据与让识别器做出预测
					#recognizer.predict为预测函数，putText则是在图片上添加文字
					if conf > 50:  #大于50成功检测  
						if img_id == 11:
							img_id = 'HHH'
						elif img_id == 12:
							img_id = 'DILIREBA'
						ledR.flashR() #LED1  LED2 循环点亮 要输入密码 
						#这里开一个训练接口 mastkey状态 
						#
						#密码输入pass=keyin()   pass==1 输入成功 输入失败     
						if keyin() :
							###发送开门指令
							clfd=os.open("./driver/drCL.txt",os.O_TRUNC|os.O_CREAT|os.O_RDONLY)
							s.write(clfd,"CL+M2+STEP+ON",20)
							ledR.flashC()  #led1闪2下
							return 0                        
						else :
							ledR.flash()  

					else:
						img_id = "i dont know U"
					cv2.putText(rgb,'HiMaster',(10,40), font, 1.5,(0,0,0),2,cv2.LINE_AA)
					cv2.putText(rgb, str(img_id), (x, y + h), font, 0.55, (0, 255, 0), 1)
				cv2.imshow('rgb', rgb)
				if cv2.waitKey(1) & 0xFF == ord('q'):
					os._exit(0)  #释放资源
				output.truncate(0)

if __name__ == '__main__':
	#led实例化用作反馈  gkey用作功能输入，密码输入
	#recognizerfun()
	led = Led()   		
	gkey = keyscan()     
	anguang=getlight()
	while True: 
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
			flash.allledOFF()      #把所有灯关掉 

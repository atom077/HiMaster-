#/usr/bin/env python
#encoding:utf-8
import RPi.GPIO as GPIO
import time
class Led:
	"""docstring for led"""
	led1 = 18      #棕色         
	led2 = 17		#白	
	led3 = 27		# 灰
	led4 = 22       #紫
	def __init__(self):
		GPIO.setmode(GPIO.BCM)
		GPIO.setup(self.led1, GPIO.OUT)  #在类里使用自己定义的属性要加self
		GPIO.setup(self.led2, GPIO.OUT)
		GPIO.setup(self.led3, GPIO.OUT)
		GPIO.setup(self.led4, GPIO.OUT)

	def LED1(self,CL):
		if CL == 1:
			GPIO.output(self.led1, GPIO.HIGH)
		else : 
			GPIO.output(self.led1, GPIO.LOW)

	def LED2(self,CL):
		if CL == 1:
			GPIO.output(self.led2, GPIO.HIGH)
		else :
			GPIO.output(self.led2, GPIO.LOW)

	def LED3(self,CL):
		if CL == 1:
			GPIO.output(self.led3, GPIO.HIGH)
		else :
			GPIO.output(self.led3, GPIO.LOW)

	def LED4(self,CL):
		if CL == 1:
			GPIO.output(self.led4, GPIO.HIGH)
		else :
			GPIO.output(self.led4, GPIO.LOW)

	def allledON(self) :
		GPIO.output(self.led1, GPIO.HIGH)
		GPIO.output(self.led2, GPIO.HIGH)
		GPIO.output(self.led3, GPIO.HIGH)
		GPIO.output(self.led4, GPIO.HIGH)
		
	def allledOFF(self):
		GPIO.output(self.led1, GPIO.LOW)
		GPIO.output(self.led2, GPIO.LOW)
		GPIO.output(self.led3, GPIO.LOW)
		GPIO.output(self.led4, GPIO.LOW)
	
	def flashC(self)  : #
		for x in range(1,3):
			GPIO.output(self.led1, GPIO.HIGH)
			time.sleep(0.5)
			GPIO.output(self.led1, GPIO.LOW)
			time.sleep(0.5)

	def flashR(self) :
		for x in range(1,3):
			GPIO.output(self.led1, GPIO.HIGH)
			time.sleep(0.5)
			GPIO.output(self.led2, GPIO.HIGH)
			time.sleep(0.5)
			GPIO.output(self.led2, GPIO.LOW)
			time.sleep(0.5)
			GPIO.output(self.led1, GPIO.LOW)
			time.sleep(0.5)	
	def flash(self) :
		for x in range(1,3):
			GPIO.output(self.led1, GPIO.HIGH)
			GPIO.output(self.led2, GPIO.HIGH)
			GPIO.output(self.led3, GPIO.HIGH)
			GPIO.output(self.led4, GPIO.HIGH)
			time.sleep(0.5)
			GPIO.output(self.led1, GPIO.LOW)
			GPIO.output(self.led2, GPIO.LOW)
			GPIO.output(self.led3, GPIO.LOW)
			GPIO.output(self.led4, GPIO.LOW)			
			time.sleep(0.5)		

'''if __name__ == '__main__':
	flash = Led()   #新建Led实例
	flash.LED1(1)
	time.sleep(5) 
	flash.LED1(0) '''
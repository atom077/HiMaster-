#/usr/bin/env python
#encoding:utf-8
import RPi.GPIO as GPIO
import time

class keyscan():
	"""get the keyscan for keyin"""
	key1 = 5
	key2 = 6 
	key3 = 13
	key4 = 19
	def __init__(self):
		GPIO.setmode(GPIO.BCM)
		# Setup light sensor pin status  先保证全部为上拉输入
		GPIO.setup(self.key1, GPIO.IN,GPIO.PUD_UP)  #在类里使用自己定义的属性要加self
		GPIO.setup(self.key2, GPIO.IN,GPIO.PUD_UP)
		GPIO.setup(self.key3, GPIO.IN,GPIO.PUD_UP)
		GPIO.setup(self.key4, GPIO.IN,GPIO.PUD_UP)

	def getkey(self):
		if not (GPIO.input(self.key1) and GPIO.input(self.key2) and  GPIO.input(self.key3) and GPIO.input(self.key4)):
			time.sleep(0.1)
			if not GPIO.input(self.key1) :
				return 1
			if not GPIO.input(self.key2) :
				return 2
			if not GPIO.input(self.key3) :
				return 3
			if not GPIO.input(self.key4) :
				return 4
		return 0 
'''
if __name__ == '__main__':
	gkey=keyscan()
	while  True :
		key=gkey.getkey()
		if key :
			print key'''



	

		
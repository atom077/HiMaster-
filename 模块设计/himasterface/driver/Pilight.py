#/usr/bin/env python
#encoding:utf-8
import RPi.GPIO as GPIO
import time
import random 

##v= 1   not light
def getlight ():
	light = 4  # GPIO Pin 7    数据线 = 绿色  地 =黄   vcc = 橙

	GPIO.setmode(GPIO.BCM)

	# Setup light sensor pin status
	GPIO.setup(light, GPIO.OUT)
	GPIO.output(light, GPIO.LOW)
	time.sleep(0.5)
	GPIO.setup(light, GPIO.IN)
	#getlight
	
	v=GPIO.input(light)
	if v:
		lightdata=random.randint(35,38)
	else :
		lightdata= random.randint(75,80)
	fd=open('/home/pi/himasterface/driver/drDT.txt','w')
	bufw='DT+P2+LIT+%d\n'%lightdata
	print bufw
	fd.write(bufw)
	fd.close
	return v


if __name__ == '__main__':
	while True:
		if getlight ():
			print "not light"
		else :
			print "have light"
		time.sleep(1)

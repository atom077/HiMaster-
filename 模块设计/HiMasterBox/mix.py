#!/usr/bin/env python
#encoding:utf-8

import sys
import json
import requests
import base64
import os
import wave
import urllib
from pydub import AudioSegment 
import io
import time
#import sys
#sys.path.append('home/pi/master/snowboy/examples/Python/')
#from Python import demo.py



class BaiduRest:
    def __init__(self, cu_id, api_key, api_secert):
        
        self.token_url = "https://openapi.baidu.com/oauth/2.0/token"
        #语音合成的resturl
        self.getvoice_url = "http://tsn.baidu.com/text2audio"
        # 语音识别的resturl
        self.upvoice_url = 'http://vop.baidu.com/server_api'
        self.cu_id = cu_id
        self.getToken(api_key, api_secert)
        return

    def getToken(self, api_key, api_secert):
        # 1.获取token
        data={'grant_type':'client_credentials','client_id':api_key,'client_secret':api_secert}
        r=requests.post(self.token_url,data=data)
        Token=json.loads(r.text)
        self.token_str = Token['access_token']


    def getVoice(self, text, filename):
        # 2. 向Rest接口提交数据
        data={'tex':text,'lan':'zh','cuid':self.cu_id,'ctp':1,'tok':self.token_str}
        r=requests.post(self.getvoice_url,data=data,stream=True)
        voice_fp = open(filename,'wb')
        voice_fp.write(r.raw.read())
        # for chunk in r.iter_content(chunk_size=1024):
            # voice_fp.write(chunk)
        voice_fp.close()


    def getText(self, filename):
        # 2. 向Rest接口提交数据
        data = {"format":"wav","rate":16000, "channel":1,"token":self.token_str,"cuid":self.cu_id,"lan":"zh"}
        # 语音的一些参数
        wav_fp = open(filename,'rb')
        voice_data = wav_fp.read()
        data['len'] = len(voice_data)
        data['speech'] = base64.b64encode(voice_data).decode('utf-8')
        post_data = json.dumps(data)
        r=requests.post(self.upvoice_url,data=bytes(post_data).encode("utf-8"))
        # 3.处理返回数据
        result= json.loads(r.text)
        print '>>>>>>>>>>>>'+result["result"][0]
        return result["result"][0]

    def ConvertToWav(self,filename,wavfilename):
        #先从本地获取mp3的bytestring作为数据样本
        fp=open("out.mp3",'rb')
        data=fp.read()
        fp.close()
        #主要部分
        aud=io.BytesIO(data)
        sound=AudioSegment.from_file(aud,format='mp3')
        raw_data = sound._data
        #写入到文件，验证结果是否正确。
        l=len(raw_data)
        f=wave.open(wavfilename,'wb')
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(16000)
        f.setnframes(l)
        f.writeframes(raw_data)
        f.close()
        return wavfilename

######KMP 字符串匹配##########
#KMP  
def kmp_match(s, p):
    m = len(s); n = len(p)  
    cur = 0#起始指针cur  
    table = partial_table(p)  
    while cur<=m-n:  
        for i in range(n):  
            if s[i+cur]!=p[i]:  
                cur += max(i - table[i-1], 1)#有了部分匹配表,我们不只是单纯的1位1位往右移,可以一次移动多位  
                break  
        else:  
            return True  
    return False  
  
#部分匹配表  
def partial_table(p):  
    '''''partial_table("ABCDABD") -> [0, 0, 0, 0, 1, 2, 0]'''  
    prefix = set()  
    postfix = set()  
    ret = [0]  
    for i in range(1,len(p)):  
        prefix.add(p[:i])  
        postfix = {p[j:i+1] for j in range(1,i+1)}  
        ret.append(len((prefix&postfix or {''}).pop()))  
    return ret  


######创建bdr对象
#api_key和api_secert 自行编写
api_key = "e7TGIdGOWE65PTbAwschxmc1" 
api_secert = "64e7d1ff79ad44c8039ca1722aee9062"
# 初始化
bdr = BaiduRest("test_python", api_key, api_secert)


#######识别out.mp3返回text
def iaten():
    text=bdr.getText("/home/pi/master/bin/wav/iflytek02.wav")
     #写入识别到的内容 
    reload(sys)
    sys.setdefaultencoding('utf-8')
    f=open('/home/pi/master/iat.txt','w')
    f.write(text)
    f.close()
    
    



#######合成语音并生成out.mp3
def mixen():
    print 'mixing................\n'
    #读取要说的内容 
    f=io.open('/home/pi/master/rebs.txt','r',encoding='utf-8')
    s=f.readlines()
    f.close()
      
    #api_key和api_secert 自行编写
    #api_key = "e7TGIdGOWE65PTbAwschxmc1" 
    #api_secert = "64e7d1ff79ad44c8039ca1722aee9062"
    # 初始化
    #bdr = BaiduRest("test_python", api_key, api_secert)
    # 将字符串语音合成并保存为out.mp3
    bdr.getVoice(s, "out.mp3")
        # 识别test.wav语音内容并显示
        # print(bdr.getText(bdr.ConvertToWav("out.mp3","test.wav")))
    #print time.time()-start
    os.system("play /home/pi/master/out.mp3");


#####图灵#
key = 'f57ffd3a115840378f5b8bd6560d91a8'#单引号里写你注册的的图灵机器人key
def tuling():
    print 'tuling...'
     #读取要说的内容 
    f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
    info = f.read()
    f.close()
     ###生成url
    url = 'http://www.tuling123.com/openapi/api?key='+key+'&info='+info
    res = requests.get(url)#得到网页HTML代码
    res.encoding = 'utf-8'#防止中文乱码
    jd = json.loads(res.text)#将得到的json格式的信息转换为Python的字典格式
    print('\nmaster: '+jd['text'])#输出结果
   
    #写入要说的内容 
    reload(sys)
    sys.setdefaultencoding('utf-8')
    f=open('/home/pi/master/rebs.txt','w')
    rs=f.write(jd['text'])
    f.close()
    mixen()#合成


#####唱歌#
def singsong():
    p = u"听歌"
    f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
    s = f.read()
    f.close()
    return kmp_match(s, p)
     


    
#######ledON
def  ledON():
	on = u"开灯"
	f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
	s = f.read()
	f.close()	
	return kmp_match(s, on)
	
#######ledOFF
def  ledOFF():
	off = u"关灯"
	f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
	s = f.read()
	f.close()	
	return kmp_match(s, off)
		
	

#######fanON
def  fanON():
    on = u"开风扇"
    f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
    s = f.read()
    f.close()   
    return kmp_match(s, on)
    
#######fanOFF
def  fanOFF():
    off = u"关掉风扇"
    f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
    s = f.read()
    f.close()   
    return kmp_match(s, off)
        
	
#######fanON
def  waterON():
    water = u"帮我烧水"
    f=io.open('/home/pi/master/iat.txt','r',encoding='utf-8')
    s = f.read()
    f.close()   
    return kmp_match(s, water)
    

#if __name__ == "__main__":
#    #start=time.time()
#    iaten()
#    tuling()



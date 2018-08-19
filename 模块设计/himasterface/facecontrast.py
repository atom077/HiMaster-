#!/usr/bin/env python
# encoding=utf-8

import time
import random
import base64
import hashlib
import requests
import urllib2
import urllib
import os
import numpy as np
import json
#from Crypto.Cipher import AES
#from flask import Flask, request
# from urllib.parse import urlencode  # python3版本的导入方式
#from PIL import Image
#from werkzeug.utils import secure_filename  # 使用这个是为了确保filename是安全的

#@app.route('/face_contrast/', methods=['GET', 'POST'])
def face_contrast(imga,imgb):
    
    url = 'https://api.ai.qq.com/fcgi-bin/face/face_facecompare'
    '''组织接口请求的参数形式，并且计算sign接口的鉴权信息，
    最终返回接口请求所需要的参数字典'''
    with open(imga, 'rb') as fa:
        imga_base64 = base64.b64encode(fa.read())
    with open(imgb, 'rb') as fb:
        imgb_base64 = base64.b64encode(fb.read())
    
    params = {
        'app_id': '1106967287',
        'image_a': imga_base64,
        'image_b': imgb_base64,
        'time_stamp': str(int(time.time())),
        'nonce_str': random_str(),
    }
    sort_dict = sorted(params.items(), key=lambda item: item[0], reverse=False)
    sort_dict.append(('app_key', 'FBwk1lgSPto2u7St'))  # 添加app_key
    rawtext = urllib.urlencode(sort_dict).encode()  # URL编码
    sha = hashlib.md5()  # 进行md5运算
    sha.update(rawtext)
    md5text = sha.hexdigest().upper()  # 计算出sign签名,接口鉴权
    params['sign'] = md5text
    #return params
    #params = get_parame(imga,imgb)
    
    response = requests.post(url, params)
    #print(response.text)
    #print(json.loads(response.text)['data']['similarity'])
    # print(response.text.data['similarity'])
    return json.loads(response.text)['data']['similarity']


def random_str():
    '''得到随机字符串'''
    str = 'abcdefghijklmnopqrstuvwxyz'
    r = ''
    for i in range(15):
        index = random.randint(0, 25)
        r += str[index]
    return r


def base64_of_image(img_name):
    '''获取原始图片的base64编码数据'''
    with open(img_name, 'rb') as f:
        content = f.read()
    return base64.b64encode(content)

#if __name__ == '__main__':
    #face_contrast("./dataSet/User.11.21.jpg","./dataSet/User.11.1.jpg")
    f=open('./user/usrID.txt','r')
    lines=f.readlines()  #返回的是一个list  内容都带\n
    id = range(len(lines))

    print zip(lines,range(len(lines)))
    for (i,j) in zip(lines,range(len(lines))):   #第一个循环后退出，即有多少行 就写多少个
        id[j] = i[:-1]         #把\n去掉

    for i in range(len(id)) :
        print "id:",id[i]
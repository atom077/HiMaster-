# coding: utf-8
#####  t时间
#####   w------事务（ｗｏｒｋ）０　无事务　　　１开门　２　开灯　３．......

import random
import math
import numpy as np
import pylab as pl
from matplotlib.pyplot import *
import os
max = 500

def gettw():
    di = []
    for t in range(0,max):
        w=random.randint(0,3)
        di.append(w)
    for i in range(len(di)):
        print i, di[i]
    return di

def opendoor(di):
    odoor=[]
    for i in range(len(di)):
        if di[i] == 1:
            odoor.append(i)
        else :
            odoor.append(0)
    print odoor
    return odoor

def creatdata():
    train=[]
    list=[int(max*3/10),int(max*6/10),int(max*9/10)]
    os.system('echo > train') #清文件
    for i in range(0,max):
        number=0
        if (i >=(list[0]-max/10)) and (i <(list[0]+max/10)) :
            number=random.randint(4,9)
        elif (i >=(list[1]-max/10)) and (i <(list[1]+max/10)) :
            number=random.randint(4,9)
        elif (i >=(list[2]-max/10)) and (i <(list[2]+max/10)) :
            number=random.randint(4,9)
        else:
            number=random.randint(0,2)
        train.append(number)
    #for i in range(0,100):
    #    train.append(0)
    fd=open('train','w')
    fd.write(str(train))
    fd.close()

# 获取列表的第一个元素
def takeFirst(elem):
    return elem[0]

# 获取列表的第二个元素
def takeSecond(elem):
    return elem[1]

def seletclock(train):
    clock=max/5           # 范围
    tNUM=3             # 想要选择提取的时间段个数
    top=[]             # top是排名前3多样本的时间段开头
    timelen=[] 			#时间段容器
    for i in range(0,max-clock+1):
        sum=0           #计数器
        for x in range(0,clock):    #x+i是时间点索引
            sum=sum+train[x+i]
        tuple=(x+i,sum)
        timelen.append(tuple)         #得到时间段内该事务样本总数，然后时间容器左移1 ，循环90次
        
    print "timelen----->",timelen
    timelen.sort(key=takeSecond,reverse=True)
    print timelen
    # for j in range(len(timelen)):    #遍历的到前三个数据 存在top中
    #     if train[j]==timelen[0] or train[j]==timelen[1] or train[j]==timelen[2]:
    #         top.append(j)
    #         print "j",j
    rang=0
    for t in range(0,len(timelen)):
        
        if len(top)==3:
            break
        if t==0:
            top.append(timelen[t])
            rang=takeFirst(timelen[t])
        if (takeFirst(timelen[t])-rang)>50 :
            rang=takeFirst(timelen[t])
            top.append(timelen[t])
    return top 

def predict(top,train):  
    predictlist=[]    #预测时间	求出范围内，最大可能的时间点
    countY=[]
    for pre in top:
        # for x in range(0,max/10):
        #     if NO1 < train[pre+x]:         #取出最大事务样本的时间点
        #         NO1=train[pre+x]
        #         timepoint=pre+x       
        predictlist.append(takeFirst(pre))
        countY.append(takeSecond(pre))
    print predictlist,countY
    return predictlist,countY

def  trianner(odoor) : #训练 每过1400分钟，对模型训练一次
    train=[]
    fd=open('train','r')
    buf=fd.read()   
    buf = buf.replace(', ','')
    buf = buf.strip(']')
    buf = buf.strip('[')                #读出来是string 所以要重新填充到list做处理
    fd.close()                          #也可以直接写string输出逻辑
    fd=open('train','w')
    for i in range(len(buf)):
        train.append(int(buf[i]))
    
    for i in range(len(odoor)) :
        if odoor[i] > 0 :
            print "opendoor time is ",odoor[i]
            train[i]=train[i]+1 
    print len(train)
    os.system('echo > train') #清文件  
    fd.write(str(train))     #写入训练文件
    print "开门事件训练完成"
    '''for i in train:
        fd.write(str(i))
    print "训练完成"'''
    fd.close()
    return train


#画图
def draw(C):
    colValue = ['r', 'y', 'g', 'b', 'c', 'k', 'm'] 
    w=['none','opendoor','openfan','openled']       
    none_X = []    #x坐标列表
    none_Y = []    #y坐标列表
    opendoor_X = []    #x坐标列表
    opendoor_Y = []    #y坐标列表
    openfan_X = []    #x坐标列表
    openfan_Y = []    #y坐标列表
    openled_X = []    #x坐标列表
    openled_Y = []    #y坐标列表
    #for x in range(len(w)):
    #    pl.scatter( label=w[x])
    for i in range(len(C)):
        '''if C[i]==0:
            none_X.append(i)
            none_Y.append(C[i])
            pl.scatter(none_X, none_Y, marker='x', color=colValue[C[i]])'''
        if C[i]==1:
            opendoor_X.append(i)
            opendoor_Y.append(C[i])
            pl.scatter(opendoor_X, opendoor_Y, marker='x', color=colValue[C[i]],label=w[1])
        '''if C[i]==2:
            openfan_X.append(i)
            openfan_Y.append(C[i])
            pl.scatter(openfan_X, openfan_Y, marker='x', color=colValue[C[i]])'''
        '''if C[i]==3:
            openled_X.append(i)
            openled_Y.append(C[i])
            pl.scatter(openled_X, openled_Y, marker='x', color=colValue[C[i]])'''
        

    pl.legend(loc='upper right')
    pl.show()

def drawhist(L,predictlist,countY):    #画出直方图
    x_pos=np.arange(500) 
    x=[0,0]
    pl.bar(x_pos,L,align = 'center',alpha = 1)
    # for x in range(0,3):
    #         pl.bar(x,countY[x],facecolor = 'yellowgreen',align = 'center',alpha = 1)
    for i in predictlist:
        x[0]=int(i)
        x[1]=int(i)
        y=[0,15]
        pl.plot(x,y,'r')
        x[0]=int(i)-max/5
        x[1]=int(i)-max/5
        y=[0,15]
        pl.plot(x,y,'r')
    # x[0]=0
    # x[1]=0
    # y=[0,15]
    # pl.plot(x,y,'w')
    pl.xlabel("time")
    pl.ylabel("count")
    pl.title('**HiMaster** opendoor trianner')
    pl.show()

creatdata()                #模拟生成训练好的100天的开门动作样本 
resoult=[]
di=gettw()
odoor=opendoor(di)
resoult=trianner(odoor)
top=seletclock(resoult)        #训练完后的结果，让选择器选择 
predictlist,countY=predict(top,resoult)
drawhist(resoult,predictlist,countY)        #画出 并标记结果


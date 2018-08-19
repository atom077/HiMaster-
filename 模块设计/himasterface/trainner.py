#/usr/bin/env python
#encoding:utf-8
import cv2
import os
import numpy as np
from PIL import Image

# recognizer = cv2.createLBPHFaceRecognizer()
detector = cv2.CascadeClassifier("/home/pi/opencv-3.3.0/data/haarcascades/haarcascade_frontalface_default.xml")
recognizer = cv2.face.LBPHFaceRecognizer_create()


def get_images_and_labels(path):   #获取素材并 转换成np数组写入label
    image_paths = [os.path.join(path, f) for f in os.listdir(path)]
    face_samples = []
    ids = []

    for image_path in image_paths:
        image = Image.open(image_path).convert('L')
        image_np = np.array(image, 'uint8')
        if os.path.split(image_path)[-1].split(".")[-1] != 'jpg':
            continue
        image_id = int(os.path.split(image_path)[-1].split(".")[1])
        faces = detector.detectMultiScale(image_np)
        for (x, y, w, h) in faces:
            face_samples.append(image_np[y:y + h, x:x + w])
            ids.append(image_id)  #提取出素材ID

    return face_samples, ids


def trianning():
    faces, Ids = get_images_and_labels('dataSet')
    recognizer.train(faces, np.array(Ids))
    recognizer.write('trainner/training.yml')
    return 0 #训练成功
    '''opencv3.2版本使用是save和load方法
    opencv3.3版本使用是write和read方法'''

# -*- coding: utf-8 -*-
"""
Created on Tue Dec  5 20:40:04 2017

@author: Gu Yi
"""
"""
第一步先利用训练数据训练好分类器
"""
import csv
import collections
import os
import numpy as np
import random
import jieba
import jieba.analyse

def count_words(words,string):#返回word在string出现的频数
    str1=string.split(' ')
    return collections.Counter(str1)[words]
def len_str(string):#分词后的字符串返回词数
    count=1
    for c in string:
        if c==' ':
            count+=1
    return count

csv_data=open('split_data_num_label.csv','r',encoding='utf-8')
#打开训练集
keywords=csv.reader(open('svm_keywords.csv','r',encoding='utf-8'))
#打开TFIDF跑出的关键词
attribute=[]
for line in keywords:
    attribute.append(line[0])
i=0
j=0
length_data=len(csv_data.readlines())

data_vector=np.zeros([length_data,len(attribute)])
label=np.zeros(length_data)
csv_data.close()
data=csv.reader(open('split_data_num_label.csv','r',encoding='utf-8'))

for line_csv in data:
    text=line_csv[0]
    length=int(line_csv[1])
    label[i]=int(line_csv[2])    
    for word in attribute:
        data_vector[i][j]=float(count_words(word,text))*100/length#          
        j+=1
    i+=1
    j=0
#文本向量保存在data_vector中
#分解训练集测试集

x_train=data_vector
y_train=label
#x_test=np.zeros([length_data-num_of_train,len(attribute)])
#y_test=np.zeros(length_data-num_of_train)


"""
训练出SVM模型
"""
from sklearn.svm import SVC   
svclf = SVC(kernel = 'linear') 
svclf.fit(x_train,y_train)  
print("分类器训练完成")
#preds = svclf.predict(x_test);  
#num = 0
#preds = preds.tolist()
#for i,pred in enumerate(preds):
#    if int(pred) == int(y_test[i]):
#        num += 1
#print(svclf.n_support_)
#print(data_vector[0].reshape(123,1).shape)
#print(svclf.coef_.shape)
#for i in range(length_data):
#    print(i,np.dot(svclf.coef_,data_vector[i].reshape(123,1)))
#print(svclf.intercept_)
#distance=svclf.decision_function(data_vector)
#index=0
#for d in distance:
#    if d>0:
#        print(index)
#    index+=1
#
#
#
#print('precision_score:' + str(float(num) / len(preds)))
"""
从数据库导入待分类文本
"""
import pymysql
import sys
print(sys.getdefaultencoding())

#打开数据库连接
db = pymysql.connect(host="114.212.82.150",port=3306,user="root",passwd='iipconfig',db="zhijianju1",charset="utf8")
#使用cursor()方法获取游标对象
cursor= db.cursor()
#使用execute() 方法执行SQL查询
cursor.execute("select version()")
#使用fetchone()方法获取单条数据
data1=cursor.fetchone()
print("DB version is : %s" %data1)
# SQL 查询语句
sql = "SELECT id,content FROM news_data \
       WHERE id > '%d'" % (1)
try:
   # 执行SQL语句
   cursor.execute(sql)
   # 获取所有记录列表
   results = cursor.fetchall()
except:
   print ("Error: unable to fecth data")
#将表存在results中
for row in results:
      id = row[0]
      content = row[1]
      #对content分词
      seg_list = jieba.cut(content, cut_all=False)
      split_content=" ".join(seg_list)
      len_content=len_str(split_content)
      #将content转化为向量
      content_vector=np.zeros([1,len(attribute)])
      i=0
      for word in attribute:
          content_vector[0][i]=float(count_words(word,split_content))*100/len_content#          
          i+=1
      x_test=content_vector
      preds = svclf.predict(x_test);  
      preds = preds.tolist()
      pred_label=int(preds[0])
      if(pred_label==1):
          print(id,content,pred_label)
      #将结果写入数据库
      update="UPDATE news_data SET is_injure_news=%d WHERE id=%d"%(pred_label,id)
      try:
          cursor.execute(update)
          db.commit()
      except:
          db.rollback()
          print("数据库修改错误")
      
     
db.close()
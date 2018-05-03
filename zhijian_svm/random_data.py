# -*- coding: utf-8 -*-
import random
import pymysql
import csv
import sys


db = pymysql.connect(host="localhost",port=3306,user="root",passwd='root',db="zhijian",charset="utf8")
cursor= db.cursor()
sql = "select id from news_data_unused"
try:
   # 执行SQL语句
   cursor.execute(sql)
   # 获取所有记录列表
   data = cursor.fetchall()
except:
   print ("Error: unable to fecth data")
data = list(data)
random.shuffle(data)
data = data[:1000]
content=[]
for id in data:
    sql = "select title,content from news_data_unused WHERE id = %s " % id
    try:
        # 执行SQL语句
        cursor.execute(sql)
        # 获取所有记录列表
        result = cursor.fetchone()
        content.append(result)
    except:
        print("Error: unable to fecth data")
print(len(content))
with open("test1.csv","w",encoding='utf8',newline='') as csvfile:
    writer = csv.writer(csvfile)
    #先写入columns_name
    writer.writerow(["标题","内容","是否是伤害事件"])
    #写入多行用writerows
    writer.writerows(content)
cursor.close()
db.close()


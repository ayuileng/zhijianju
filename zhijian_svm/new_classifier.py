# -*- coding: utf-8 -*-
"""
Created on Sun Jun 17 19:52:44 2018

@author: Gu Yi
"""

# 特征选择
import pandas as pd
import numpy as np
import jieba as jb


def is_ustr(in_str):
    out_str = ''
    for i in range(len(in_str)):
        if is_uchar(in_str[i]):
            out_str = out_str + in_str[i]
        else:
            out_str = out_str + ''
    return out_str


def is_uchar(uchar):
    """判断一个unicode是否是汉字"""
    if uchar >= u'\u4e00' and uchar <= u'\u9fa5':
        return True
    """判断一个unicode是否是数字"""
    if uchar >= u'\u0030' and uchar <= u'\u0039':
        return False
    """判断一个unicode是否是英文字母"""
    if (uchar >= u'\u0041' and uchar <= u'\u005a') or (uchar >= u'\u0061' and uchar <= u'\u007a'):
        return False
    if uchar in ('-', ',', '，', '。', '.', '>', '?', ' '):
        return False
    return False


data = pd.read_csv("res1.csv", sep='\|\@\|', encoding='utf-8', header=None)
data["context"] = ""
data["split_context"] = ""

str_inj = ""
str_noinj = ""
for i in range(len(data)):
    mystr = data.loc[i, 0] + data.loc[i, 1]
    data.loc[i, "context"] = is_ustr(mystr)
    seg_list = jb.cut(is_ustr(mystr))
    data.loc[i, "split_context"] = " ".join(seg_list)
    if (data.loc[i, 2] == 0):
        str_noinj = str_noinj + data.loc[i, "split_context"]
    else:
        str_inj = str_inj + data.loc[i, "split_context"]
corpus = [str_noinj, str_inj]
from sklearn import feature_extraction
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer

vectorizer = CountVectorizer(min_df=1)
transformer = TfidfTransformer()
tfidf = transformer.fit_transform(vectorizer.fit_transform(corpus))

word = vectorizer.get_feature_names()
weight = tfidf.toarray()

keywords = []
for j in range(len(word)):
    if (weight[1][j] - weight[0][j] > 0.005):
        print(word[j], weight[1][j] - weight[0][j])
        keywords.append(word[j])

train_data = pd.DataFrame()
for i in range(len(keywords)):
    train_data["word" + str(i)] = 0
train_data["sentiments"] = 0

from snownlp import SnowNLP

for i in range(len(data)):
    context_str = data.loc[i, "split_context"]
    context_list = context_str.split()
    s = SnowNLP(data.loc[i, 0])
    train_data.loc[i, "sentiments"] = s.sentiments
    for j in range(len(keywords)):
        train_data.loc[i, "word" + str(j)] = context_list.count(keywords[j])

X = train_data.values
y = data[2].values
import xgboost as xgb
from sklearn.ensemble import RandomForestClassifier
from sklearn import cross_validation

# X_train,X_test,y_train,y_test=cross_validation.train_test_split(X,y,test_size=0.3,random_state=2)
#
# rfc = RandomForestClassifier()
# xgbc=xgb.XGBClassifier()
# from sklearn.model_selection import cross_val_score
#
# print(cross_val_score(rfc, X_train, y_train, cv=5).mean())
# print(cross_val_score(xgbc, X_train, y_train, cv=5).mean())
xgbclf = xgb.XGBClassifier()
xgbclf.fit(X, y)
print("分类器训练完成")
"""
从数据库导入待分类文本
"""
import pymysql
import sys

print(sys.getdefaultencoding())

# 打开数据库连接
db = pymysql.connect(host="114.212.82.101", port=3306, user="root", passwd='root', db="zhijianju", charset="utf8")
# 使用cursor()方法获取游标对象
cursor = db.cursor()
# 使用execute() 方法执行SQL查询
cursor.execute("select version()")
# 使用fetchone()方法获取单条数据
data1 = cursor.fetchone()
print("DB version is : %s" % data1)
# SQL 查询语句
sql = "SELECT * FROM news_data \
       WHERE id > '%d'" % (1)
try:
    # 执行SQL语句
    cursor.execute(sql)
    # 获取所有记录列表
    results = cursor.fetchall()
except:
    print("Error: unable to fecth data")

for row in results:
    id = row[0]
    title = row[1]
    content = row[2]
    # 对content分词
    mystr = title + content
    seg_list = jb.cut(mystr, cut_all=False)
    split_content = " ".join(seg_list)
    mystr_list = split_content.split()
    # 将content转化为向量
    content_vector = np.zeros([1, len(keywords) + 1])
    for i in range(len(keywords)):
        content_vector[0][i] = mystr_list.count(keywords[i])
    s = SnowNLP(title)
    content_vector[0][len(keywords)] = s.sentiments

    x_test = content_vector
    preds = xgbclf.predict(x_test)
    preds = preds.tolist()
    pred_label = int(preds[0])
    if (pred_label == 1):
        print(id, title, pred_label)
    # 将结果写入数据库
    update = "UPDATE news_data SET is_injure_news=%d WHERE id=%d" % (pred_label, id)
    try:
        cursor.execute(update)
        db.commit()
    except:
        db.rollback()
        print("数据库修改错误")

db.close()

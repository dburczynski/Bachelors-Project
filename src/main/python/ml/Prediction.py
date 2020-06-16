import sys
import time
import tensorflow as tf
import pandas as pd
import datetime
from MongoToCsv import dataToPredict
from DataProcessing import preprocess_df_predict
from pymongo import MongoClient
import pymongo

currency_from = sys.argv[1]
currency_to = sys.argv[2]
ip = sys.argv[3]
admin = sys.argv[4]
password = sys.argv[5]
model_file_name = sys.argv[6]

SEQ_LEN = 60
FUTURE_PERIOD_PREDICT = 1
RATIO_TO_PREDICT = "CLOSE"
EPOCHS = 10
BATCH_SIZE = 64
NAME = f"{SEQ_LEN}-SEQ-{FUTURE_PERIOD_PREDICT}-PRED{int(time.time())}"

dataToPredict(currency_from+currency_to, ip, admin, password)
df = pd.read_csv(currency_from+currency_to+'pd.csv', names=["date","high","open","low","close","ema"])
df.set_index('date', inplace=True)
date = df[len(df)-1:].index.values.astype(str)
date = datetime.datetime.strptime(date[0],'%Y-%m-%d')

print(df[len(df)-6:])

df = preprocess_df_predict(df)

model = tf.keras.models.load_model(model_file_name)

output = model.predict(df)

client = MongoClient('mongodb://'+ip+':27017', username=admin, password=password, authSource='admin', authMechanism='SCRAM-SHA-256')
db = client.forex

date += datetime.timedelta(days=1)
while(date.weekday() == 5 or date.weekday() == 6):
    date += datetime.timedelta(days=1)

buy = 0
if output[0][0] > output[0][1]:
    buy = 1
string_date = str(date.date())
data = {
    'date' : string_date,
    'currency' : currency_from+currency_to,
    'buy' : buy
}
if(len(list(db.PredictionData.find({'date': string_date, 'currency': currency_from+currency_to}))) == 0):
    result = db.PredictionData.insert_one(data)


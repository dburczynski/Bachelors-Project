import datetime
import pandas as pd
from pymongo import MongoClient
import pymongo
import csv

def allData(currency, ip, admin, password):
    client = MongoClient('mongodb://'+ip+':27017',username=admin, password=password, authSource='admin', authMechanism='SCRAM-SHA-256')

    db = client.forex

    df = pd.DataFrame()

    dataset = list(db.RealData.find({"currency": str(currency)}).sort('date', pymongo.ASCENDING))
    df = pd.DataFrame(dataset)
    df.high = df.high.astype(float)
    df.open = df.open.astype(float)
    df.low = df.low.astype(float)
    df.close = df.close.astype(float)
    df.EMA = df.EMA.astype(float)

    csvList = []
    for i in range(0, len(df)):
        date = datetime.datetime.strptime(df['date'][i], '%Y-%m-%d')
        date = str(date.date())
        rowList = [date,df['high'][i],df['open'][i],df['low'][i],df['close'][i],df['EMA'][i]]
        csvList.append(rowList)
    with open(currency+'.csv','w') as myfile:
        wr = csv.writer(myfile, delimiter=",")
        for item in csvList:
            wr.writerow(item)


def dataToPredict(currency, ip, admin, password):
    client = MongoClient('mongodb://' + ip + ':27017', username=admin, password=password, authSource='admin',
                         authMechanism='SCRAM-SHA-256')

    db = client.forex

    df = pd.DataFrame()

    dataset = list(db.RealData.find({'currency': currency}).sort('date', pymongo.ASCENDING))
    df = pd.DataFrame(dataset)
    df.high = df.high.astype(float)
    df.open = df.open.astype(float)
    df.low = df.low.astype(float)
    df.close = df.close.astype(float)
    df.EMA = df.EMA.astype(float)
    csvList = []
    for i in range(len(dataset)-65, len(dataset)):
        date = datetime.datetime.strptime(df['date'][i], '%Y-%m-%d')
        date = str(date.date())
        rowList = [date, df['high'][i], df['open'][i], df['low'][i], df['close'][i], df['EMA'][i]]
        csvList.append(rowList)
    with open(currency+'pd.csv', 'w') as myfile:
        wr = csv.writer(myfile, delimiter=",")
        for item in csvList:
            wr.writerow(item)
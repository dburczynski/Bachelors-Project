import requests
import sys
import json
from pymongo import MongoClient


#sets args to according variables
currency_from = sys.argv[1]
currency_to = sys.argv[2]
ip = sys.argv[3]
admin = sys.argv[4]
password = sys.argv[5]
api_key = sys.argv[6]


#checks repsonse status code and loads json data
response = requests.get("https://www.alphavantage.co/query?function=FX_DAILY&from_symbol="+currency_from+"&to_symbol="+currency_to+"&outputsize=full&apikey="+api_key)
if response.status_code != 200:
    sys.exit()

json_data_currency = json.loads(response.text)

response = requests.get("https://www.alphavantage.co/query?function=EMA&symbol="+currency_from+currency_to+"&interval=daily&time_period=1000&series_type=open&apikey="+api_key)
if response.status_code != 200:
    sys.exit()

json_data_ema = json.loads(response.text)



#json data to series
time_series = json_data_currency['Time Series FX (Daily)']
ema_series = json_data_ema['Technical Analysis: EMA']

#array of series
seriesArray = [time_series,ema_series]
#selects array that has smallest size
seriesArray.sort(key=len)
minSeries = seriesArray[0]

#creates arrays for data
dates = []
open = []
high = []
low = []
close = []
ema = []



#data input
for k in minSeries:
    try:
        ts = time_series[k]
        em = ema_series[k]

    except:
        continue
    open.append(ts['1. open'])
    high.append(ts['2. high'])
    low.append(ts['3. low'])
    close.append(ts['4. close'])
    ema.append(em['EMA'])
    dates.append(k)

#connect to mongodb
client = MongoClient('mongodb://'+ip+':27017', username=admin, password=password, authSource='admin', authMechanism='SCRAM-SHA-256')
db = client.forex
collection = db.RealData
inserted = 0
duplicates = 0
deleted = 0
for i in range(0, len(dates)):
    searchResultList = list(collection.find({'date': str(dates[i])}))
    if len(searchResultList) == 0:
        data = {
            'date': dates[i],
            'open': open[i],
            'high': high[i],
            'low': low[i],
            'close': close[i],
            'EMA': ema[i],
            'currency' : currency_from+currency_to
        }
        result = db.RealData.insert_one(data)
        inserted = inserted + 1

    if len(searchResultList) > 1:
        duplicates = duplicates + len(searchResultList)
        for i in range(0, len(searchResultList)):
            db.RealData.remove({'_id': searchResultList[i]['_id']})
            deleted = deleted + 1
        data = {
            'date': dates[i],
            'open': open[i],
            'high': high[i],
            'low': low[i],
            'close': close[i],
            'EMA': ema[i],
            'currency' : currency_from+currency_to
        }
        result = db.RealData.insert_one(data)





print('Inserted '+str(inserted)+' document(s)!')
print('Duplicates found: '+str(duplicates))
print('Deleted '+str(deleted)+' document(s)')

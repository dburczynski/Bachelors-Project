import pandas as pd
import sys
import time
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, LSTM, BatchNormalization
from tensorflow.keras.callbacks import TensorBoard, ModelCheckpoint
from MongoToCsv import allData
from DataProcessing import preprocess_df, classify

SEQ_LEN = 60
FUTURE_PERIOD_PREDICT = 1
RATIO_TO_PREDICT = "CLOSE"
EPOCHS = 10
BATCH_SIZE = 64
NAME = f"{SEQ_LEN}-SEQ-{FUTURE_PERIOD_PREDICT}-PRED{int(time.time())}"


currency_from = sys.argv[1]
currency_to = sys.argv[2]
ip = sys.argv[3]
admin = sys.argv[4]
password = sys.argv[5]

allData(currency_from+currency_to, ip, admin, password)

main_df = pd.DataFrame()
df = pd.read_csv(currency_from+currency_to+'.csv', names=["date","high","open","low","close","ema"])
df.set_index('date', inplace=True)
main_df = df

main_df['future'] = main_df['close'].shift(-FUTURE_PERIOD_PREDICT)

main_df['target'] = list(map(classify, main_df['close'], main_df['future']))

times = sorted(main_df.index.values)
last_5pct = times[-int(0.05*len(times))]

validation_main_df = main_df[(main_df.index >= last_5pct)]
main_df = main_df[(main_df.index < last_5pct)]

#preprocess_df(main_df)
train_x, train_y = preprocess_df(main_df)
validation_x, validation_y = preprocess_df(validation_main_df)

print(f"train data:  {len(train_x)} validation: {len(validation_x)}")
print(f"dont buys: {train_y.count(0)}, buys: {train_y.count(1)}")
print(f"Validation dont buys: {validation_y.count(0)}, buys {validation_y.count(1)}")

model = Sequential()
model.add(LSTM(128, input_shape=(train_x.shape[1:]), return_sequences=True))
model.add(Dropout(0.2))
model.add(BatchNormalization())

model.add(LSTM(128, input_shape=(train_x.shape[1:]), return_sequences=True))
model.add(Dropout(0.1))
model.add(BatchNormalization())

model.add(LSTM(128, input_shape=(train_x.shape[1:])))
model.add(Dropout(0.2))
model.add(BatchNormalization())

model.add(Dense(32, activation="relu"))
model.add(Dropout(0.2))

model.add(Dense(2,activation="softmax"))

opt = tf.keras.optimizers.Adam(lr=0.001, decay=1e-6)

model.compile(loss='sparse_categorical_crossentropy',
             optimizer=opt,
             metrics=['accuracy'])

tensorboard = TensorBoard(log_dir=f"logs/{NAME}")

filepath = currency_from+currency_to+"-Final-{epoch:02d}-{val_acc:.3f}"
checkpoint = ModelCheckpoint("/home/darek/Projects/forexpredictor/src/main/python/ml/models/{}.model".format(filepath,monitor='val_acc', verbose=1, save_best_only=True, node='max'))

history = model.fit(
    train_x, train_y,
    batch_size=BATCH_SIZE,
    epochs=EPOCHS,
    validation_data=(validation_x,validation_y),
    callbacks=[tensorboard, checkpoint])
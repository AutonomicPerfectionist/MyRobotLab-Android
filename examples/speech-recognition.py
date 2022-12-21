ear = Runtime.getService("ear@android")
python = Runtime.getService("python")

def onText(text):
    print(str(text))

python.subscribe("ear@android", "publishText")
ear.startListening()

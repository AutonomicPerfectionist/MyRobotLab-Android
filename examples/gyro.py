# new awesome robot script
python = Runtime.getService("python")
gyro = Runtime.getService("gyro@android")

def onOrientation(orientation):
    print(str(orientation))
    
python.subscribe("gyro@android", "publishOrientation")
gyro.start()

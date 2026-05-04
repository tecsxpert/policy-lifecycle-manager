import time

start_time = time.time()

def get_uptime():
    return round(time.time() - start_time, 2)
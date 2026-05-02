response_times = []

def add_response_time(t):
    response_times.append(t)

    # keep only last 10
    if len(response_times) > 10:
        response_times.pop(0)

def get_avg_response_time():
    if not response_times:
        return 0
    return round(sum(response_times) / len(response_times), 3)
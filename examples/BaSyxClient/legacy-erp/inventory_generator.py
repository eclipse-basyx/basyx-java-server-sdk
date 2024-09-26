import os
import csv
import random
import time
from datetime import datetime, timedelta

export_path = os.getenv("ERP_INTERNAL_EXPORT_PATH", "/export") 
gen_interval = int(os.getenv("ERP_GEN_INTERVAL", 10))
max_number_of_motors = int(os.getenv("ERP_MAX_NUMBER_OF_MOTORS", 30))

if not os.path.exists(export_path):
    os.makedirs(export_path)

manufacturers = ["MotorCorp", "PowerMotors", "ElectroTech", "MechWorks"]

motor_data = []

STATUS_IN_STOCK = "In Stock"
STATUS_UNDER_MAINTENANCE = "Under Maintenance"
STATUS_SOLD = "Sold"
TIMESTAMP_FMT = "%Y-%m-%dT%H:%M:%S"

def do_with_chance(action, probability, *args):
    if random.random() < probability:
        action(*args)


def create_motor():
    motor_id = f"MTR{random.randint(1000, 9999)}"
    motor = {
        "MotorID": motor_id,
        "MotorType": random.choice(["AC Motor", "DC Motor"]),
        "Manufacturer": random.choice(manufacturers),
        "PurchaseDate": datetime.now().strftime(TIMESTAMP_FMT),
        "Location": f"Aisle_{random.randint(1, 10)}",
        "LastMaintenance": "N/A",
        "MaintenanceSchedule": (datetime.now() + timedelta(days=random.randint(90, 180))).strftime(TIMESTAMP_FMT),
        "WarrantyPeriod": (datetime.now() + timedelta(days=365*random.randint(2, 5))).strftime(TIMESTAMP_FMT),
        "Status": STATUS_IN_STOCK,
        "DateSold": ""
    }
    return motor

def receive_new_motor():
    new_motor = create_motor()
    motor_data.append(new_motor)
    print(f"New motor received: {new_motor['MotorID']}")

def sell_motor(motor):
    motor["Location"] = "N/A"
    motor["DateSold"] = datetime.now().strftime(TIMESTAMP_FMT)
    motor["Status"] = STATUS_SOLD
    print(f"Motor sold: {motor['MotorID']}")

def send_motor_to_maintenance(motor):
    motor["Status"] = "Under Maintenance"
    motor["LastMaintenance"] = datetime.now().strftime(TIMESTAMP_FMT)
    next_maintenance = datetime.now() + timedelta(days=180)
    motor["MaintenanceSchedule"] = next_maintenance.strftime(TIMESTAMP_FMT)
    print(f"Motor {motor['MotorID']} is now under maintenance")

def remove_motor_from_maintenance(motor):
    motor["Status"] = STATUS_IN_STOCK
    print(f"Motor {motor['MotorID']} is now back in stock after maintenance")

def manage_motor_lifecycle(motor):
    if motor["Status"] == STATUS_IN_STOCK:
        action = random.choice(["sell", "maintenance", "none", "none"])
        if action == "sell":
            sell_motor(motor)
        elif action == "maintenance":
            send_motor_to_maintenance(motor)
    elif motor["Status"] == STATUS_UNDER_MAINTENANCE:
        do_with_chance(remove_motor_from_maintenance, 0.3, motor)

def generate_inventory_csv():
    timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    file_name = f"inventory_{timestamp}.csv"
    file_path = os.path.join(export_path, file_name)

    with open(file_path, mode='w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=motor_data[0].keys())
        writer.writeheader()
        writer.writerows(motor_data)

    print(f"Generated new inventory CSV: {file_name}")

def start_generating():
    # Receive a few initial motors
    for _ in range(3):
        receive_new_motor()

    while True:
        # Simulate motor lifecycle events for each motor
        for motor in motor_data:
            manage_motor_lifecycle(motor)

        # Occasionally receive new motors
        if(len(motor_data) < max_number_of_motors):
            do_with_chance(receive_new_motor, 0.5)

        generate_inventory_csv()
        
        time.sleep(gen_interval)

if __name__ == "__main__":
    start_generating()

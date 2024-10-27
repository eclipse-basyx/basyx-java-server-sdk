FROM python:3.8-slim

RUN pip install paho-mqtt

COPY publisher.py /publisher.py

CMD ["python", "/publisher.py"]
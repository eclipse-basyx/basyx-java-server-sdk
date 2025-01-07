FROM python:3.10-slim
ARG HTTP_PROXY
ARG HTTPS_PROXY
WORKDIR /app

COPY . /app

RUN chmod +x inventory_generator.py

CMD ["python", "./inventory_generator.py"]

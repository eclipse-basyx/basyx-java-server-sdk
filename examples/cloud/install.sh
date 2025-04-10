# Cache local charts for the aas-dashboard charts
helm dep update
# Add bitnami (reliable source for all external charts)
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo add t3n https://storage.googleapis.com/t3n-helm-charts

# Add UI plugins to ConfigMaps
kubectl create configmap -n basyx time-series-ui-plugin --from-file=config_files/basyx-web-ui/TimeSeriesVisualizerPlugin.vue
kubectl create configmap -n basyx hello-world-plugin --from-file=config_files/basyx-web-ui/HelloWorldPlugin.vue

# MongoDB has an issue with charts not sitting well in an umbrella chart.
# This is a bug with some common variables being defined externally.
helm install -n basyx mongodb bitnami/mongodb --version 14.5.0 -f values.mongodb.yaml
# helm upgrade -n basyx mongodb bitnami/mongodb -f values.mongodb.yaml
helm uninstall -n basyx mongodb

# Install a release `basyxv2` in a namespace `basyx`.
helm upgrade --install -n basyx basyxv2 -f values.external.yaml -f values.basyx.yaml . 
helm diff upgrade -n basyx basyxv2 -f values.external.yaml -f values.basyx.yaml . 
helm uninstall -n basyx basyxv2

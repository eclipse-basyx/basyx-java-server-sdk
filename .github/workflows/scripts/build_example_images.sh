#!/bin/bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <mem|mongodb|dynamic-rbac|kafka>"
  exit 1
fi

aas_discovery_modules=(
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice-core"
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice-backend"
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice-backend-h2"
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice-backend-mongodb"
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice-http"
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice-feature-authorization"
  "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice.component"
)

case "$1" in
  mem)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-log-mem"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-log-mem"
      "${aas_discovery_modules[@]}"
    )
    ;;
  mongodb)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-log-mongodb"
      "${aas_discovery_modules[@]}"
    )
    ;;
  dynamic-rbac)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.submodelrepository.component"
      "${aas_discovery_modules[@]}"
    )
    ;;
  kafka)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-kafka-mem"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-kafka-mem"
      "org.eclipse.digitaltwin.basyx:basyx.submodelservice.component"
    )
    ;;
  *)
    echo "Unknown image set: $1"
    exit 1
    ;;
esac

module_list=$(IFS=,; echo "${modules[*]}")

mvn package \
  -DskipTests \
  -Ddocker.namespace=eclipsebasyx \
  -Ddocker.target.platforms=linux/amd64 \
  --projects "${module_list}" \
  --also-make

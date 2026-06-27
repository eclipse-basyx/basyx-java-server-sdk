#!/bin/bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <mem|mongodb|dynamic-rbac|kafka>"
  exit 1
fi

case "$1" in
  mem)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-log-mem"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-log-mem"
      "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice.component"
    )
    ;;
  mongodb)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice.component"
    )
    ;;
  dynamic-rbac)
    modules=(
      "org.eclipse.digitaltwin.basyx:basyx.aasenvironment.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.submodelregistry-service-release-log-mongodb"
      "org.eclipse.digitaltwin.basyx:basyx.submodelrepository.component"
      "org.eclipse.digitaltwin.basyx:basyx.aasdiscoveryservice.component"
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

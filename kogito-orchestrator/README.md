# SAGA with Kogito and LRA microprofile

## Kogito orchestrator

Start Saga orchestrator:

* BROKER_URL: Where events are emitted
* internal.base.uri: Used for generating callback URIs registered in the lra-coordinator
* saga.process.id: The process to start `orders` or `tripReservation`
* lra.http.port: Port to connect to the lra-coordinator, defaults to 8080
* lra.http.host: Host to connect to the lra-coordinator, defaults to localhost

```bash
BROKER_URL=http://localhost:18080 mvn clean package quarkus:dev -Dsaga.process.id=tripReservation -Dlra.http.port=8580 -Dinternal.base.uri=http://localhost:8080
```

Note: Native build doesn't work due to current narayana-lra limitations.

## Run the LRA Coordinator

Container image: quay.io/ruben/lra-coordinator-thorntail

```bash
podman run quay.io/ruben/lra-coordinator-thorntail -Dthorntail.http.port=8580
```

Run locally (from git repository jbosstm/narayana)

```bash
java -jar target/lra-coordinator-thorntail.jar -Dthorntail.http.port=8580
```

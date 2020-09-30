# Reservations service project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `sagas-poc-reservations-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/sagas-poc-reservations-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/sagas-poc-reservations-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Car reservation request
```
curl -v http://localhost:8080/ \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: CarReservation" \
  -H "Ce-Source: dev.knative.samples/helloworldsource" \
  -H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f79" \
  -H "Content-Type: application/json" \
  -H "Ce-Subject: CAR-0001" \
  -d @examples/car.json

> POST / HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.69.1
> Accept: */*
> Ce-Specversion: 1.0
> Ce-Type: CarReservation
> Ce-Source: dev.knative.samples/helloworldsource
> Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f79
> Content-Type: application/json
> Ce-Subject: CAR-0001
> Content-Length: 248

< HTTP/1.1 200 OK
< ce-source: http://localhost:8080/cars/reservations/
< ce-subject: CAR-0001
< Content-Length: 0
< ce-specversion: 1.0
< ce-type: CarReservationSuccess
< ce-id: 6e9661a2-0afc-4902-82fe-0cc11e4aef12
```

## Car reservation cancel
```{bash}
$ curl -v http://localhost:8080/ -XPOST \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: CarReservationCancel" \
  -H "Ce-Source: dev.knative.samples/helloworldsource" \
  -H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f79" \
  -H "Content-Type: application/json" \
  -H "Ce-Subject: CAR-0001"

> POST / HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.69.1
> Accept: */*
> Ce-Specversion: 1.0
> Ce-Type: CarReservationCancel
> Ce-Source: dev.knative.samples/helloworldsource
> Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f79
> Content-Type: application/json
> Ce-Subject: CAR-0001
> Content-Length: 248
> 
* upload completely sent off: 248 out of 248 bytes
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Content-Length: 0
```
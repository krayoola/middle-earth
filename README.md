# Middle-earth

An application showcasing Streaming of numeric values from a GRPC server, up to the given maximum value

## Components

mordor - `prime-number-server` GRPC service that produce prime numbers up to the given value
gondor - `proxy-service` A http streaming service that is exposed to clients, and accepts a numeric value which being forward to mordor service
protobuf - GRPC contracts protocol used by both service to communicate to each other

## Technology stack

- Scala - main programing language used
- cats - functional library abstraction
- Http4s - Http web service library
- fs2.Streams - Streaming process library
- fs2 grpc - grpc implementation built on top of cats and fs2
- munit/ - unit test library
- pureConfig - typed configuration sourcing library

## High level overview

```
                           Gondor                                    Mordor
               | Endpoint -> Service -> Repository ┐             |
 clients  <->  |                                   |-->  grpc <--|  mordor.Service
               | Endpoint <- Service <- Repository ┘             |
```

### Endpoint

layer that is responsible for receive and serve http request and response

### Service

layer that is responsible for any business logic of the application(e.g change of sequence of call, some validation etc)

### Repository

layer responsible for communication and other activities related to the external modules.

### grpc

protocol used to communicate both gondor and mordor service

### mordor.Service

layer responsible for the computation related to the request

## Mordor

Mordor `prime-number-server`, is responsible for prime number generation that is requested by Client. It's main function is to abstract any computation away from the proxy server a.k.a Gondor in a streaming fashion.

### Sample request

```curl
grpcurl -d '{"maxNumberRange":"17"}' -plaintext \
-import-path protobuf/src/main/protobuf -proto prime.proto \
127.0.0.1:9999 com.service.PrimeNumberService/GeneratePrimeNumber
```

### How to run Mordor

```shell
sbt mordor
```

## Gondor

Gondor `proxy-service`, is a responsible for accepting request and response from clients, and apply any necessary basic validation that can be extracted away from Mordor, and serving the response back to clients in a streaming fashion.

### Api exposed

- `/prime/:intValue` 
  - strictly accepts positive numeric value only
- `/prime/v2/:intValue` - [Optional solution]
  - accept any numeric value, however gives you a response if fails the validation

### Sample request

request:

```curl
curl -v GET 'http://127.0.0.1:8081/prime/17'
```

response:

```
* Connected to 127.0.0.1 (127.0.0.1) port 8081 (#1)
> GET /prime/17 HTTP/1.1
> Host: 127.0.0.1:8081
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200 OK
< Content-Type: text/plain; charset=UTF-8
< Date: Thu, 15 Jul 2021 12:01:00 GMT
< Transfer-Encoding: chunked
<
* Connection #1 to host 127.0.0.1 left intact
2,3,5,7,11,13,17*
Closing connection 1
```

### How to run Gondor

```shell
sbt gondor
```

## Create docker images

to create a gondor docker image

```shell
sbt dockerGondor
```

to create a mordor docker image

```shell
sbt dockerMordor
```

## Run tests

to run all gondor unit tests

```shell
sbt testGondor
```

to run all mordor unit tests

```shell
sbt testMordor
```

## Implementation

## Technology alternatives

## Improvements and recommendation

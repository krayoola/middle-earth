# Welcome to Middle-earth

A client-server application showcasing streaming of prime numeric values from a GRPC server.

## Components

mordor - `prime-number-server` A GRPC protocol serving service that produce prime numbers in a streaming manner.

gondor - `proxy-service` A http streaming service that serves clients requests and accepts numeric value which is then being forwarded to mordor service.

protobuf - GRPC contract protocol used by both services to communicate to each other

## Technology stack

- Scala - main programing language used
- cats - functional library abstraction
- Http4s - Http web service library
- fs2.Streams - Streaming process library
- fs2 grpc - grpc implementation built on top of cats and fs2
- munit - unit test library
- pureConfig - typed configuration sourcing library

## High level overview

```docs
    Internet                 Gondor                                    Mordor
               | Endpoint -> Service -> Repository -┐             |
 clients  <->  |                                    |-->  grpc <--|  mordor.Service
               | Endpoint <- Service <- Repository <┘             |
```

### Endpoint

layer that is responsible for receiving and serving http request/response.

### Service

layer that is responsible for any business logic of the application (e.g chaining of events, change of sequence of call, some validation etc)

### Repository

layer responsible for communication and other activities on external modules.

### grpc

protocol used to communicate for both gondor and mordor service

### mordor.Service

layer responsible for the computation related to the request

## Mordor

Mordor `prime-number-server`, is responsible for prime number generation that will be stream to the client. It's main function is to abstract any computation away from the proxy server a.k.a Gondor.

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

Gondor `proxy-service`, is responsible for accepting request and response from clients, apply any necessary basic validation that can be extracted away from Mordor, and serving the response back to clients in a streaming fashion.

### Api exposed

- `/prime/:intValue`
  - strictly accepts positive numeric value only
- `/prime/v2/:intValue` - [Optional solution]
  - accepts any numeric value, however gives you a response if fails the validation
- `/prime/v3/:intValue` - [Not part of the solution(yet)]
  - accepts any numeric value, however returns status 501 not implemented function

### Sample request

request:

```curl
curl -v GET 'http://127.0.0.1:8081/prime/17'
```

response:

```shell
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
2,3,5,7,11,13,17
```

_NOTE: response should be compressed with `GZip`, Curl is not showing it, However its shown on the demo below._

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

## Running docker images

```docker
# docker run --rm -p 9999:9999 --name mordor <docker id/name>
# e.g
docker run --rm -p 9999:9999 --name mordor e9ab442f5b6d
```

```docker
# docker run --rm -p 8081:8081 --name gondor <docker id/name>
# e.g
docker run --rm -p 8081:8081 --name gondor e9ab442f5b6d
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

The tools and libraries I chosen and picked were mainly because of my taste, what I know that will do the job smoothly and my likeness on working in a functional code base.

As everything is expressed in functional way (I hope), It will be easy to comprehend with the code and extract/change/modify/chain any business request.

Also testing every layer of the application feels so much easier due to most of the function are composed, pure. additionally we are sure that all operation are executed at the end of the application.

Why I choose fs2.Stream over others? Aside from its being a purely functional streaming library, For me it's a great fit to the ecosystem since fs2.stream is a first class citizen of http4s. I would say integration is easy, along with their core functional abstraction under the hood, cats. also worth to mention that because of its pull based approach of streaming we mitigate any network and buffer overload also reduce any resource hungry operation. that said it fits perfectly on our requirement.

Why Effect pattern? by doing so. we build our application by wrapping behaviors/description of an application in a effect. and be sure that it will not execute in a single place. and not execute directly that may introduce side effect, additionally we can change any execution model we like on our application.

## Technology alternatives

Scala - Alternatively we can use any programming language/libraries where we are comfortable and with rich GRPC support such as (Go, Node[type-script/javascript], Java, Rust etc).

cats - ZIO, scalaZ

Http4s - Akka Http, ZIO Http, PlayFramework

Fs2.Streams - Akka Streams, ZStreams

Fs2 Grpc - [Akka GRPC](https://github.com/akka/akka-grpc)

Munit - [Scala test](https://github.com/scalatest/scalatest), [Specs2](https://github.com/etorreborre/specs2)

PureConfig - [Typesafe config](https://github.com/lightbend/config)

## Improvements and recommendation

- Give some love on the Mordor code base.

- CI/Pipeline integration (Jenkins, Travis or Github actions etc)

- Separation of this mono repo into 3 different repositories to enable a parallel contribution from other developers.

- Enrich unit tests with more assertions and cases for both services.

- Enhance test using property-based testing libraries such as [ScalaCheck](https://github.com/typelevel/scalacheck) etc.

- black box/functional testing.

- web app (probably built on [react](https://github.com/facebook/react)/[vue](https://github.com/vuejs/vue):type-script) for better stream response representation.

- add logging libraries such as [log4cats](https://github.com/typelevel/log4cats) etc.

- Apply Authentication and Authentication Layer on both Gondor and Mordor Services.

  - TLS
  - Session based/Cookie based

- integrate refactoring and linting tool such as [scalafix](https://github.com/scalacenter/scalafix).

- Tapir Integration for API documentation.

- Expose health check APIs for both services.

- Create a performance testing suite using [Gatling.io](https://github.com/gatling/gatling)

## Demo

![Demo](demo/demo.gif)

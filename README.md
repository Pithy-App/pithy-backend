# Pithy
`Pithy` is an application for sorting comments from popular social medias' posts by custom, plain-english queries.

## Pithy Backend

`pithy-backend` includes majority of the backend functionalities including but not limited to `authentication`, interactions with various `platform` APIs, `databases` etc.

It is a highly robust and scalable application that takes advantage of Akka's powerful concurrency model throughout the entire application, making it a strong candidate for large number of concurrent users.

## Overview

`pithy-backend` is a multi-modular repo where each submodule represents an independent portion of the backend. 

Refer to `build.sbt` for detailed configurations regarding each submodule's dependencies, co-dependencies, and settings.

## Getting Started

### Environment

* Java 17
* Scala 2.13.15
* Sbt 1.10.1
* Linux/Unix needed for running AWS Lambda related scripts


### Modules

Each submodule has its own `Main`, `MainDev`, `MainInput` and `MainOutput` implementations for observability, testing, and scalability purposes. Such structure makes it easier to, for instance, add another `platform` like Instagram to the existing structure.

* `root`: Parent module used for assembling ÜberJar
* `app`: main entry point of the application after authentication. It abstracts away the main business logics from Lambda handlers.
* `auth`: authentication logics
* `platform`: API requests, responses, clients for different platforms
* `openai`: API requests, responses through [OpenAI Scala Client](https://github.com/cequence-io/openai-scala-client)
* `database`: [To be implemented] caches, labels, then batch uploads comment data to OpenAI API. Also stores user info (cookies/accounts).
* `lambda`: AWS Lambda functions that expose various aspects of this backend to `pithy-frontend`
* `utils`: Common utility module shared by all submodules, which includes useful objects like `JSON parser`
* `deployment`: Bash scripts for assembling, deploying and testing the `ÜberJar` corresponding to the current state of the application to AWS.

### Env vars

Set OpenAI API key as environment variable
```
export OPENAI_SCALA_CLIENT_API_KEY=your_key
```

### Executing program

* Be at the root level: `/pithyback-end`
* For invokable submodules (ones with `MainDev`), run `sbt clean compile [submoduleName]/run`. E.g. `sbt clean compile auth/run`, `sbt clean compile app/run`, etc
* To run `deployment` scripts, sign into `aws-cli` (AWS console) first with IAM first.

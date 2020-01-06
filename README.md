#   chat-room

Proof-of-concept that demonstrates a full-stack streaming architecture by implementing a simple chat room.

### Built With

* Spring Boot (WebFlux, Messaging)
* Amazon Kinesis Data Streams
* Amazon Kinesis SDK (Producer, Client)
* POH&J (Plain Old HTML & JavaScript)

### Build
`mvn clean install`

### Run
* Create an Amazon Kinesis Data Stream called `messages` in `us-west-1`
* Create a user with full access to Kinesis, CloudWatch, and DynamoDB
* `AWS_ACCESS_KEY=... AWS_SECRET_KEY=... mvn spring-boot:run`
* http://localhost:8080

### How It Works

Chat messages are sent via XHR from browser to server. A Reactive Spring `HandlerFunction` on the server
asynchronously invokes an Amazon Kinesis `KinesisProducer` to store records in an Amazon Kinesis Stream.

An Amazon Kinesis `Scheduler` instance is submitted to the Spring Boot `TaskExecutor` when the application starts.
The `Scheduler` polls Kinesis, and uses a factory to create instances of `ChatMessageRecordProcessor` that process new records in the
Kinesis stream.

The `ChatMessageRecordProcessor.processRecords` method parses the Kinesis record, and publishes the data to a `UnicastProcessor`.
A `UnicastProcessor` is a kind of reactive queue or buffer.

The _same instance_ of the `UnicastProcessor` is used to create an instance of `Flux<ChatMessage>` which is injected
into the `WebSocketSessionHandler`.

The `WebSocketSessionHandler` is an instance of `WebSocketHandler` whose `handle` method sends the `Flux<ChatMessage>`
to the client, and never completes. The WebSocket session is kept open until the client closes it.

Effectively sharing the instance of `UnicastProcessor` by creating a `Flux` from it is kind of the "trick" -- the `Flux` acts as an unbounded
stream of `ChatMessage` since a reference to the underlying `UnicastProcessor` is maintained by the factory that creates instances of
`ChatMessageRecordProcessor`.

The client side is implemented with HTML and JavaScript.
The JavaScript makes a WebSocket connection to the server when the page loads. The JavaScript defines a
`WebSocket.onmessage` handler that creates and appends `<div>` elements to DOM with the `ChatMessage` sent from the
`Flux<ChatMessage>`.

### Why Bother with Kinesis?

Couldn't the `HandlerFunction` just publish to the `UnicastProcessor` and obviate the need for a middleware like
Amazon Kinesis?

It could, but then the application couldn't scale horizontally because it would be stateful.
Whereas the  `UnicastProcessor` is really just a buffer, Kinesis works as a datastore of streaming data, similar to how
PostgreSQL or Oracle would work for an application that managed relational data.

### References

* [Amazon Kinesis Developer Guide](https://docs.aws.amazon.com/streams/latest/dev/kinesis-dg.pdf)
* [Writing to your Kinesis Data Stream Using the KPL](https://docs.aws.amazon.com/streams/latest/dev/kinesis-kpl-writing.html)
* [Developing a Kinesis Client Library Consumer in Java](https://docs.aws.amazon.com/streams/latest/dev/kcl2-standard-consumer-java-example.html)
* [Web on Reactive Stack](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
* [Reactor 3 Reference Guide](https://projectreactor.io/docs/core/release/reference/index.html)
# MultiThreadProject

A Spring Boot WebSocket chat server that groups connected clients into rooms and broadcasts text messages to the other clients in the same room.

## Technology

- Java 17
- Spring Boot 4.1.0
- Spring WebSocket
- Maven

## Running the application

Start the server with the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

The server listens on port `3000`.

## WebSocket endpoint

Connect to `/ws` with a `room` query parameter:

```text
ws://localhost:3000/ws?room=gaming
```

The `room` parameter is required and must be the first query parameter. Room names are case-sensitive.

## Testing with Postman

1. Create a WebSocket request in Postman.
2. Connect to `ws://localhost:3000/ws?room=gaming`.
3. Open a second WebSocket request using the exact same URL.
4. Send a text message from either client.
5. The other client receives the message.

When a client connects, it receives:

```text
You are in room: gaming
```

When a client sends a message, the other open clients in that room receive:

```text
User <session-id> says: <message>
```

The sender does not receive a copy of its own message. Clients connected to different rooms do not receive each other's messages.

## Implementation

`WebSocketConfig` registers the WebSocket handler at `/ws` and allows all origins for local development.

`MyHandler`:

- Extracts the room name from the connection URI.
- Stores sessions in a thread-safe set for each room.
- Broadcasts text messages to other open sessions in the same room.
- Removes sessions when connections close.
- Uses `ConcurrentWebSocketSessionDecorator` to make concurrent sends safer.

## Project structure

```text
src/
├── main/
│   ├── java/com/example/multithreadproject/
│   │   ├── MultiThreadProjectApplication.java
│   │   └── config/
│   │       ├── MyHandler.java
│   │       └── WebSocketConfig.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/multithreadproject/
        └── MultiThreadProjectApplicationTests.java
```

## Running tests

```bash
./mvnw test
```

The current test verifies that the Spring application context loads successfully.

## Production considerations

The WebSocket configuration currently uses `.setAllowedOrigins("*")`. Restrict this to trusted frontend origins before deploying. The current room model is in-memory, so rooms and connections are lost when the application stops and do not synchronize across multiple server instances.

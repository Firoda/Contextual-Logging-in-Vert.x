# Vertx Event Bus example


## Contributor
Badaruddeen shafi
@msbadar [Badar](http://twitter.com/msbadar)

## The Concept

Asynchronous communication between server and worker. Server busy with handling request when cluster is doing some computation. Pub sub done vrtex Event Bus class

## Requirement
1. vertex : 3.10
2. Java : 1.8.0
3. mvn latest



#Structure
1. Server.java
	- listen all request
	- the incoming message to /ping is and its message passed to Listener
	- passed pong message after getting reply

2. Listener
	- Listen whether server sends message or not
	- if then process and give response back

## Build
You build the project using:

```
mvn clean package

```	

## Running

Generated file can run by (Server)

```
java -jar target/vertex-example-1.0-fade.jar

```

After building run inside src folder

```
	vertx run Server.java
	vertx run Listener.java
```


Then, open a browser to http://localhost:8080/ping


## Sending message

http://localhost:8080/ping?msg=myMessage


## Ref

1. java World [java-world](http://www.javaworld.com/article/2078838/mobile-java/open-source-java-projects-vert-x.html?).
2. vertx examples [vertex-example] (https://github.com/vert-x3/vertx-examples)



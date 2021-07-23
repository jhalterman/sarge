# Sarge

[![Build Status](https://travis-ci.org/jhalterman/sarge.svg)](https://travis-ci.org/jhalterman/sarge)
[![Maven Central](https://img.shields.io/maven-central/v/net.jodah/sarge.svg?maxAge=60&colorB=53C92E)][maven] 
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![JavaDoc](https://img.shields.io/maven-central/v/net.jodah/sarge.svg?maxAge=60&label=javadoc&color=blue)](https://jodah.net/sarge/javadoc)

*Simple object supervision (for when stuff goes wrong)*

Sarge creates *supervised* objects which *automatically* handle failures when they occur by performing retries, state resets, and failure escalation, allowing for easy and robust fault tolerance with little effort.

## Usage

Sarge handles failures according to a `Plan` which takes an exception and directs Sarge to do something with it. Creating a `Plan` is straightforward:

```java
Plan plan = Plans
  .retryOn(TimeoutException.class, 5, Duration.mins(1))
  .escalateOn(ConnectionClosedException.class)
  .rethrowOn(IllegalArgumentException.class, IllegalStateException.class)
  .make();
```      
      
This Plan retries any method invocations that fail with a TimeoutException, escalates any ConnectionClosedExceptions, and rethrows any IllegalArgumentExceptions and IllegalStateExceptions.      

#### Supervision

With our `Plan` in hand, we can create a *supervised* object:

```java
Sarge sarge = new Sarge();
MailService service = sarge.supervised(MailService.class, plan);
```    

Supervision is automatically applied according to the plan when any exception occurs while invoking a method against the object:
    
```java
// Failures are handled according to the plan
service.sendMail();
```
    
#### Hierarchical supervision

Sarge can create a parent/child supervision hierarchy where the `Supervisor`'s plan is applied to any failures that occur in the child:

```java
class Parent implements Supervisor {
  @Override
  public Plan plan(){
    return Plans
      .retryOn(TimeoutException.class, 5, Duration.mins(1))
      .escalateOn(ConnectionClosedException.class)
      .make();
  }
}
 
Parent parent = new Parent(); 
Sarge sarge = new Sarge();
 
// Create a new Child that is supervised by the parent
Child child = sarge.supervised(Child.class, parent);
```    
    
We can link additional supervisable objects into a parent-child supervision hierarchy, which will handle any failures that are escalated:

```java
Parent parent = sarge.supervisable(Parent.class);
sarge.supervise(parent, uberParent);
```
	
#### More on plans

Aside from the `Plans` class, Plans can also be constructed directly by implementing the `Plan` interface and returning the desired `Directive` for handling each failure:

```java
Plan plan = new Plan() {
  public Directive apply(Throwable cause) {
    if (cause instanceof TimeoutException)
      return Directive.Retry(5, Duration.min(1));
    if (cause instanceof ConnectionClosedException)
      return Directive.Escalate;
  }
};
```
    
#### Lifecycle hooks

Lifecycle hooks allow supervised objects to be notified prior to a supervision directive being carried out, allowing an object to reset its internal state if necessary:

```java
class SupervisedService implements PreRetry {
  @Override
  public void preRetry(Throwable reason) {
    if (reason instanceof ConnectionClosedException)
      connect();
  }
}
```

#### 3rd Party Integration

By default, supervised objects must be instantiated by Sarge since they require instrumentation. As an alternative, we can delegate instantiation of supervised objects to other libraries such as [Spring](https://github.com/jhalterman/sarge/tree/master/src/test/java/net/jodah/sarge/integration/SpringIntegrationTest.java) or [Guice](https://github.com/jhalterman/sarge/tree/master/src/test/java/net/jodah/sarge/integration/GuiceIntegrationTest.java) by hooking into Sarge's [SupervisedInterceptor](https://jhalterman.github.com/sarge/javadoc/net/jodah/sarge/SupervisedInterceptor.html). Have a look at the [tests](https://github.com/jhalterman/sarge/tree/master/src/test/java/net/jodah/sarge/integration) for examples on how to integrate 3rd party libraries.

#### Logging

Logging is provided via the [slf4j](http://www.slf4j.org/) [API](http://www.slf4j.org/apidocs/index.html). Invocation exceptions are logged at the ERROR level and include only the exception message. Full exception logging can be enabled by setting the DEBUG log level for the `net.jodah.sarge` category.

## Docs

JavaDocs are available [here](https://jodah.net/sarge/javadoc).

## Limitations

Since Sarge relies on runtime bytecode generation to create supervised objects by subclassing them, it cannot supervise classes that are _final_, _protected_ or _private_, or methods that are _final_ or _private_.

## Thanks

Sarge was inpsired by [Erlang OTP's](http://www.erlang.org/doc/design_principles/des_princ.html) supervision trees and [Akka's supervision](http://akka.io) implementation. Thanks to the their contributors for the great work.

## License

Copyright 2012-2013 Jonathan Halterman - Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

[maven]: https://maven-badges.herokuapp.com/maven-central/net.jodah/sarge
# Sarge 0.2.0

*Simple object supervision (for when stuff goes wrong)*

Sarge creates *supervised* objects which *automatically* handle failures when they occur by performing retries, state resets, and failure escalation, allowing for easy and robust fault tolerance with little effort.

## Setup

[Download](https://github.com/jhalterman/sarge/downloads) the latest Sarge jar and add it to your classpath.

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
MailService service = sarge.supervise(MailService.class, plan);
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
 
// Create a Child that is supervised by the parent
Child child = sarge.supervise(Child.class, parent);
```    
    
We can link additional objects into the supervision hierarchy, which will handle any failures that are escalated:

```java
sarge.link(uberParent, parent);
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
	
## Thanks

Sarge was inpsired by [Erlang OTP's](http://www.erlang.org/doc/design_principles/des_princ.html) supervision trees and [Akka's supervision](http://akka.io) implementation. Thanks to the their contributors for the great work.

## License

Copyright 2012 Jonathan Halterman - Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
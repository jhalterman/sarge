# Sarge 0.1.0

Simple object supervision (for when stuff goes wrong).

## Introduction

Sarge supervises your objects, making fault handling easy. When failures occur, sarge whips your objects into shape by performing retries, state resets, and failure escalation.

## Examples

#### Simple supervision

	Sarge sarge = new Sarge();

    // Make a supervision plan
    Plan plan = Plans
      .retryOn(ConnectionFailedException.class, 5, Duration.mins(1))
      .rethrowOn(IllegalArgumentException.class, IllegalStateException.class)
      .make();

    // Create an instance of a class with failures handled according to the plan
    Something s = sarge.supervise(Something.class, plan);
     
    // Supervision is applied automatically when something goes wrong
    s.doSomething();
    
#### Hierarchical supervision

Hierarchical supervision involves chaining supervisors and supervised objects, where failures can be escalated up the chain as necessary.

    class SomeParent implements Supervisor {
      @Override
      public void plan(){
        Plans
          .retryOn(ConnectionFailedException.class, 5, Duration.mins(1))
          .rethrowOn(IllegalArgumentException.class, IllegalStateException.class)
          .escalateOn(TimeoutException.class)
          .make(); 
      }
    }
     
    SomeParent parent = new SomeParent();
    Sarge sarge = new Sarge();
     
    // Create an instance of a class supervised by the parent
    SomeChild c = sarge.supervise(SomeChild.class, parent);
     
    // Supervision is applied automatically when something goes wrong
    s.doSomething();
    
	// We can also link additional objects into the supervision hierarchy
	sarge.link(uberParent, parent);
	
## How it works

The key primitive in Sarge's supervision is a Plan, which takes a failure (Throwable) and directs sarge to do something with it such as retry, escalate, rethrow or resume. Plans can be constructed via the `Plans` class or directly such as:

    Plan plan = new Plan() {
      public Directive apply(Throwable cause) {
        if (cause instanceof ConnectionFailedException)
          return Directive.Retry(5, Duration.min(1));
        if (cause instanceof ConnectionClosedException)
          return Directive.Rethrow;
      }
    };

	
## Thanks

Sarge was inpsired by [Erlang OTP's](http://www.erlang.org/doc/design_principles/des_princ.html) supervision trees and [Akka's supervision](http://akka.io) implementation. Thanks to the their contributors for the great work.

## License

Copyright 2012 Jonathan Halterman - Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

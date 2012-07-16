# Sarge 0.1.0

Simple object supervision (for when stuff goes wrong).

## Introduction

Sarge is supervision for your objects. When failures occur, sarge whips your objects into for you by performing retries, state resets, and failure escalation. 

Sarge allows you to build supervision trees that can handle failures separate from traditional try/catch logic, where failures can be escalated up the tree as necessary. The concept was adapted from [Erlang OTP's](http://www.erlang.org/doc/design_principles/des_princ.html) supervision trees.

## Example

**Simple supervision**

	Sarge sarge = new Sarge();

    // Make a supervision plan
    Plan plan = Plans
      .retryOn(ConnectionFailedException.class, 5, Duration.mins(1))
      .rethrowOn(IllegalArgumentException.class, IllegalStateException.class)
      .make();
     
    // Create an instance of a class, with failures handled according to the given plan
    Something s = Supervision.supervised(Something.class, plan);
     
    // Supervision is applied automatically when something goes wrong
    s.doSomething();
    
**Hierarchical supervision**

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
     
    SomeParent p = new SomeParent();
     
    // Create an instance of a class, supervised by the parent
    SomeChild c = Supervision.supervised(SomeChild.class, someParent);
     
    // Supervision is applied automatically when something goes wrong
    s.doSomething();    

	
## Gratitude

Sarge was inpsired by [Akka's supervision](http://akka.io). Thanks to the Akka team for their great work.

## License

Copyright 2012 Jonathan Halterman - Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

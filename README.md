# Sarge 0.1.0

Simple object supervision (for when stuff goes wrong).

## Introduction

Sarge is supervision for your objects. When failures occur, sarge whips your objects into for you by performing retries, state resets, and failure escalation.

## Example

**Simple supervision**

    // Make a supervision plan
    Plan plan = Plans
      .retryOn(ConnectionFailedException.class, 5, Duration.mins(1))
      .rethrowOn(IllegalArgumentException.class, IllegalStateException.class)
      .make();

    // Create an instance of a class with failures handled according to the plan
	Sarge sarge = new Sarge();    
    Something s = sarge.supervise(Something.class, plan);
     
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
     
    SomeParent parent = new SomeParent();
     
    // Create an instance of a class supervised by the parent
    Sarge sarge = new Sarge();
    SomeChild c = sarge.supervise(SomeChild.class, parent);
     
    // Supervision is applied automatically when something goes wrong
    s.doSomething();
    
	// We can also link additional objects into the supervision hierarchy
	sarge.link(uberParent, someParent);
	
## More

Sarge allows you to build supervision trees that can handle escalating failures separate from traditional try/catch logic. The concept was adapted from [Erlang OTP's](http://www.erlang.org/doc/design_principles/des_princ.html) supervision trees.

	
## Gratitude

Sarge was inpsired by [Akka's supervision](http://akka.io). Thanks to the Akka team for their great work.

## License

Copyright 2012 Jonathan Halterman - Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).

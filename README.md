# Sarge 0.1.0

*Simple object supervision (for when stuff goes wrong)*

Sarge creates *supervised* objects which *automatically* handle failures when they occur by performing retries, state resets, and failure escalation, allowing for easy and robust fault tolerance with little effort.

## Usage

Sarge handles failures according to a `Plan` which takes a failure and directs Sarge to do something with it. Creating a `Plan` is straightforward:

    Plan plan = Plans
      .retryOn(TimeoutException.class, 5, Duration.mins(1))
      .escalateOn(ConnectionClosedException.class)
      .rethrowOn(IllegalArgumentException.class, IllegalStateException.class)
      .make();
      
This Plan retries any method invocations that fail with a TimeoutException, escalates any ConnectionClosedExceptions, and rethrows any IllegalArgumentExceptions and IllegalStateExceptions.      

#### Supervision

With our `Plan` in hand, we can create a *supervised* object:

	Sarge sarge = new Sarge();
    MailService s = sarge.supervise(MailService.class, plan);

Supervision is automatically applied according to the plan when any exception occurs when invoking a method against the object:
    
    // Failures are handled according to the plan
    s.sendMail();
    
#### Hierarchical supervision

Sarge can create a parent/child supervision hierarchy where the `Supervisor`'s plan is applied to any failures that occur in the child:

    class Parent implements Supervisor {
      @Override
      public void plan(){
        Plans
	      .retryOn(TimeoutException.class, 5, Duration.mins(1))
          .escalateOn(ConnectionClosedException.class)                    
          .make(); 
      }
    }
     
    Parent parent = new Parent();
    Sarge sarge = new Sarge();
     
    // Create a Child that is supervised by the parent
    Child c = sarge.supervise(Child.class, parent);
    
We can link additional objects into the supervision hierarchy, which will handle any failures that are escalated:
    
	sarge.link(uberParent, parent);
	
#### More on plans

Aside from the `Plans` class, Plans can also be constructed directly by implementing the `Plan` interface and returning the desired `Directive` for handling each failure:

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

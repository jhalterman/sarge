package net.jodah.sarge;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotates a class that is capable of being supervised. Only required for child classes that do
 * not implement {@link SelfSupervisor}.
 * 
 * @author Jonathan Halterman
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Supervisable {
}

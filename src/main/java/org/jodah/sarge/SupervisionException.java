package org.jodah.sarge;

/**
 * @author Jonathan Halterman
 */
public class SupervisionException extends RuntimeException {
  private static final long serialVersionUID = 0;

  public SupervisionException(Exception cause) {
    super(cause);
  }
}

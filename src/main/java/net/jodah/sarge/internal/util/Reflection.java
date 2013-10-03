package net.jodah.sarge.internal.util;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public final class Reflection {
  private Reflection() {
  }

  /**
   * Returns a simplified String representation of the {@code member}.
   */
  public static String toString(Member member) {
    if (member instanceof Method)
      return member.getDeclaringClass().getSimpleName() + "." + member.getName() + "()";
    return null;
  }
}

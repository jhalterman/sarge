package org.sarge.internal.util;

public interface Function<F, T> {
  T apply(F from);
}

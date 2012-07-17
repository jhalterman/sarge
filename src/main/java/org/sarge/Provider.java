package org.sarge;

/**
 * Provides instances of type {@code T}.
 * 
 * @param <T> type to provide
 * @author Jonathan Halterman
 */
public interface Provider<T> {
  /**
   * Returns an instance of the requested type {@code T} else {@code null} if the requested type
   * cannot be provided.
   * 
   * @param requestedType to provide an instance of
   * @throws MappingException if an error occurs while providing the requested type
   */
  T get(Class<T> requestedType);
}

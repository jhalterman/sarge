package org.sarge.internal.util;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Adapted from com.yammer.dropwizard.logging.Log.
 */
public class Log {
  private final Logger logger;

  public static Log forClass(Class<?> klass) {
    return new Log((Logger) LoggerFactory.getLogger(klass));
  }

  private Log(Logger logger) {
    this.logger = logger;
  }

  // TRACE

  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  public void trace(String message) {
    logger.trace(message);
  }

  public void trace(String message, Object... args) {
    if (isTraceEnabled()) {
      logger.trace(MessageFormatter.arrayFormat(message, args).getMessage());
    }
  }

  public void trace(Throwable e, String message, Object... args) {
    if (isTraceEnabled()) {
      logger.trace(MessageFormatter.arrayFormat(message, args).getMessage(), e);
    }
  }

  // DEBUG

  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  public void debug(String message) {
    logger.debug(message);
  }

  public void debug(String message, Object... args) {
    if (isDebugEnabled()) {
      logger.debug(MessageFormatter.arrayFormat(message, args).getMessage());
    }
  }

  public void debug(Throwable e, String message, Object... args) {
    if (isDebugEnabled()) {
      logger.debug(MessageFormatter.arrayFormat(message, args).getMessage(), e);
    }
  }

  // INFO

  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  public void info(String message) {
    logger.info(message);
  }

  public void info(String message, Object... args) {
    if (isInfoEnabled()) {
      logger.info(MessageFormatter.arrayFormat(message, args).getMessage());
    }
  }

  public void info(Throwable e, String message, Object... args) {
    if (isInfoEnabled()) {
      logger.info(MessageFormatter.arrayFormat(message, args).getMessage(), e);
    }
  }

  // WARN

  public void warn(String message) {
    logger.warn(message);
  }

  public void warn(String message, Object... args) {
    logger.warn(MessageFormatter.arrayFormat(message, args).getMessage());
  }

  public void warn(Throwable e, String message, Object... args) {
    logger.warn(MessageFormatter.arrayFormat(message, args).getMessage(), e);
  }

  // ERROR

  public void error(String message) {
    logger.error(message);
  }

  public void error(String message, Object... args) {
    logger.error(MessageFormatter.arrayFormat(message, args).getMessage());
  }

  public void error(Throwable e, String message, Object... args) {
    logger.error(MessageFormatter.arrayFormat(message, args).getMessage(), e);
  }

  public Level getLevel() {
    return logger.getEffectiveLevel();
  }
}

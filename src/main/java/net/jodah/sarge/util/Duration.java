package net.jodah.sarge.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import net.jodah.sarge.internal.util.Assert;

/**
 * Adapted from com.yammer.dropwizard.uti.Duration.
 */
public class Duration {
  static final Pattern PATTERN = Pattern.compile("[\\d]+[\\s]*(" + "ns|nanosecond(s)?|"
      + "us|microsecond(s)?|" + "ms|millisecond(s)?|" + "s|second(s)?|" + "m|minute(s)?|"
      + "h|hour(s)?|" + "d|day(s)?" + ')');
  private static final Map<String, TimeUnit> SUFFIXES;
  private static final Duration INFINITE = new Duration();

  static {
    SUFFIXES = new HashMap<String, TimeUnit>();

    SUFFIXES.put("ns", TimeUnit.NANOSECONDS);
    SUFFIXES.put("nanosecond", TimeUnit.NANOSECONDS);
    SUFFIXES.put("nanoseconds", TimeUnit.NANOSECONDS);

    SUFFIXES.put("us", TimeUnit.MICROSECONDS);
    SUFFIXES.put("microsecond", TimeUnit.MICROSECONDS);
    SUFFIXES.put("microseconds", TimeUnit.MICROSECONDS);

    SUFFIXES.put("ms", TimeUnit.MILLISECONDS);
    SUFFIXES.put("millisecond", TimeUnit.MILLISECONDS);
    SUFFIXES.put("milliseconds", TimeUnit.MILLISECONDS);

    SUFFIXES.put("s", TimeUnit.SECONDS);
    SUFFIXES.put("second", TimeUnit.SECONDS);
    SUFFIXES.put("seconds", TimeUnit.SECONDS);

    SUFFIXES.put("m", TimeUnit.MINUTES);
    SUFFIXES.put("minute", TimeUnit.MINUTES);
    SUFFIXES.put("minutes", TimeUnit.MINUTES);

    SUFFIXES.put("h", TimeUnit.HOURS);
    SUFFIXES.put("hour", TimeUnit.HOURS);
    SUFFIXES.put("hours", TimeUnit.HOURS);

    SUFFIXES.put("d", TimeUnit.DAYS);
    SUFFIXES.put("day", TimeUnit.DAYS);
    SUFFIXES.put("days", TimeUnit.DAYS);
  }

  private final long count;
  private final TimeUnit unit;
  private final boolean finite;

  private Duration() {
    this.count = Long.MAX_VALUE;
    this.unit = TimeUnit.DAYS;
    finite = false;
  }

  private Duration(long count, TimeUnit unit) {
    this.count = count;
    this.unit = Assert.notNull(unit);
    finite = true;
  }

  public static Duration of(long count, TimeUnit unit) {
    return new Duration(count, unit);
  }

  public static Duration of(String duration) {
    Assert.isTrue(PATTERN.matcher(duration).matches(), "Invalid duration: %s", duration);
    int i = 0;
    for (; i < duration.length(); i++)
      if (Character.isLetter(duration.charAt(i)))
        break;
    String unit = duration.subSequence(0, i).toString().trim();
    String dur = duration.subSequence(i, duration.length()).toString();
    return new Duration(Long.parseLong(unit), SUFFIXES.get(dur));
  }

  public boolean isFinite() {
    return finite;
  }

  public static Duration inf() {
    return INFINITE;
  }

  public static Duration infinite() {
    return INFINITE;
  }

  public static Duration days(long count) {
    return new Duration(count, TimeUnit.DAYS);
  }

  public static Duration hours(long count) {
    return new Duration(count, TimeUnit.HOURS);
  }

  public static Duration microseconds(long count) {
    return new Duration(count, TimeUnit.MICROSECONDS);
  }

  public static Duration millis(long count) {
    return new Duration(count, TimeUnit.MILLISECONDS);
  }

  public static Duration milliseconds(long count) {
    return new Duration(count, TimeUnit.MILLISECONDS);
  }

  public static Duration mins(long count) {
    return new Duration(count, TimeUnit.MINUTES);
  }

  public static Duration minutes(long count) {
    return new Duration(count, TimeUnit.MINUTES);
  }

  public static Duration nanos(long count) {
    return new Duration(count, TimeUnit.NANOSECONDS);
  }

  public static Duration nanoseconds(long count) {
    return new Duration(count, TimeUnit.NANOSECONDS);
  }

  public static Duration secs(long count) {
    return new Duration(count, TimeUnit.SECONDS);
  }

  public static Duration seconds(long count) {
    return new Duration(count, TimeUnit.SECONDS);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if ((obj == null) || (getClass() != obj.getClass()))
      return false;
    final Duration duration = (Duration) obj;
    return (count == duration.count) && (unit == duration.unit);

  }

  @Override
  public int hashCode() {
    return (31 * (int) (count ^ (count >>> 32))) + unit.hashCode();
  }

  public long toDays() {
    return TimeUnit.DAYS.convert(count, unit);
  }

  public long toHours() {
    return TimeUnit.HOURS.convert(count, unit);
  }

  public long toMicros() {
    return TimeUnit.MICROSECONDS.convert(count, unit);
  }

  public long toMicroseconds() {
    return TimeUnit.MICROSECONDS.convert(count, unit);
  }

  public long toMillis() {
    return TimeUnit.MILLISECONDS.convert(count, unit);
  }

  public long toMilliseconds() {
    return TimeUnit.MILLISECONDS.convert(count, unit);
  }

  public long toMins() {
    return TimeUnit.MINUTES.convert(count, unit);
  }

  public long toMinutes() {
    return TimeUnit.MINUTES.convert(count, unit);
  }

  public long toNanos() {
    return TimeUnit.NANOSECONDS.convert(count, unit);
  }

  public long toNanoseconds() {
    return TimeUnit.NANOSECONDS.convert(count, unit);
  }

  public long toSecs() {
    return TimeUnit.SECONDS.convert(count, unit);
  }

  public long toSeconds() {
    return TimeUnit.SECONDS.convert(count, unit);
  }

  @Override
  public String toString() {
    String units = unit.toString().toLowerCase();
    if (count == 1)
      units = units.substring(0, units.length() - 1);
    return Long.toString(count) + ' ' + units;
  }
}

Datatype module to make Jackson (http://jackson.codehaus.org) recognize Java 8 Date & Time API data types (JSR-310).

## Status

As of version 2.4, available for standard JDK8 distributions.

Earlier versions were build against pre-release candidates of Java 8, as follows:

* Version 2.3.1 requires Java 1.8.0-ea-b128 and newer.
* Version 2.2.2-beta4 and 2.2.3-beta5 require Java 1.8.0-ea-b99 to 1.8.0-ea-b112.

[![Build Status](https://travis-ci.org/FasterXML/jackson-datatype-jsr310.svg)](https://travis-ci.org/FasterXML/jackson-datatype-jsr310)

## Summary

Most JSR-310 types are serialized as numbers (integers or decimals as appropriate) if the
[`SerializationFeature#WRITE_DATES_AS_TIMESTAMPS`](http://fasterxml.github.com/jackson-databind/javadoc/2.2.0/com/fasterxml/jackson/databind/SerializationFeature.html#WRITE_DATES_AS_TIMESTAMPS)
feature is enabled, and otherwise are serialized in standard [ISO-8601](http://en.wikipedia.org/wiki/ISO_8601)
string representation. ISO-8601 specifies formats for representing offset dates and times, zoned dates and times,
local dates and times, periods, durations, zones, and more. All JSR-310 types have built-in translation to and from
ISO-8601 formats.

Granularity of timestamps is controlled through the companion features
[`SerializationFeature#WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS`](http://fasterxml.github.com/jackson-databind/javadoc/2.2.0/com/fasterxml/jackson/databind/SerializationFeature.html#WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
and
[`DeserializationFeature#READ_DATE_TIMESTAMPS_AS_NANOSECONDS`](http://fasterxml.github.com/jackson-databind/javadoc/2.2.0/com/fasterxml/jackson/databind/DeserializationFeature.html#READ_DATE_TIMESTAMPS_AS_NANOSECONDS).
For serialization, timestamps are written as fractional numbers (decimals), where the number is seconds and the decimal
is fractional seconds, if `WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS` is enabled (it is by default), with resolution as fine
as nanoseconds depending on the underlying JDK implementation. If `WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS` is disabled,
timestamps are written as a whole number of milliseconds. At deserialization time, decimal numbers are always read as
fractional second timestamps with up-to-nanosecond resolution, since the meaning of the decimal is unambiguous. The
more ambiguous integer types are read as fractional seconds without a decimal point if
`READ_DATE_TIMESTAMPS_AS_NANOSECONDS` is enabled (it is by default), and otherwise they are read as milliseconds.

Some exceptions to this standard serialization/deserialization rule:
* [`Period`](http://download.java.net/jdk8/docs/api/java/time/Period.html), which always results in an ISO-8601 format
because Periods must be represented in years, months, and/or days.
* [`Year`](http://download.java.net/jdk8/docs/api/java/time/Year.html), which only contains a year and cannot be
represented with a timestamp.
* [`YearMonth`](http://download.java.net/jdk8/docs/api/java/time/YearMonth.html), which only contains a year and a month
and cannot be represented with a timestamp.
* [`MonthDay`](http://download.java.net/jdk8/docs/api/java/time/MonthDay.html), which only contains a month and a day and
cannot be represented with a timestamp.
* [`ZoneId`](http://download.java.net/jdk8/docs/api/java/time/ZoneId.html) and
[`ZoneOffset`](http://download.java.net/jdk8/docs/api/java/time/ZoneOffset.html), which do not actually store dates and
times but are supported with this module nonetheless.
* [`LocalDate`](http://download.java.net/jdk8/docs/api/java/time/LocalDate.html),
[`LocalTime`](http://download.java.net/jdk8/docs/api/java/time/LocalTime.html),
[`LocalDateTime`](http://download.java.net/jdk8/docs/api/java/time/LocalDateTime.html), and
[`OffsetTime`](http://download.java.net/jdk8/docs/api/java/time/OffsetTime.html), which cannot portably be converted to
timestamps and are instead represented as arrays when `WRITE_DATES_AS_TIMESTAMPS` is enabled.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.6.1</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

### Registering module

Starting with Jackson 2.2, `Module`s can be automatically discovered using the Java 6 Service Provider Interface (SPI) feature.
You can activate this by instructing an `ObjectMapper` to find and register all `Module`s:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```

You should use this feature with caution as it has performance implications. You should generally create one constant
`ObjectMapper` instance for your entire application codebase to share, or otherwise use one of `ObjectMapper`'s
`findModules` methods and cache the result.

If you prefer to selectively register this module, this is done as follows, without the call to
`findAndRegisterModules()`:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());
```

After either of these, functionality is available for all normal Jackson operations.

## More

See [Wiki](../../wiki) for more information
(JavaDocs, downloads).

Also: there is [JDK 1.7 backport](https://github.com/joschi/jackson-datatype-threetenbp) datatype module!

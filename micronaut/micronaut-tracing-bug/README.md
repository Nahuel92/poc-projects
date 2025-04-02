## Description

Micronaut opentracing (the latest version is 6.9.0 at the time of writing) has a dependency issue that causes the
following exception to be thrown:

```
    io.micronaut.context.exceptions.BeanInstantiationException: Error instantiating bean of type [io.opentelemetry.instrumentation.api.instrumenter.Instrumenter]
    ...
    Caused by: java.lang.NoClassDefFoundError: kotlin/jvm/internal/Intrinsics
```

## Expected behavior

OTEL to work out of the box

## Actual behavior

A `ClassNotFound` exception is thrown when running the app:

```
    io.micronaut.context.exceptions.BeanInstantiationException: Error instantiating bean of type [io.opentelemetry.instrumentation.api.instrumenter.Instrumenter]
    ...
    Caused by: java.lang.NoClassDefFoundError: kotlin/jvm/internal/Intrinsics
```

## How to test?

1. Run `MicronautTracingBugTest`. You will see one test failing (OTEL enabled) and one passing (OTEL disabled)

## Workaround

Add this dependency to the `pom.xml` file:

```xml

<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib</artifactId>
    <version>1.9.25</version>
    <scope>runtime</scope>
</dependency>
```

But this may not be appropriate for all projects.
## Description

Versions 4.7.4 of:

- The Micronaut parent
- Micronaut Core

Introduced a bug that was not present in versions:

- 4.6.2 of the Micronaut parent
- 4.6.5 Micronaut Core

When using `@Serdeable.Deserializable(as = ImmutableList.class)` (ImmutableList is from the Guava collections).

## Expected behavior

Deserialize without any errors as before

## How to test?

1. Run `SerdeableDeserializableTest`. You will see it failing
2. Go to `pom.xml` and comment-out current versions of both Micronaut parent and Core
3. Uncomment their previous versions
4. Clean-compile the project
5. Run again `SerdeableDeserializableTest`. You will that it passes now
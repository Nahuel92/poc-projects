## Micronaut Serialization Bug

Example project to attach to the bug report https://github.com/micronaut-projects/micronaut-serialization/issues/1040

### Description

Micronaut Serialization >= 2.12.1 introduced a bug that was not present in versions <= 2.12.0 when using
`@Serdeable.Deserializable(as...)` to customize the deserialized class.

In my particular case, I'm using `@Serdeable.Deserializable(as = ImmutableList.class)` (ImmutableList is from the Guava
collections).

### Expected behavior

Deserialize without any errors as before.

### How to test?

1. Run `MicronautSerializationBugTest#successOnGettingResponse`. You will see it failing
2. Go to `pom.xml` and replace `micronaut.serialization.version` with `2.12.0` (which is the latest working version)
3. Clean-compile the project
4. Run again `MicronautSerializationBugTest#successOnGettingResponse`. You will see that it passes now
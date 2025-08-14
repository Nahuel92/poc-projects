## Micronaut JWT Propagation Bug

Example project to attach to the bug report https://github.com/micronaut-projects/micronaut-security/issues/1161

### Description

JWT propagation doesn't work if JWTs don't contain a valid signature.

### Expected behavior

Automatic JWT propagation should work for services that are not themselves secured but needs to propagate JWT from the
request and use it with downstream services.

### Actual behavior

Automatic JWT propagation is not working

## How to test?

1. Run `RecordLombokBuilderTest`. You will see it failing
2. Go to `pom.xml` and comment-out current versions of both Micronaut parent and Core
3. Uncomment their previous versions
4. Clean-compile the project
5. Run again `RecordLombokBuilderTest`. You will that it passes now


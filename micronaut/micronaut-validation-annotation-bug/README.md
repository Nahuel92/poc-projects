## Micronaut 5.1.5 Validation Annotation Bug

PoC project for https://github.com/micronaut-projects/micronaut-core/issues/13202

### Description

A recent version of Micronaut parent introduced a bug that was not present in previous versions.

When using validation annotations (e.g.,`@NotNull`, `@NotEmpty`, etc.) in a service's constructor
that takes a collection (e.g., `Set<?>`, `List<?>`, etc.), I get:

```
Message: Cannot validate bean [io.github.nahuel92.MyService]. No bean introspection present.
Please add @Introspected.

Path Taken:
...
at io.micronaut.validation.validator.DefaultValidator.validateBean(DefaultValidator.java:784)
	at io.micronaut.inject.ValidatedBeanDefinition.validate(ValidatedBeanDefinition.java:42)
```

### Expected behavior

Validation annotations used in a service's constructor that takes a collection working as before.
Annotating a service with`@Introspected` doesn't make any sense because a service is not a
DTO/entity.

### How to test?

1. Run `MyServiceTest`. You will see it failing
2. Go to `pom.xml` and comment-out current version of Micronaut parent
3. Uncomment the previous Micronaut parent version
4. It might be required to clean the project
5. Run again `MyServiceTest`. You will see that it passes now
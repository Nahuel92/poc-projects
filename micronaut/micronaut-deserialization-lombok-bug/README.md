## Description

Versions 4.7.4 of:

- The Micronaut parent
- Micronaut Core

Introduced a bug that was not present in versions:

- 4.6.2 of the Micronaut parent
- 4.6.5 Micronaut Core

When using Java records plus `@Builder` from Lombok.

## Expected behavior

When using nested Java records, and:

- They are annotated with `@Builder`
- They are annotated with validation annotations

1. If the payload is null or empty, every field (including nested-record fields) should be validated.
2. If the payload is valid, the DTO should be deserialized correctly

## Actual behavior

1. If the payload is null or empty, nested-record fields aren't validated and the nested-record is shown as null:

   ### Output example using a newer Micronaut version (bugged)

    ```
    jakarta.validation.ConstraintViolationException:
    get.<return value>.id: must not be null,
    get.<return value>.commonDTO: must not be null  // should list validations for all of its fields
    ```

   ### Output example using a previous Micronaut version (not bugged)

    ```
    jakarta.validation.ConstraintViolationException:
    get.<return value>.id: must not be null,
    get.<return value>.commonDTO.commonField: must not be blank,
    get.<return value>.commonDTO.commonField2: must not be null
    ```

2. If the payload is valid, the DTO fails to be deserialized

   ### Output example using a newer Micronaut version (bugged)

    ```
    Expecting code not to raise a throwable but caught
    "jakarta.validation.ConstraintViolationException: get.<return value>.commonDTO: must not be null
    ```

   ### Output example using a previous Micronaut version (not bugged)

   Test passes without throwing and exception

## How to test?

1. Run `RecordLombokBuilderTest`. You will see it failing
2. Go to `pom.xml` and comment-out current versions of both Micronaut parent and Core
3. Uncomment their previous versions
4. Clean-compile the project
5. Run again `RecordLombokBuilderTest`. You will that it passes now
## Description

When using `@Embeddable`, if it has a validated field that is null/missing when reading from the DB, an exception is
thrown with an error message indicating that the whole embeddable is null/missing.

### Example of the current error message:

`Error instantiating entity [io.github.nahuel92.MyEntity]: Null argument specified for [auditFields]. 
If this argument is allowed to be null annotate it with @Nullable`

## Expected behavior

The error message should explicitly mention which field of the embeddable is null/missing. This way, developers could
quickly identify and fix the issue.

### Suggested error message:

`Error instantiating entity [io.github.nahuel92.MyEntity]: Null argument specified for [auditFields.createdBy]. 
If this argument is allowed to be null annotate it with @Nullable`

## How to test?

Run the `EmbeddableTest` class.
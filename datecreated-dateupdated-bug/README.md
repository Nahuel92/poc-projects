### Description

When using `@DateCreated` and/or `@DateUpdated` in an `@Embeddable` class that is used by other classes, fields are not
automatically filled in.

### Expected behavior

Fields annotated with `@DateCreated` and/or `@DateUpdated` should be automatically filled in.

### How to test?

Run the `DateCreatedDateUpdatedBugTest` class.
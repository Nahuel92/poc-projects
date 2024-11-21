### Description

The issue happens when mapping a DB field type `DATE` to a Java type `LocalDate`.

During tests, when saving, say 2024/01/01, and then using the `findById` method, the returned LocalDate is one day
behind the original one.

### Expected behavior

`LocalDate` should not be one day behind.

### How to test?

Run the `DateFieldBugTest` class.
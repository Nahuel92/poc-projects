## Micronaut Data Test Bug

Example project to attach to the bug report https://github.com/micronaut-projects/micronaut-data/issues/3058

### Description

While testing an app using Micronaut Data JDBC, I have the need of running queries to change data immediately before
calling the subject under test to simulate a rapid data burst that the SUT should be able to handle.

Using `JdbcOperations` to update data doesn't work as expected. It can change the data stored in a table, but the SUT is
not able to see changes, even when manually committing the transaction.

I narrowed-down the bug to a multithreading issue. If I execute my repository method in another thread, then the
repository won't see updates made in test code. But, if I execute my repository method in the same thread, then it can
see the updates and tests will pass.

### How to test?

Just run the included tests (each one has a, hopefully, meaningful description).
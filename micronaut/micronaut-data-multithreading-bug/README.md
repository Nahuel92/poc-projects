## Micronaut Data Multithreading Bug

Example project that might be related to the bug
report https://github.com/micronaut-projects/micronaut-core/issues/11116

I don't remember why I created this, thus this project may not be applicable anymore

### Description

When a DataSource is set as read-only,

### Expected behavior

> [!NOTE]
> My understanding is that a DataSource is created at app startup and shared among all app's threads.

Read-only behavior should be honored, even if repository code runs in another thread.

### How to test?

Simply run tests in the `MicronautMultiThreadingBugTest` class. I also added logs around the repository's methods to
make the issue more evident.
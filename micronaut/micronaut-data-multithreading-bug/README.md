### Description

When a DataSource is set as read-only,

### Expected behavior

> [!NOTE]
> My understanding is that a DataSource is created at app startup and shared among all app's threads.

Read-only behavior should be honored, even if repository code runs in another thread.

### How to test?

Simply run tests in the `MicronautMultiThreadingBugTest` class. I also added logs around the repository's methods to
make the issue more evident.
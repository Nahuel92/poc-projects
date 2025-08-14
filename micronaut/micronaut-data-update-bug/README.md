## Micronaut Data Update Bug

Example project to attach to the bug report https://github.com/micronaut-projects/micronaut-data/issues/3049

### Description

Using Micronaut Data JDBC and an entity without an ID assigned, an update attempt will be silently ignored, even when
the JavaDocs for
#CrudRepository.update mentions that an exception should be thrown in that case.

### How to test?

Just run the included tests.
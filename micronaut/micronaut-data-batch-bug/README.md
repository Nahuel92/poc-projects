## Micronaut Data JDBC Batch Bug

Example project to attach to the bug report https://github.com/micronaut-projects/micronaut-data/issues/3516

### Description

When creating a manual `exists(...)` method for a `@JdbcRepository`, for example:

```java

@Query("""
        SELECT EXISTS (
            SELECT 1
            FROM my_entity AS e
            WHERE (e.field1, e.field2) IN :myEntity
        )"""
)
boolean exists(MyEntity myEntity);
```

If the method is passed an entity without an `id`, the following exception is thrown:

```text
io.micronaut.data.exceptions.DataAccessException: Supplied entity is a transient instance: MyEntity[id=null, field1=test, field2=1]
```

The same happens with a batch `exist(...)` method, for example:

```java

@Query("""
        SELECT e
        FROM my_entity AS e
        WHERE (e.field1, e.field2) IN :myEntity"""
)
List<MyEntity> exists(Iterable<MyEntity> myEntity);
```

In this case, the exception thrown is:

```text
io.micronaut.data.exceptions.DataAccessException: Error executing SQL Query: SELECT e
FROM my_entity AS e
WHERE (e.field1, e.field2) IN ? Supplied entity is a transient instance: MyEntity[id=null, field1=test, field2=1]
```

### Expected behavior

Queries to be executed without issues.

### How to test?

Run the `MicronautDataBatchBugTest` class. It contains tests for both single `exists(...)` and batch `exist(...)`
methods.
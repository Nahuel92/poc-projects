## Micronaut Data JDBC Bug

Example project to attach to the bug report https://github.com/micronaut-projects/micronaut-data/issues/3505

### Description

When creating a manual read-only `@Query` that returns:

- INSERT
- UPDATE
- DELETE

As a value, Micronaut Data JDBC misinterpret it as if those values where the reserved words from SQL, leading to an
unexpected operation.

Query examples:

```sql
SELECT a.id        AS id,
       a.attribute AS attribute,
       CASE
           WHEN a.another_attribute = 'c' THEN 'INSERT'
           WHEN a.another_attribute = 'u' THEN 'UPDATE'
           WHEN a.another_attribute = 'd' THEN 'DELETE'
           ELSE 'UNKNOWN'
           END     AS operation
FROM my_table AS a
```

```sql
SELECT *
FROM my_table AS a -- Comments are also affected if you use [ INSERT/insert | DELETE/delete | /UPDATE/update ]
```

```sql
SELECT *
FROM my_table AS a /* Comments are also affected if you use [ INSERT/insert | DELETE/delete | /UPDATE/update ] */
```

### Expected behavior

Query to be executed without issues.

### How to test?

Run the `MicronautDataJdbcBugTest` class.
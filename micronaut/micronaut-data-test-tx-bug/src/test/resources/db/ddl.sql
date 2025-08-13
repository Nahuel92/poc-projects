DROP TABLE IF EXISTS my_entity;

CREATE TABLE IF NOT EXISTS my_entity
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    first_name
    VARCHAR,
    last_name
    VARCHAR,
    enabled
    BOOL
);
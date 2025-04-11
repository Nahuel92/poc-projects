CREATE TABLE IF NOT EXISTS public.my_users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR                 NOT NULL,
    password   VARCHAR,
    first_name VARCHAR,
    last_name  VARCHAR,
    email      VARCHAR,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL
);
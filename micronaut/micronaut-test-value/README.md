## Description

When using `@Value` with the `${}` syntax as a test parameter, this exception is thrown:
`Unresolvable property specified to @Value`.

## Expected behavior

`@Value` to be resolved regardless of the syntax used, for example:

- @Value("${myProp}")
- @Value("myProp")

## How to test?

Run the MicronautTestValueTest class.
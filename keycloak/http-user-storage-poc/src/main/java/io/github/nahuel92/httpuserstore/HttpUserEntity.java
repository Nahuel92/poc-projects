package io.github.nahuel92.httpuserstore;

public record HttpUserEntity(String username, String password, String email, String firstName, String lastName) {
}
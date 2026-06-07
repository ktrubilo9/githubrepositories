package com.ktrubilo9.githubrepositories;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("Github user '%s' was not found".formatted(username));
    }
}

package com.ktrubilo9.githubrepositories;

public record ApiError(
        int status,
        String message
) {
}

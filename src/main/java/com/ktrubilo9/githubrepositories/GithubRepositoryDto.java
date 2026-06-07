package com.ktrubilo9.githubrepositories;

public record GithubRepositoryDto(
        String name,
        GithubOwnerDto owner,
        boolean fork
) {
}

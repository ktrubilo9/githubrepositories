package com.ktrubilo9.githubrepositories;

public record GithubBranchDto(
        String name,
        GithubBranchCommitDto commit
) {
}

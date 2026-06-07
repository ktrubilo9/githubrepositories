package com.ktrubilo9.githubrepositories;

public record BranchResponse(
        String name,
        String lastCommitSha
) {
    public static BranchResponse from(GithubBranchDto githubBranchDto) {
        return new BranchResponse(
                githubBranchDto.name(),
                githubBranchDto.commit().sha()
        );
    }
}

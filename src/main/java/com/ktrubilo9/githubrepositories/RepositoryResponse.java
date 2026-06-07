package com.ktrubilo9.githubrepositories;

import java.util.List;

public record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchResponse> branches
) {
}

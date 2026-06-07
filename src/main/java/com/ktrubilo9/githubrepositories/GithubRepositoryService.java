package com.ktrubilo9.githubrepositories;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GithubRepositoryService {

    private final GithubClient githubClient;

    public GithubRepositoryService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public List<RepositoryResponse> getRepositories(String user) {
        List<GithubRepositoryDto> repos = githubClient.getUserRepositories(user);

        List<RepositoryResponse> repositories = new ArrayList<>();
        for (GithubRepositoryDto githubRepositoryDto : repos) {
            if(githubRepositoryDto.fork())
                continue;

            List<GithubBranchDto> githubBranches = githubClient.getRepositoryBranches(
                    githubRepositoryDto.owner().login(),
                    githubRepositoryDto.name()
            );

            repositories.add(new RepositoryResponse(
                    githubRepositoryDto.name(),
                    githubRepositoryDto.owner().login(),
                    githubBranches.stream().map(BranchResponse::from).toList()
            ));
        }

        return repositories;
    }

}

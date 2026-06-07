package com.ktrubilo9.githubrepositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GithubClient {
    private final RestClient restClient;

    public GithubClient(
            RestClient.Builder restClientBuilder,
            @Value("${github.api.base-url}") String githubApiBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(githubApiBaseUrl)
                .build();
    }

    public List<GithubRepositoryDto> getUserRepositories(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new UserNotFoundException(username);
                        }
                )
                .body(new ParameterizedTypeReference<List<GithubRepositoryDto>>() {});
    }

    public List<GithubBranchDto> getRepositoryBranches(String owner, String repository) {
        return restClient.get()
                .uri("/repos/{owner}/{repository}/branches", owner, repository)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubBranchDto>>() {});
    }
}

package com.ktrubilo9.githubrepositories;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {
    private final GithubRepositoryService githubRepositoryService;

    public GithubController(GithubRepositoryService githubRepositoryService) {
        this.githubRepositoryService = githubRepositoryService;
    }

    @GetMapping("/users/{username}/repositories")
    public List<RepositoryResponse> getRepositories(@PathVariable String username) {
        return githubRepositoryService.getRepositories(username);
    }
}

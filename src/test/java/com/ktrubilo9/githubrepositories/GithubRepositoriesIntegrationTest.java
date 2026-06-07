package com.ktrubilo9.githubrepositories;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GithubRepositoriesIntegrationTest {
    private static final WireMockServer wireMock = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        wireMock.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("github.api.base-url", wireMock::baseUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @Test
    void returnsOnlyNonForkRepositoriesWithBranches() throws Exception {
        wireMock.stubFor(get(urlEqualTo("/users/test/repos"))
                .willReturn(okJson("""
                        [
                            {
                              "name": "owned",
                              "owner": {"login": "test"},
                              "fork": false
                            },
                            {
                              "name": "forked",
                              "owner": {"login": "test"},
                              "fork": true
                            }
                        ]
                        """)));
        wireMock.stubFor(get(urlEqualTo("/repos/test/owned/branches"))
                .willReturn(okJson("""
                        [
                          {"name": "main", "commit": {"sha": "aaa123"}},
                          {"name": "refactor", "commit": {"sha": "bbb456"}}
                        ]
                        """)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/users/test/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].repositoryName").value("owned"))
                .andExpect(jsonPath("$[0].ownerLogin").value("test"))
                .andExpect(jsonPath("$[0].branches.length()").value(2))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("aaa123"))
                .andExpect(jsonPath("$[0].branches[1].name").value("refactor"))
                .andExpect(jsonPath("$[0].branches[1].lastCommitSha").value("bbb456"));

        wireMock.verify(1, getRequestedFor(urlEqualTo("/repos/test/owned/branches")));
        wireMock.verify(0, getRequestedFor(urlEqualTo("/repos/test/forked/branches")));
    }

    @Test
    void returnsNotFoundIfUserDoesNotExist() throws Exception {
        wireMock.stubFor(get(urlEqualTo("/users/missing/repos"))
                .willReturn(notFound()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "message": "Not found",
                                    "documentation_url": "url",
                                    "status": "404"
                                }
                                """)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/github/users/missing/repositories"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Github user 'missing' was not found"));
    }
}

package com.reviewgenius.agent.service;

import com.reviewgenius.agent.model.ExistingReviewComment;
import com.reviewgenius.agent.model.Issue;
import com.reviewgenius.agent.model.PullRequestMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.reviewgenius.agent.util.CommonUtil.buildCommentBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubPullRequestService {

  private static final String GITHUB_BASE_API_URL = "https://api.github.com";
  private static final String PR_API_PATH = "/repos/{owner}/{repo}/pulls/{pullNumber}";

  private static final String REVIEW_API_PATH = "/repos/{owner}/{repo}/pulls/{pullNumber}/reviews";

  private final GitHubApiClient gitHubApiClient;

  public String fetchLatestCommitSha(String prUrl) {
    PullRequestMetadata metadata = extractPrMetadata(prUrl);
    String url = buildApiUrl(PR_API_PATH, metadata.getOwner(), metadata.getRepo(), metadata.getPullRequestNumber());
    return gitHubApiClient.fetchLatestCommitSha(prUrl, url);
  }

  public void addReviewComments(String prUrl, String commitId, List<Issue> issues) {
    PullRequestMetadata metadata = extractPrMetadata(prUrl);
    List<ExistingReviewComment> existingComments = fetchExistingReviewComments(metadata);
    String reviewApiUrl = buildApiUrl(
        REVIEW_API_PATH,
        metadata.getOwner(),
        metadata.getRepo(),
        metadata.getPullRequestNumber());

    List<Map<String, Object>> comments = issues.stream()
        .distinct()
        .filter(issue -> !isDuplicateComment(issue, existingComments))
        .filter(issue -> issue.getDiffPosition() != null)
        .map(this::buildComment)
        .toList();

    if (comments.isEmpty()) {
      log.info("No new review comments to add. prUrl={}", prUrl);
      return;
    }
    gitHubApiClient.addReviewComments(prUrl, getPayload(commitId, comments), reviewApiUrl);
  }

  private Map<String, Object> getPayload(String commitId, List<Map<String, Object>> comments) {
    return Map.of(
        "commit_id", commitId,
        "event", "COMMENT",
        "comments", comments);
  }

  public boolean isPullRequestOpen(String prUrl) {
    PullRequestMetadata metadata = extractPrMetadata(prUrl);
    String prApiUrl = buildApiUrl(PR_API_PATH, metadata.getOwner(), metadata.getRepo(),
        metadata.getPullRequestNumber());
    Map<String, Object> responseBody = gitHubApiClient.isPullRequestOpen(prApiUrl);
    log.info("GitHub PR response={}", responseBody);
    return responseBody != null && "open".equalsIgnoreCase((String) responseBody.get("state"));
  }

  private List<ExistingReviewComment> fetchExistingReviewComments(PullRequestMetadata metadata) {
    String url = buildApiUrl(PR_API_PATH + "/comments", metadata.getOwner(), metadata.getRepo(),
        metadata.getPullRequestNumber());
    List<Map<String, Object>> responseBody = gitHubApiClient.getExistingReviewComments(url);
    return responseBody.stream().map(this::mapToExistingComment).toList();

  }

  private ExistingReviewComment mapToExistingComment(Map<String, Object> comment) {
    return ExistingReviewComment.builder().path((String) comment.get("path"))
        .line(comment.get("line") != null ? ((Number) comment.get("line")).intValue() : null)
        .body((String) comment.get("body"))
        .build();
  }

  private boolean isDuplicateComment(Issue issue, List<ExistingReviewComment> existingComments) {
    return existingComments
        .stream()
        .anyMatch(existing -> Objects.equals(existing.getPath(), issue.getFileName())
            && Objects.equals(existing.getLine(), issue.getLineNumber()));
  }

  private Map<String, Object> buildComment(Issue issue) {
    if (issue.getDiffPosition() == null) {
      throw new IllegalStateException(
          "Missing diff position for issue");
    }
    return Map.of(
        "path", issue.getFileName(),
        // "line", issue.getLineNumber(),
        // "side", resolveReviewSide(issue),
        "position", issue.getDiffPosition(),
        "body", buildCommentBody(issue));
  }

  /*
   * private String resolveReviewSide(Issue issue) { return Objects.nonNull(issue.getSide()) ? issue.getSide().name() :
   * DEFAULT_REVIEW_SIDE; }
   */

  private String buildApiUrl(String path, String owner, String repo, Integer pullRequestNumber) {
    return UriComponentsBuilder
        .fromHttpUrl(GITHUB_BASE_API_URL)
        .path(path)
        .buildAndExpand(owner, repo, pullRequestNumber)
        .toUriString();
  }

  private PullRequestMetadata extractPrMetadata(String prUrl) {
    validatePullRequestUrl(prUrl);
    String cleanedUrl = prUrl.replace("https://github.com/", "");
    String[] parts = cleanedUrl.split("/");
    return PullRequestMetadata.builder()
        .owner(parts[0])
        .repo(parts[1])
        .pullRequestNumber(Integer.parseInt(parts[3]))
        .prUrl(prUrl)
        .build();
  }

  private void validatePullRequestUrl(String prUrl) {
    if (!prUrl.contains("/pull/")) {
      throw new IllegalArgumentException("Invalid GitHub pull request URL: " + prUrl);
    }
  }
}

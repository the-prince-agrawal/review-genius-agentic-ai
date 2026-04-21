package com.reviewgenius.agent.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

  @NotBlank(message = "PR URL cannot be empty")
  @Pattern(regexp = "https://github.com/.*/.*/pull/\\d+/?", message = "Invalid GitHub PR URL")
  private String prURL;

  @NotBlank(message = "Review type cannot be empty")
  private String reviewType;
}

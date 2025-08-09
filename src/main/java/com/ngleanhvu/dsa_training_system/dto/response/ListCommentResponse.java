package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListCommentResponse {
    private List<CommentResponse> comments;
    private Integer page;
    private Integer totalPages;
}

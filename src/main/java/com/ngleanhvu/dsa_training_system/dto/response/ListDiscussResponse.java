package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListDiscussResponse {
    private List<DiscussResponse> discuss;
    private int page;
    private int totalPages;
}

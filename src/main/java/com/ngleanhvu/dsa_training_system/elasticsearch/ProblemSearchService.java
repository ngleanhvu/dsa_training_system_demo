package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.ProblemDocumentResponse;

import java.util.List;


public interface ProblemSearchService {
    List<ProblemDocumentResponse> search(ProblemSearchRequest request, PagingSearch pagingSearch);
}

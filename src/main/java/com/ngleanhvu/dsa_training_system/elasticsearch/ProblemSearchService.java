package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.ngleanhvu.dsa_training_system.dto.response.ListProblemDocumentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;

public interface ProblemSearchService {
    ListProblemDocumentResponse search(ProblemSearchRequest request, PagingSearch pagingSearch);
}

package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import org.springframework.data.domain.Page;


public interface ProblemSearchService {
    Page<ProblemDocument> search(ProblemSearchRequest request, PagingSearch pagingSearch);
}

package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.document.ProblemDocument;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProblemSearchService {
    Page<ProblemDocument> search(ProblemSearchRequest request, PagingSearch pagingSearch);
}

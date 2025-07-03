package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.document.ProblemDocument;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.repo.impl.ProblemDocumentRepoImpl;
import com.ngleanhvu.dsa_training_system.service.ProblemSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemSearchServiceImpl implements ProblemSearchService {

    private final ProblemDocumentRepoImpl problemDocumentRepoImpl;

    @Override
    public Page<ProblemDocument> search(ProblemSearchRequest request, PagingSearch pagingSearch) {
        return problemDocumentRepoImpl.search(request, pagingSearch);
    }
}

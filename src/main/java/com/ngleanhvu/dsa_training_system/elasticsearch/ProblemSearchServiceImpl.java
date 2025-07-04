package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemSearchServiceImpl implements ProblemSearchService {

    private final ProblemDocumentRepoImpl problemDocumentRepoImpl;


    @Override
    public Page<ProblemDocument> search(ProblemSearchRequest request, PagingSearch pagingSearch) {
        return problemDocumentRepoImpl.search(request, pagingSearch);
    }
}

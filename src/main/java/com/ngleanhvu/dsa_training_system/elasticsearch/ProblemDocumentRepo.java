package com.ngleanhvu.dsa_training_system.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProblemDocumentRepo extends ElasticsearchRepository<ProblemDocument, Integer> {
}

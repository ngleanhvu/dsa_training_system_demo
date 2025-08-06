package com.ngleanhvu.dsa_training_system.elasticsearch;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void createProblemIndex() {
        try {
            boolean exists = elasticsearchClient.indices()
                    .exists(e -> e.index("problem_index"))
                    .value();

            if (!exists) {
                CreateIndexResponse response = elasticsearchClient.indices().create(c -> c
                        .index("problem_index")
                        .mappings(m -> m
                                .properties("id", p -> p.keyword(k -> k))
                                .properties("title", p -> p.text(t -> t
                                        .fields("keyword", f -> f.keyword(k -> k))
                                ))
                                .properties("url", p -> p.keyword(k -> k))
                                .properties("difficultyId", p -> p.integer(i -> i))
                                .properties("difficultyName", p -> p.keyword(k -> k))
                                .properties("topic", p -> p.integer(i -> i))
                                .properties("topicTitle", p -> p.keyword(k -> k))
                                .properties("acceptanceRate", p -> p.double_(d -> d))
                                .properties("isPublic", p -> p.boolean_(b -> b))
                                .properties("createdAt", p -> p.date(d -> d))
                        )
                );

                if (response.acknowledged()) {
                    System.out.println("Index 'problem_index' created successfully.");
                }
            } else {
                System.out.println("Index 'problem_index' already exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

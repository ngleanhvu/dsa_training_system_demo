package com.ngleanhvu.dsa_training_system.elasticsearch;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "problem_index")
@Data @Builder
public class ProblemDocument {
    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private Integer problemId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String slug;

    @Field(type = FieldType.Object)
    private DifficultyDocument difficulty;

    @Field(type = FieldType.Integer)
    private List<Integer> topic;

    @Field(type = FieldType.Double)
    private BigDecimal acceptanceRate;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static
    class DifficultyDocument {
        private Integer id;
        private String name;
    }


}


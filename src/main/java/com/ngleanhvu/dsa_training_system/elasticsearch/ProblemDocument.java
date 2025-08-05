package com.ngleanhvu.dsa_training_system.elasticsearch;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "problem_index")
public class ProblemDocument {
    @Id
    @Field(type = FieldType.Keyword)
    private Integer id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Integer)
    private Integer difficultyId;

    @Field(type = FieldType.Keyword)
    private String difficultyName;

    @Field(type = FieldType.Integer)
    private List<Integer> topic;

    @Field(type = FieldType.Keyword)
    private List<String> topicTitle;

    @Field(type = FieldType.Double)
    private double acceptanceRate;

    @Field(type = FieldType.Boolean)
    private boolean isPublic;

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate createdAt;
}


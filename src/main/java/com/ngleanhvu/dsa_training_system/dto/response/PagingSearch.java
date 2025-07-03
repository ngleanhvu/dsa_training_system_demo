package com.ngleanhvu.dsa_training_system.dto.response;


import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class PagingSearch {
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String direction = "asc";

    public Pageable toPageable() {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}
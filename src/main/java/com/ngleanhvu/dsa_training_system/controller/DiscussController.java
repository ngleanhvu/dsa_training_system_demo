package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/discuss")
public class DiscussController {

    private final DiscussService discussService;

    @PostMapping
    public ResponseEntity<?> createDiscuss(@RequestBody DiscussCreateRequest discussCreateRequest) {
        discussService.createDiscuss(discussCreateRequest);
        var response = ApiResponse.builder()
                .message("Discuss create success")
                .metadata(null)
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getDiscusses (@RequestParam(required = false, defaultValue = "") String keyword,
                                           @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                           @RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "10") int size,
                                           @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setPage(page);
        pagingSearch.setSize(size);
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setDirection(sortDir);

        var response = discussService.getDiscusses(keyword, pagingSearch);

        var apiResponse = ApiResponse.builder()
                .message("Get discusses success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}

package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/discuss")
@Slf4j
public class DiscussController {

    private final DiscussService discussService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<?> createDiscuss(@RequestBody DiscussCreateRequest discussCreateRequest,
                                           @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserIdFromToken(authHeader);
        discussCreateRequest.setUserId(userId);
        discussService.createDiscuss(discussCreateRequest);
        var response = ApiResponse.builder()
                .message("Discuss create success")
                .metadata(null)
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getDiscusses (@RequestBody DiscussFilterRequest discussFilterRequest,
                                           @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                           @RequestParam(required = false, defaultValue = "1") int page,
                                           @RequestParam(required = false, defaultValue = "10") int size,
                                           @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setPage(page > 0 ? page - 1 : 0);
        pagingSearch.setSize(size);
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setDirection(sortDir);

        var response = discussService.getDiscusses(discussFilterRequest, pagingSearch);

        var apiResponse = ApiResponse.builder()
                .message("Get discusses success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{discussId}")
    public ResponseEntity<?> updateDiscuss(@RequestBody DiscussUpdateRequest discussUpdateRequest,
                                           @PathVariable("discussId") Integer discussId,
                                           @RequestHeader("Authorization") String token) {

        String userId = jwtUtil.getUserIdFromToken(token);

        discussUpdateRequest.setUserId(userId);

        discussService.updateDiscuss(discussId, discussUpdateRequest);

        var response = ApiResponse.builder()
                .message("Discuss update success")
                .metadata(null)
                .status(HttpStatus.OK.name())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{discussId}")
    public ResponseEntity<?> deleteDiscuss(@PathVariable("discussId") Integer discussId,
                                           @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        discussService.deleteDiscuss(userId, discussId);
        var response = ApiResponse.builder()
                .message("Discuss delete success")
                .metadata(null)
                .status(HttpStatus.NO_CONTENT.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{discussId}")
    public ResponseEntity<?> getDiscuss(@PathVariable("discussId") Integer discussId) {
        DiscussResponse discussResponse = discussService.getDiscussById(discussId);
        var response = ApiResponse.builder()
                .message("Get discuss success")
                .metadata(discussResponse)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{discussId}/toggle")
    public ResponseEntity<?> toggleVote(@RequestHeader("Authorization") String token,
                                        @PathVariable("discussId") Integer discussId) {
        String userId = jwtUtil.getUserIdFromToken(token);
        discussService.toggleVote(userId, discussId);
        var response = ApiResponse.builder()
                .message("Discuss toggle success")
                .metadata(null)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

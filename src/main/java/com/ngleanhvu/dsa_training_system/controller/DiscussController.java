package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import jakarta.validation.Valid;
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
    @PostMapping("/create")
    public ResponseEntity<?> createDiscuss(@Valid @RequestBody DiscussCreateRequest discussCreateRequest,
                                           @RequestHeader("Authorization") String token) {
        log.info("discuss create request: {}", discussCreateRequest);
        String userId = jwtUtil.getUserIdFromToken(token);
        log.info("userId: {}", userId);
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
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDir, Math.max(page-1,0), size);
        var response = discussService.getDiscusses(discussFilterRequest, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .message("Get discusses success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/search/credential")
    public ResponseEntity<?> getDiscussesWithCredentials (@RequestBody DiscussFilterRequest discussFilterRequest,
                                                          @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(required = false, defaultValue = "1") int page,
                                                          @RequestParam(required = false, defaultValue = "10") int size,
                                                          @RequestParam(required = false, defaultValue = "desc") String sortDir,
                                                          @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        PagingSearch pagingSearch = AppUtil.toPagingSearch(AppUtil.changeSortBy(sortBy), sortDir, Math.max(page-1,0), size);
        var response = discussService.getDiscussesWithUser(discussFilterRequest,userId,pagingSearch);
        var apiResponse = ApiResponse.builder()
                .message("Get discusses success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{discussId}")
    public ResponseEntity<?> updateDiscuss(@RequestBody DiscussUpdateRequest discussUpdateRequest,
                                           @PathVariable("discussId") Integer discussId,
                                           @RequestHeader("Authorization") String token) {
        log.info("discuss request: {}", discussUpdateRequest);
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

    @GetMapping("/detail/{discussId}")
    public ResponseEntity<?> getDiscuss(@PathVariable("discussId") Integer discussId) {
        DiscussDetailResponse discussResponse = discussService.getDiscussById(discussId);
        var response = ApiResponse.builder()
                .message("Get discuss success")
                .metadata(discussResponse)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/detail/credential/{discussId}")
    public ResponseEntity<?> getDiscussWithCredential(@PathVariable("discussId") Integer discussId,
                                                      @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        DiscussDetailResponse discussDetailResponse = discussService.getDiscussDetail(discussId, userId);
        var response = ApiResponse.builder()
                .message("Get discuss detail success")
                .metadata(discussDetailResponse)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{discussId}")
    public ResponseEntity<?> getDiscussForUpdate(@PathVariable("discussId") Integer discussId) {
        var response = discussService.getDiscussForUpdate(discussId);
        var apiResponse = ApiResponse.builder()
                .message("Get discuss success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
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

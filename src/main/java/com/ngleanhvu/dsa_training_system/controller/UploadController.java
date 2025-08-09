package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/upload")
@Slf4j
public class UploadController {
    private final S3Service s3Service;

    // Handle single file upload (for SunEditor image upload)
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadSingle(@RequestPart("files") MultipartFile file) throws IOException {
        log.info("Uploading file: " + file.getOriginalFilename());
        String url = s3Service.upload(file);

        // SunEditor expects this specific response format
        List<Map<String, String>> response = new ArrayList<>();
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("name", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));
        response.add(result);

        return ResponseEntity.ok().body(response);
    }

    // Optional: Keep this for multiple file uploads from other parts of your app
    @PostMapping(value = "/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMultiple(@RequestPart("files") List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = s3Service.upload(file);
            urls.add(url);
        }
        return ResponseEntity.ok().body(urls);
    }
}
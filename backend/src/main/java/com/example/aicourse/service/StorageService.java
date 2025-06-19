package com.example.aicourse.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile file) throws IOException;
    Resource load(String path);
}
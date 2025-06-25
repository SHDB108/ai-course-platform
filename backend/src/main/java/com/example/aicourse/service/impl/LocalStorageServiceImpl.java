package com.example.aicourse.service.impl;

import com.example.aicourse.config.StorageProperties;
import com.example.aicourse.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@ConditionalOnProperty(name="storage.type", havingValue="local", matchIfMissing=true)
public class LocalStorageServiceImpl implements StorageService {

    private final StorageProperties prop;

    @Autowired
    public LocalStorageServiceImpl(StorageProperties prop) {
        this.prop = prop;
    }
    @Override
    public String upload(MultipartFile file) throws IOException {
        Files.createDirectories(prop.getLocalPath());
        String newName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dst = prop.getLocalPath().resolve(newName);
        file.transferTo(dst);
        return newName;
    }

    @Override
    public Resource load(String path) {
        return new FileSystemResource(prop.getLocalPath().resolve(path));
    }

    @Override
    public void delete(String path) throws IOException {
        if (path == null || path.isBlank()) {
            return;
        }
        Path fileToDelete = prop.getLocalPath().resolve(path);
        Files.deleteIfExists(fileToDelete);
    }
}
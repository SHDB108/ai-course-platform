package com.example.aicourse.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile file) throws IOException;
    Resource load(String path);

    /**
     * 删除存储的文件
     * @param path 文件路径或文件名
     * @throws IOException 如果删除失败
     */
    void delete(String path) throws IOException;
}
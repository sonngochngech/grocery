package com.grocery.app.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadImage(String fileName, byte[] file) throws IOException;
}

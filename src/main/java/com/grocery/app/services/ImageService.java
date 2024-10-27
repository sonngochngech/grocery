package com.grocery.app.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ImageService {

    public Map<String,Object> uploadImage(MultipartFile file,String folderPath,String fileName) throws IOException;
}

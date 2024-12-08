package com.grocery.app.services.impl;

import com.cloudinary.Cloudinary;
import com.grocery.app.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadImage(String fileName, byte[] file) throws IOException {

        Map<String,Object> uploadOptions=new HashMap<>();
        uploadOptions.put("public_id",fileName);

        Map result =cloudinary.uploader().upload(file,uploadOptions);

        return (String) result.get("url");
    }
}

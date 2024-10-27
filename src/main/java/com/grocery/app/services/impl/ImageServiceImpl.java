package com.grocery.app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.grocery.app.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public class ImageServiceImpl implements ImageService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file,String folderPath,String fileName) throws IOException {
        Map result= cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
               "public_id",fileName,
               "public_id_prefix",folderPath
       ));
       return  Map.of(String.format(folderPath+"/%s",fileName),result.get("url"));
    }
}

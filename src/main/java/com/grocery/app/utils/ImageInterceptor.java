package com.grocery.app.utils;

import com.grocery.app.config.constant.ResCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

import static com.grocery.app.config.constant.AppConstants.FILE_SIZE;

public class ImageInterceptor implements HandlerInterceptor {

    private static final String[] ALLOWED_CONTENT_TYPES = {
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_GIF_VALUE
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MultipartFile file= getMultipartFile(request);
        if(file==null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(ResCode.FILE_NOT_FOUND.getCode());
            return false;
        }
        if(file.getSize()>FILE_SIZE){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(ResCode.FILE_EXCEED_SIZE.getCode());
            return false;
        }
        if(!IsImageType(file.getContentType())){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(ResCode.FILE_NOT_IMAGE.getCode());
            return false;
        }
        return true;
    }

    private MultipartFile getMultipartFile(HttpServletRequest request) {
        if(request instanceof MultipartHttpServletRequest multipartRequest) {
            return multipartRequest.getFile("file");
        }
        return null;
    }

    private boolean IsImageType(String contentType) {
        return Arrays.asList(ALLOWED_CONTENT_TYPES).contains(contentType);
    }

}

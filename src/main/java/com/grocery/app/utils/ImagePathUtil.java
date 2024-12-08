package com.grocery.app.utils;

public class ImagePathUtil {

    public static String setImagePath(String type, String name, Long id) {
        return type + "/" + name + "/" + id;
    }
}

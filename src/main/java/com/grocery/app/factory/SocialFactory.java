package com.grocery.app.factory;

import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.utils.social.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
//public class SocialFactory {
//
//    @Autowired
//    @Qualifier("googleService")
//    private SocialService googleService;
//
//    public  SocialService useService(String name) {
//        if(name.equals(AppConstants.AuthProviderType.facebook.toString())) {
//            return googleService;
//        }else if(name.equals(AppConstants.AuthProviderType.google.toString())) {
//            return googleService;
//        }
//        return null;
//    }
//}

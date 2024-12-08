package com.grocery.app.config.constant;

public class AppConstants {

    public static final  String[] ADMIN_URLS= { "/api/admin/**" };
    public static final  String[] PUBLIC_URLS= {  "/api/auth/**" };
    public static final  String[] USER_URLS= { "/api/user/**","/api/family/**","api/upload/**" };
    public static final Long ADMIN_ID= 101L;
    public static final Long USER_ID= 102L;
    public static enum SexType {
        MALE,
        FEMALE
    }
    public static  enum InvitationStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        EXPIRED

    }
    public static enum AuthProviderType {
        facebook,
        google
    }
    public  static final String VN="vn";
    public static  final String EN="en";
    public static  final Long ACCESS_TOKEN_LIFETIME= 86400000L;
    public static final Long REFRESH_TOKEN_LIFETIME= 2592000000L;
    public  static final String DATE_FORMAT= "yyyy-MM-dd";
    public static final Long INVITATION_EXPIRED_TIME= 604800000L;
    public  static final Integer FILE_SIZE= 1048576;
    public static final String AVATAR_PATH="avatar";






}

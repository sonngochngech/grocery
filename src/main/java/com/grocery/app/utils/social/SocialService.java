package com.grocery.app.utils.social;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface SocialService {

    public Object getUserCredential(String token) throws IOException, GeneralSecurityException;


}

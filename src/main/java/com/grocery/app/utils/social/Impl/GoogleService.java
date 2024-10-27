package com.grocery.app.utils.social.Impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.grocery.app.utils.social.SocialService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleService  implements SocialService {

    @Value("${google.client.id}")
    private  String GOOGLE_CLIENT_ID;

    public Object getUserCredential(String token) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),new GsonFactory())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();
        try{
            GoogleIdToken idToken = verifier.verify(token);
            if(idToken != null){
                return idToken.getPayload();
            }
        }catch (GeneralSecurityException | IOException e){
            e.printStackTrace();
        }
        return null;
    }

}

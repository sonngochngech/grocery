package com.grocery.app.factory;

import com.grocery.app.dto.ExpoNotiDTO;
import io.github.jav.exposerversdk.ExpoPushMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.grocery.app.config.constant.AppConstants.EXTERNAL_DATA;

@Component
@AllArgsConstructor

public class ExpoMessageFactory {

    public static ExpoPushMessage createExpoPushMessage(ExpoNotiDTO expoNotiDTO){
        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
          expoPushMessage.setTo(expoNotiDTO.getToken());
          expoPushMessage.setBody(expoNotiDTO.getBody());
          expoPushMessage.setData(new HashMap<>(Map.of(EXTERNAL_DATA,expoNotiDTO.getExternalData())));
          return expoPushMessage;
    }
}

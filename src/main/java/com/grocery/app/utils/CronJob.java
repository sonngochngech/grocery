package com.grocery.app.utils;

import com.grocery.app.dto.NotiDTO;
import com.grocery.app.dto.UserFridgeDTO;
import com.grocery.app.notification.NotificationFactory;
import com.grocery.app.notification.NotificationProducer;
import com.grocery.app.services.FridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CronJob {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private NotificationFactory notificationFactory;

    @Autowired
    private NotificationProducer notificationProducer;

    @Scheduled(cron = "0 * * * * *")
    public Void  sendExpriedFridgeItemsNoti(){
        List<UserFridgeDTO> userFridgeDTOS=fridgeService.findExpriedFridgeItems();
        if(userFridgeDTOS== null) return  null;
        if(userFridgeDTOS.isEmpty()) return  null;
        Map<String,String> result=new HashMap<>();
        userFridgeDTOS.forEach(userFridgeDTO -> {
            if(result.containsKey(userFridgeDTO.getEmail())){
                String items=result.get(userFridgeDTO.getEmail());
                items=items+"\n"+"Tủ lạnh: "+userFridgeDTO.getFridgeName() +"có thức ăn"+userFridgeDTO.getFoodName()+"hết hạn. Vui lòng kiểm tra";
                result.put(userFridgeDTO.getEmail(),items);
                return;
            }
            result.put(userFridgeDTO.getEmail(),userFridgeDTO.getFridgeName() +"-"+userFridgeDTO.getFoodName());
        });
        List<Long> fridgeItemIds=userFridgeDTOS.stream().map(UserFridgeDTO::getFridgeItemId).toList();

        fridgeService.isNotifiedItem(fridgeItemIds);

        result.forEach((email,content)->{
            NotiDTO noti=notificationFactory.sendExpriedNoti(email,content);
            notificationProducer.sendMessage(noti);

        });
        return  null;
    }




}

package com.grocery.app.utils;

import com.grocery.app.dto.NotiContentDTO;
import com.grocery.app.dto.NotiDTO;
import com.grocery.app.dto.NotificationDTO;
import com.grocery.app.dto.UserFridgeDTO;
import com.grocery.app.entities.Task;
import com.grocery.app.notification.NotificationFactory;
import com.grocery.app.notification.NotificationProducer;
import com.grocery.app.repositories.TaskRepo;
import com.grocery.app.services.FridgeService;
import com.grocery.app.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CronJob {

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private NotificationFactory notificationFactory;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private NotificationService notificationService;

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

    @Scheduled(cron = "0 0/1 * * * ?") // Chạy mỗi 1 giờ
    public Void sendTaskAlert() {
        // Lấy danh sách các task sắp xếp theo timestamp
        ArrayList<Task> availableTasks = taskRepo.findAllAndSortByTimestamp();
        if (availableTasks == null) return null;

        String headContent = "Bạn có nhiệm vụ sắp đến hạn (thời gian thực hiện nhỏ hơn 1 tiếng) - nhiệm vụ mua ";

        // Lấy thời gian hiện tại và thời gian 1 tiếng sau
        long now = System.currentTimeMillis();
        long oneHourFromNow = now + 3600 * 1000; // Thêm 1 tiếng (3600 giây * 1000 mili giây)

        for (Task task : availableTasks) {
            // Kiểm tra nếu task.getTimestamp() nằm trong khoảng 1 tiếng từ bây giờ
            if (task.getTimestamp().getTime() >= now && task.getTimestamp().getTime() <= oneHourFromNow) {
                String foodName = task.getFood().getName() + " số lượng " + task.getQuantity();

                NotiContentDTO notiContentDTO = NotiContentDTO.builder()
                        .title("Nhiệm vụ mới")
                        .type("task")
                        .message(headContent + foodName)
                        .externalData("nothing else")
                        .build();

                NotificationDTO notificationDTO = notificationService.saveNotification(
                        task.getShoppingList().getOwner().getId(),
                        List.of(task.getAssignee().getId()),
                        notiContentDTO
                );

                NotiDTO noti = notificationFactory.sendNotification(notificationDTO);
                notificationProducer.sendMessage(noti);

            }
        }
        return null;
    }

}

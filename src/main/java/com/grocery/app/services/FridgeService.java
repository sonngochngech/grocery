package com.grocery.app.services;

import com.grocery.app.dto.FridgeDTO;
import com.grocery.app.dto.FridgeItemDTO;
import com.grocery.app.dto.UserFridgeDTO;

import java.util.List;

public interface FridgeService {

    FridgeDTO getFridgeByFamily(Long familyId);
    FridgeDTO addItemToFridge(Long familyId, FridgeItemDTO fridgeItemDTO);
    FridgeDTO removeItemFromFridge(Long familyId, Long fridgeItemId);
    FridgeDTO updateItemInFridge(Long familyId, FridgeItemDTO fridgeItemDTO);
    List<UserFridgeDTO> findExpriedFridgeItems();
    Void isNotifiedItem(List<Long> ids);


}

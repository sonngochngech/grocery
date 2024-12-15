package com.grocery.app.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.FridgeDTO;
import com.grocery.app.dto.FridgeItemDTO;
import com.grocery.app.entities.Food;
import com.grocery.app.entities.Fridge;
import com.grocery.app.entities.FridgeItem;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FoodRepo;
import com.grocery.app.repositories.FridgeRepo;
import com.grocery.app.services.FoodService;
import com.grocery.app.services.FridgeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FridgeServiceImpl implements FridgeService {

    @Autowired
    private FridgeRepo fridgeRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FoodService foodService;

    @Override
    public FridgeDTO getFridgeByFamily(Long familyId) {
        Fridge fridge = fridgeRepo.findByFamilyId(familyId).orElseThrow(() -> new ServiceException(ResCode.FRIDGE_NOT_FOUND.getMessage(), ResCode.FRIDGE_NOT_FOUND.getCode()));
        return modelMapper.map(fridge, FridgeDTO.class);
    }

    @Override
    public FridgeDTO addItemToFridge(Long familyId, FridgeItemDTO fridgeItemDTO) {

        Food food=validate(fridgeItemDTO.getFood().getId());


        Fridge fridge = fridgeRepo.findByFamilyId(familyId).orElseThrow(() -> new ServiceException(ResCode.FRIDGE_NOT_FOUND.getMessage(), ResCode.FRIDGE_NOT_FOUND.getCode()));

        FridgeItem fridgeItem=modelMapper.map(fridgeItemDTO,FridgeItem.class);
        fridgeItem.setFood(food);
        fridgeItem.setFridge(fridge);
        fridge.getFridgeItemList().add(fridgeItem);
        fridge = fridgeRepo.save(fridge);
        return modelMapper.map(fridge, FridgeDTO.class);
    }

    @Override
    public FridgeDTO removeItemFromFridge(Long familyId, Long fridgeItemId) {
        Fridge fridge = fridgeRepo.findByFamilyId(familyId).orElseThrow(() -> new ServiceException(ResCode.FRIDGE_NOT_FOUND.getMessage(), ResCode.FRIDGE_NOT_FOUND.getCode()));
        FridgeItem fridgeItem = fridge.getFridgeItemList().stream().filter(item -> item.getId().equals(fridgeItemId)).findFirst().orElseThrow(() -> new ServiceException(ResCode.ITEM_NOT_FOUND.getMessage(), ResCode.ITEM_NOT_FOUND.getCode()));
        fridge.getFridgeItemList().remove(fridgeItem);
        fridge = fridgeRepo.save(fridge);
        return modelMapper.map(fridge, FridgeDTO.class);
    }


    @Override
    @Transactional
    public FridgeDTO updateItemInFridge(Long familyId, FridgeItemDTO fridgeItemDTO) {
        Food food=validate(fridgeItemDTO.getFood().getId());
        Fridge fridge = fridgeRepo.findByFamilyId(familyId).orElseThrow(() -> new ServiceException(ResCode.FRIDGE_NOT_FOUND.getMessage(), ResCode.FRIDGE_NOT_FOUND.getCode()));
        FridgeItem fridgeItem = fridge.getFridgeItemList().stream().filter(item -> item.getId().equals(fridgeItemDTO.getId())).findFirst().orElseThrow(() -> new ServiceException(ResCode.ITEM_NOT_FOUND.getMessage(), ResCode.ITEM_NOT_FOUND.getCode()));
        fridge.getFridgeItemList().remove(fridgeItem);
        fridgeItem = modelMapper.map(fridgeItemDTO, FridgeItem.class);
        fridgeItem.setFridge(fridge);
        fridgeItem.setFood(food);
        fridge.getFridgeItemList().add(fridgeItem);
        fridge = fridgeRepo.save(fridge);

        FridgeDTO dto= modelMapper.map(fridge, FridgeDTO.class);
        return dto;
    }

    private  Food validate(Long id){
        Food result = foodService.verifyFoodExistence(id);
        if(result==null){
            throw new ServiceException(ResCode.FOOD_NOT_FOUND.getMessage(), ResCode.FOOD_NOT_FOUND.getCode());
        }
        return  result;
    }
}

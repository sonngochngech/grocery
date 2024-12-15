package com.grocery.app.controllers;


import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.FridgeDTO;
import com.grocery.app.dto.FridgeItemDTO;
import com.grocery.app.exceptions.ControllerException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.AuthenticationService;
import com.grocery.app.services.FamilyService;
import com.grocery.app.services.FridgeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/family/fridges")
public class FridgeController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private FridgeService fridgeService;

    @Autowired
    private FamilyService familyService;


    @GetMapping("")
    private ResponseEntity<BaseResponse<FridgeDTO>> getFridge(@RequestParam Long familyId){
        Long userId = authenticationService.getCurrentUser().getId();
        boolean isMember = familyService.verifyMember(familyId,userId) || familyService.verifyOwner(familyId,userId);
        if(!isMember){
            throw  new ControllerException(ResCode.NOT_BELONG_TO_FAMILY.getMessage(), ResCode.NOT_BELONG_TO_FAMILY.getCode());
        }
        FridgeDTO fridgeDTO = fridgeService.getFridgeByFamily(familyId);
        BaseResponse<FridgeDTO> response = ResponseFactory.createResponse(fridgeDTO, ResCode.GET_FRIDGE_SUCCESSFULLY.getMessage(), ResCode.GET_FRIDGE_SUCCESSFULLY.getCode());


        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/add")
    private ResponseEntity<BaseResponse<FridgeDTO>> AddItemToFridge(@RequestParam Long familyId,@Valid @RequestBody FridgeItemDTO fridgeItemDTO){
        Long userId = authenticationService.getCurrentUser().getId();
        Boolean isOwner = familyService.verifyOwner(familyId,userId);
        if(!isOwner){
            throw  new ControllerException(ResCode.NOT_OWNER_OF_FAMILY.getMessage(), ResCode.NOT_OWNER_OF_FAMILY.getCode());
        }
        FridgeDTO fridge = fridgeService.addItemToFridge(familyId, fridgeItemDTO);
        BaseResponse<FridgeDTO> response = ResponseFactory.createResponse(fridge, ResCode.ADD_ITEM_TO_FRIDGE_SUCCESSFULLY.getMessage(), ResCode.ADD_ITEM_TO_FRIDGE_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/remove")
    private ResponseEntity<BaseResponse<FridgeDTO>> RemoveItemFromFridge(@RequestParam Long familyId, @RequestParam Long itemId){
        Long userId = authenticationService.getCurrentUser().getId();
        Boolean isOwner = familyService.verifyOwner(familyId,userId);
        if(!isOwner){
            throw  new ControllerException(ResCode.NOT_OWNER_OF_FAMILY.getMessage(), ResCode.NOT_OWNER_OF_FAMILY.getCode());
        }
        FridgeDTO fridge=fridgeService.removeItemFromFridge(familyId, itemId);
        BaseResponse<FridgeDTO> response = ResponseFactory.createResponse(fridge, ResCode.REMOVE_ITEM_FROM_FRIDGE_SUCCESSFULLY.getMessage(), ResCode.REMOVE_ITEM_FROM_FRIDGE_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    private ResponseEntity<BaseResponse<FridgeDTO>> UpdateItemInFridge(@RequestParam Long familyId, @RequestBody FridgeItemDTO fridgeItemDTO){
        Long userId = authenticationService.getCurrentUser().getId();
        Boolean isOwner = familyService.verifyOwner(familyId,userId);
        if(!isOwner){
            throw  new ControllerException(ResCode.NOT_OWNER_OF_FAMILY.getMessage(), ResCode.NOT_OWNER_OF_FAMILY.getCode());
        }
        FridgeDTO fridges = fridgeService.updateItemInFridge(familyId, fridgeItemDTO);
        BaseResponse<FridgeDTO> response = ResponseFactory.createResponse(fridges, ResCode.UPDATE_ITEM_IN_FRIDGE_SUCCESSFULLY.getMessage(), ResCode.UPDATE_ITEM_IN_FRIDGE_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}

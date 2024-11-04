package com.grocery.app.controllers;

import com.grocery.app.dto.ShoppingListDTO;
import com.grocery.app.dto.request.createRequest.CreateShoppingListRequest;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateShoppingListRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
import com.grocery.app.entities.Task;
import com.grocery.app.services.impl.ShoppingListServiceImpl;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public class ShoppingListController {
    private ShoppingListServiceImpl shoppingListService;

    public ResponseEntity<ShoppingListDTO> createShoppingList(CreateShoppingListRequest createShoppingListRequest){
        return null;
    }

    public ResponseEntity<ShoppingListDTO> getShoppingListById(long userId, long shoppingListId){
        return null;
    }

    public ResponseEntity<ArrayList<ShoppingListDTO>> getAllShoppingList(long userId, int from, int to){
        return null;
    }

    public ResponseEntity<ShoppingListDTO> updateShoppingList(UpdateShoppingListRequest updateShoppingListRequest){
        return null;
    }

    public ResponseEntity<ShoppingListDTO> deleteShoppingList(long userId, long shoppingListId){
        return null;
    }
}

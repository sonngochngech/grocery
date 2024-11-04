package com.grocery.app.services;

import com.grocery.app.dto.ShoppingListDTO;

import java.util.ArrayList;

public interface ShoppingListService {
    ShoppingListDTO createShoppingList(ShoppingListDTO shoppingListDTO);

    ShoppingListDTO getShoppingListById(long userId, long id);

    ArrayList<ShoppingListDTO> getAllShoppingList(long userId, int from, int to);

    ShoppingListDTO updateShoppingList(ShoppingListDTO shoppingListDTO);

    ShoppingListDTO deleteShoppingList(long userId, long id);
}

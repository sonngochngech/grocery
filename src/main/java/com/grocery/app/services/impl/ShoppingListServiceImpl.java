package com.grocery.app.services.impl;

import com.grocery.app.config.StatusConfig;
import com.grocery.app.dto.ShoppingListDTO;
import com.grocery.app.entities.ShoppingList;
import com.grocery.app.repositories.ShoppingListRepository;
import com.grocery.app.services.ShoppingListService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShoppingListDTO createShoppingList(ShoppingListDTO shoppingListDTO) {
        ShoppingList shoppingList = modelMapper.map(shoppingListDTO, ShoppingList.class);
        ShoppingList savedShoppingList = shoppingListRepository.save(shoppingList);
        return modelMapper.map(savedShoppingList, ShoppingListDTO.class);
    }

    @Override
    public Optional<ShoppingListDTO> getShoppingListById(long userId, long id) {
        return shoppingListRepository.findById(id)
                .filter(shoppingList -> shoppingList.getOwnerId() == userId)
                .map(shoppingList -> modelMapper.map(shoppingList, ShoppingListDTO.class));
    }

    @Override
    public ArrayList<ShoppingListDTO> getAllShoppingList(long userId, int from, int to) {
        List<ShoppingList> shoppingLists = shoppingListRepository.findAllByUserId(userId);
        if (from < 0 || to > shoppingLists.size() || from > to) {
            throw new IndexOutOfBoundsException("Invalid pagination parameters.");
        }

        List<ShoppingList> paginatedLists = shoppingLists.subList(from, to);
        return paginatedLists.stream()
                .map(shoppingList -> modelMapper.map(shoppingList, ShoppingListDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ShoppingListDTO updateShoppingList(ShoppingListDTO shoppingListDTO) {
        Optional<ShoppingList> existingShoppingListOpt = shoppingListRepository.findById(shoppingListDTO.getId());

        if (existingShoppingListOpt.isPresent()) {
            ShoppingList existingShoppingList = existingShoppingListOpt.get();
            modelMapper.map(shoppingListDTO, existingShoppingList);
            ShoppingList updatedShoppingList = shoppingListRepository.save(existingShoppingList);
            return modelMapper.map(updatedShoppingList, ShoppingListDTO.class);
        }

        return null; // Or throw an exception
    }

    @Override
    public ShoppingListDTO deleteShoppingList(long userId, long id) {
        ShoppingList shoppingList = shoppingListRepository.findById(id).orElse(null);

        if (shoppingList != null &&
                shoppingList.getOwnerId() == userId &&
                !Objects.equals(shoppingList.getStatus(), StatusConfig.DELETED.getStatus())) {

            shoppingList.setStatus(StatusConfig.DELETED);
            ShoppingList updatedShoppingList = shoppingListRepository.save(shoppingList);
            return modelMapper.map(updatedShoppingList, ShoppingListDTO.class);
        }
        return null;
    }

    // Getters and Setters (if required for testing or other purposes)
    public ShoppingListRepository getShoppingListRepository() {
        return shoppingListRepository;
    }

    public void setShoppingListRepository(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}

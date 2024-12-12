package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.ShoppingListDTO;
import com.grocery.app.dto.TaskDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.entities.Family;
import com.grocery.app.entities.ShoppingList;
import com.grocery.app.entities.Task;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.ShoppingListRepo;
import com.grocery.app.repositories.TaskRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.ShoppingListService;
import com.grocery.app.services.TaskService;
import lombok.Getter;
import lombok.Setter;
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

    // Getters and Setters (if required for testing or other purposes)
    @Autowired
    private ShoppingListRepo shoppingListRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public ShoppingListDTO createShoppingList(ShoppingListDTO shoppingListDTO) {
        ShoppingList shoppingList = convertToShoppingList(shoppingListDTO);
        ShoppingList savedShoppingList = shoppingListRepository.save(shoppingList);
        return convertToShoppingListDTO(savedShoppingList);
    }

    @Override
    public ShoppingListDTO getShoppingListById(long userId, long id) {

        ShoppingList shoppingList = shoppingListRepository.findById(id).orElse(null);

        if(shoppingList == null){
            throw new ServiceException(
                    ResCode.SHOPPING_LIST_NOT_FOUND.getMessage(),
                    ResCode.SHOPPING_LIST_NOT_FOUND.getCode()
            );
        }

        if(shoppingList.getOwner().getId() != userId){
            throw new ServiceException(
                    ResCode.NOT_SHOPPING_LIST_OWNER.getMessage(),
                    ResCode.NOT_SHOPPING_LIST_OWNER.getCode()
            );
        }

        return convertToShoppingListDTO(shoppingList);
    }

    @Override
    public ArrayList<ShoppingListDTO> getAllShoppingList(long userId, int from, int to) {
        List<ShoppingList> shoppingLists = shoppingListRepository.findAllByUserId(userId);

        // Clamp "to" parameter
        int maxSize = shoppingLists.size();
        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize));

        List<ShoppingList> paginatedLists = shoppingLists.subList(from, to);
        return paginatedLists.stream()
                .map(this::convertToShoppingListDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ShoppingListDTO updateShoppingList(ShoppingListDTO shoppingListDTO) {
        ShoppingList existingShoppingList = convertToShoppingList(shoppingListDTO);
        ShoppingList updatedShoppingList = shoppingListRepository.save(existingShoppingList);
        return convertToShoppingListDTO(updatedShoppingList);
    }

    @Override
    public ShoppingListDTO deleteShoppingList(long userId, long id) {
        ShoppingList shoppingList = shoppingListRepository.findById(id).orElse(null);

        if (shoppingList != null &&
                shoppingList.getOwner().getId() == userId &&
                !Objects.equals(shoppingList.getStatus(), StatusConfig.DELETED.getStatus())) {

            shoppingList.setStatus(StatusConfig.DELETED.getStatus());
            ShoppingList deletedShoppingList = shoppingListRepository.save(shoppingList);
            return convertToShoppingListDTO(deletedShoppingList);
        }
        return null;
    }

    public ShoppingListDTO convertToShoppingListDTO(ShoppingList shoppingList) {
        FamilyDTO familyDTO = modelMapper.map(shoppingList.getFamily(), FamilyDTO.class);
        UserDTO userDTO = modelMapper.map(shoppingList.getOwner(), UserDTO.class);

        ArrayList<TaskDTO> taskDTOS = new ArrayList<>();
        for (Task task : shoppingList.getTaskArrayList()) {
            taskDTOS.add(taskService.convertToTaskDTO(task));
        }

        return ShoppingListDTO.builder()
                .id(shoppingList.getId())
                .name(shoppingList.getName())
                .description(shoppingList.getDescription())
                .ownerDTO(userDTO)
                .familyDTO(familyDTO)
                .taskArrayList(taskDTOS)
                .createdAt(shoppingList.getCreatedAt())
                .updatedAt(shoppingList.getUpdatedAt())
                .status(shoppingList.getStatus())
                .build();
    }

    public ShoppingList convertToShoppingList(ShoppingListDTO shoppingListDTO) {
        User user = userRepo.findById(shoppingListDTO.getOwnerDTO().getId()).orElse(null);
        Family family = familyRepo.findById(shoppingListDTO.getFamilyDTO().getId()).orElse(null);
        ArrayList<Task> tasks = new ArrayList<>();

        for (TaskDTO taskDTO : shoppingListDTO.getTaskArrayList()) {
            tasks.add(taskService.convertToTask(taskDTO));
        }

        ShoppingList shoppingList = ShoppingList.builder()
                .owner(user)
                .family(family)
                .taskArrayList(tasks)
                .name(shoppingListDTO.getName())
                .description(shoppingListDTO.getDescription())
                .createdAt(shoppingListDTO.getCreatedAt())
                .updatedAt(shoppingListDTO.getUpdatedAt())
                .status(shoppingListDTO.getStatus())
                .build();

        if (shoppingListDTO.getId() != null) {
            shoppingList.setId(shoppingListDTO.getId());
        }

        return shoppingList;
    }

}

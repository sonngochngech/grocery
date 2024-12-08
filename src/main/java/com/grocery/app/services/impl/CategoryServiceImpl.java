package com.grocery.app.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.CategoryDTO;
import com.grocery.app.entities.Category;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.CategoryRepo;
import com.grocery.app.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<CategoryDTO> getAllCategory() {
        List<Category> categories=categoryRepo.findAll();
        if (categories == null || categories.isEmpty()) {
            throw new ServiceException(ResCode.NO_CATEGORY.getMessage(), ResCode.NO_CATEGORY.getCode());
        }
        return categories.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).toList();
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category storedCategory=categoryRepo.findByName(category.getName()).orElse(null);
        if(storedCategory!=null){
            throw new ServiceException(ResCode.CATEGORY_EXIST.getMessage(), ResCode.CATEGORY_EXIST.getCode());
        }
        category = categoryRepo.save(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category storedCategory=categoryRepo.findById(category.getId()).orElse(null);
        if(storedCategory==null){
            throw new ServiceException(ResCode.CATEGORY_NOT_FOUND.getMessage(), ResCode.CATEGORY_NOT_FOUND.getCode());
        }
        storedCategory.setName(category.getName());
        storedCategory = categoryRepo.save(storedCategory);

        return modelMapper.map(storedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category category = categoryRepo.findById(id).orElse(null);
        if(category==null){
            throw new ServiceException(ResCode.CATEGORY_NOT_FOUND.getMessage(), ResCode.CATEGORY_NOT_FOUND.getCode());
        }
        category.setIsDeleted(true);
        category = categoryRepo.save(category);
        return modelMapper.map(category, CategoryDTO.class);
    }
}

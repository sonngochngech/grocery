package com.grocery.app.controllers;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.CategoryDTO;
import com.grocery.app.dto.UnitDTO;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<CategoryDTO>>> getAllCategories(){
        List<CategoryDTO> categories = categoryService.getAllCategory();
        BaseResponse<List<CategoryDTO>> response = ResponseFactory.createResponse(categories, ResCode.GET_CATEGORY_SUCCESSFULLY.getMessage(), ResCode.GET_CATEGORY_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<CategoryDTO>> createCategory(@RequestBody @Valid CategoryDTO categoryDTO){
        CategoryDTO category = categoryService.createCategory(categoryDTO);
        BaseResponse<CategoryDTO> response = ResponseFactory.createResponse(category, ResCode.CREATE_CATEGORY_SUCCESSFULLY.getMessage(), ResCode.CREATE_CATEGORY_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @PutMapping("/update")
    public ResponseEntity<BaseResponse<CategoryDTO>> updateCategory(@RequestBody @Valid CategoryDTO categoryDTO){
        CategoryDTO category = categoryService.updateCategory(categoryDTO);
        BaseResponse<CategoryDTO> response = ResponseFactory.createResponse(category, ResCode.UPDATE_CATEGORY_SUCCESSFULLY.getMessage(), ResCode.UPDATE_CATEGORY_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<CategoryDTO>> deleteCategory(@RequestBody Long id){
        CategoryDTO category = categoryService.deleteCategory(id);
        BaseResponse<CategoryDTO> response = ResponseFactory.createResponse(category, ResCode.DELETE_CATEGORY_SUCCESSFULLY.getMessage(), ResCode.DELETE_CATEGORY_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}

package com.grocery.app.controllers;

import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;
import com.grocery.app.dto.request.createRequest.CreateMealRequest;
import com.grocery.app.dto.request.updateRequest.UpdateMealRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/meal")
@Slf4j
public class MealController {
    @PostMapping("/add")
    public ResponseEntity<MealDTO> createMeal(CreateMealRequest createMealRequest){
        return null;
    }
    @GetMapping("/get/{mealId}")
    public ResponseEntity<MealDTO> getMealById(@PathVariable Long mealId){
        return null;
    }
    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<MealDTO>> getAllMeal(@RequestParam int from, @RequestParam int to){
        return null;
    }

    @GetMapping("/recommend/{term}")
    public ResponseEntity<ArrayList<RecommendedMealDTO>> recommendMeal(@PathVariable String term){
        return null;
    }
    @PostMapping("/update")
    public ResponseEntity<MealDTO> updateMeal(UpdateMealRequest updateMealRequest){
        return null;
    }
    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<MealDTO> deleteMeal(@PathVariable Long mealId){
        return null;
    }
}

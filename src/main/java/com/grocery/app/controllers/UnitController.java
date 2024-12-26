package com.grocery.app.controllers;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UnitDTO;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.UnitService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
@SecurityRequirement(name = "bearerAuth")
public class UnitController {

    @Autowired
    private UnitService unitService;


    @GetMapping("/user/units")
    public ResponseEntity<BaseResponse<List<UnitDTO>>> getAllUnits(){
        List<UnitDTO> units = unitService.getAllUnit();
        BaseResponse<List<UnitDTO>> response = ResponseFactory.createResponse(units, ResCode.GET_UNITS_SUCCESSFULLY.getCode(), ResCode.GET_UNITS_SUCCESSFULLY.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/admin/units/create")
    public ResponseEntity<BaseResponse<UnitDTO>> createUnit(@RequestBody @Valid UnitDTO unitDTO){
        UnitDTO unit = unitService.createUnit(unitDTO);
        BaseResponse<UnitDTO> response = ResponseFactory.createResponse(unit, ResCode.CREATE_UNIT_SUCCESSFULLY.getCode(), ResCode.CREATE_UNIT_SUCCESSFULLY.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @PutMapping("/admin/units/update")
    public ResponseEntity<BaseResponse<UnitDTO>> updateUnit(@RequestBody @Valid UnitDTO unitDTO){
        UnitDTO unit = unitService.updateUnit(unitDTO);
        BaseResponse<UnitDTO> response = ResponseFactory.createResponse(unit, ResCode.UPDATE_UNIT_SUCCESSFULLY.getCode(), ResCode.UPDATE_UNIT_SUCCESSFULLY.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/admin/units/delete/{id}")
    public ResponseEntity<BaseResponse<UnitDTO>> deleteUnit(@PathVariable Long id){
        UnitDTO unit = unitService.deleteUnit(id);
        BaseResponse<UnitDTO> response = ResponseFactory.createResponse(unit, ResCode.DELETE_UNIT_SUCCESSFULLY.getCode(), ResCode.DELETE_UNIT_SUCCESSFULLY.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

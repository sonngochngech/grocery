package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UnitDTO;
import com.grocery.app.entities.Unit;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.UnitRepo;
import com.grocery.app.services.UnitService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepo unitRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<UnitDTO> getAllUnit() {
        List<Unit> units=unitRepo.findAll();
        if (units == null || units.isEmpty()) {
            throw new ServiceException(ResCode.NO_UNIT.getMessage(), ResCode.NO_UNIT.getCode());
        }
        return units.stream().map(unit -> modelMapper.map(unit,UnitDTO.class)).toList();
    }

    @Override
    public UnitDTO createUnit(UnitDTO unitDTO) {
        Unit unit = modelMapper.map(unitDTO, Unit.class);
        Unit storedUnit=unitRepo.findByName(unit.getName()).orElse(null);
        if(storedUnit!=null){
            throw new ServiceException(ResCode.UNIT_EXIST.getMessage(), ResCode.UNIT_EXIST.getCode());
        }
        unit = unitRepo.save(unit);
        return modelMapper.map(unit, UnitDTO.class);
    }

    @Override
    public UnitDTO updateUnit(UnitDTO unitDTO) {
        Unit unit = modelMapper.map(unitDTO, Unit.class);
        Unit storedUnit=unitRepo.findById(unit.getId()).orElse(null);
        if(storedUnit==null){
            throw new ServiceException(ResCode.UNIT_NOT_FOUND.getMessage(), ResCode.UNIT_NOT_FOUND.getCode());
        }
        storedUnit.setName(unit.getName());
        storedUnit = unitRepo.save(storedUnit);

        return modelMapper.map(storedUnit, UnitDTO.class);
    }

    @Override
    public UnitDTO deleteUnit(Long id) {
        Unit unit = unitRepo.findById(id).orElse(null);
        if(unit==null){
            throw new ServiceException(ResCode.UNIT_NOT_FOUND.getMessage(), ResCode.UNIT_NOT_FOUND.getCode());
        }
        unit.setIsDeleted(true);
        unit = unitRepo.save(unit);
        return modelMapper.map(unit, UnitDTO.class);
    }
}

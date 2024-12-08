package com.grocery.app.services;

import com.grocery.app.dto.UnitDTO;

import java.util.List;

public interface UnitService {
    List<UnitDTO> getAllUnit();
    UnitDTO createUnit(UnitDTO unitDTO);
    UnitDTO updateUnit(UnitDTO unitDTO);
    UnitDTO deleteUnit(Long id);
}

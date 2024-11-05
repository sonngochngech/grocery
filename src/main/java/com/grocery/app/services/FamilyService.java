package com.grocery.app.services;

import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;

import java.util.List;

public interface FamilyService {

    List<FamilyDTO> getFamilyByUser(Long userId);
    FamilyDTO createFamily(FamilyDTO familyDTO);
    FamilyDTO updateFamily(FamilyDTO familyDTO);
    FamilyDetailDTO getFamilyInformation(Long familyId);
    FamilyDTO deleteFamily(Long familyId);
    FamilyDetailDTO removeFamilyMember(Long familyId, Long userId);
    FamilyDetailDTO addFamilyMember(Long familyId, Long userId);
    Boolean verifyOwner(Long familyId, Long userId);
    Boolean verifyMember(Long familyId, Long userId);


}

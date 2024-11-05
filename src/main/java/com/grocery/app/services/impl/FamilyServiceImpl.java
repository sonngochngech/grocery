package com.grocery.app.services.impl;

import com.grocery.app.config.constant.Language;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import com.grocery.app.entities.Family;
import com.grocery.app.entities.FamilyMember;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ResourceException;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.FamilyService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@EnableJpaAuditing
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private UserRepo userRepo;


    @Override
    public List<FamilyDTO> getFamilyByUser(Long userId) {
        List<Family> families= familyRepo.findFamilyByUser(userId).orElse(null);
        if(families!=null){
            return families.stream().map(family -> modelMapper.map(family, FamilyDTO.class)).toList();
        }
        return null;
    }

    @Override
    public FamilyDTO createFamily(FamilyDTO familyDTO) {
        log.info("Create family:{} ",familyDTO);
        Family family = modelMapper.map(familyDTO, Family.class);
        family.setCreatedAt(null);
        family.setUpdatedAt(null);
        family = familyRepo.save(family);
        return modelMapper.map(family, FamilyDTO.class);
    }

    @Override
    public FamilyDTO updateFamily(FamilyDTO familyDTO){
        Family family = familyRepo.findById(familyDTO.getId()).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        modelMapper.map(familyDTO,family);
        family = familyRepo.save(family);
        return modelMapper.map(family, FamilyDTO.class);
    }

    @Override
    public FamilyDetailDTO getFamilyInformation(Long familyId) {
        log.info("Get family information:{}",familyId);
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        log.info("Family:{}",family);
        List<FamilyMember> familyMembers = family.getFamilyMembers();
        FamilyDTO familyDTO = modelMapper.map(family, FamilyDTO.class);
        List<UserDTO> members =familyMembers.stream().map(familyMember -> modelMapper.map(familyMember.getUser(), UserDTO.class)).toList();
        FamilyDetailDTO familyDetailDTO = FamilyDetailDTO.builder().basicInfo(familyDTO).members(members).build();
        log.info("Family detail:{}",familyDetailDTO);
        return familyDetailDTO;
    }

    @Override
    public FamilyDTO deleteFamily(Long familyId) {
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        family.setIsDeleted(Boolean.TRUE);
        family = familyRepo.save(family);
        return modelMapper.map(family, FamilyDTO.class);
    }

    @Override
    public FamilyDetailDTO removeFamilyMember(Long familyId, Long userId) {
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        family.getFamilyMembers().removeIf(familyMember -> familyMember.getUser().getId().equals(userId));
        familyRepo.save(family);
        return modelMapper.map(family, FamilyDetailDTO.class);
    }

    @Override
    public FamilyDetailDTO addFamilyMember(Long familyId, Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new ServiceException(ResCode.USER_NOT_FOUND.getMessage(), ResCode.USER_NOT_FOUND.getCode());
        }
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        FamilyMember familyMember = FamilyMember.builder().name(user.getUsername()).user(user).build();
        family.getFamilyMembers().add(familyMember);
        family = familyRepo.save(family);
        return modelMapper.map(family, FamilyDetailDTO.class);
    }

    @Override
    public Boolean verifyOwner(Long familyId, Long userId) {
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        return family.getOwner().getId().equals(userId);
    }

    @Override
    public Boolean verifyMember(Long familyId, Long userId) {
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        return family.getFamilyMembers().stream().anyMatch(familyMember -> familyMember.getUser().getId().equals(userId));
    }


}

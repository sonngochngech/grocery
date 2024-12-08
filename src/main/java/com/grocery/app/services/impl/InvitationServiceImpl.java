package com.grocery.app.services.impl;


import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.InvitationDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.entities.Family;
import com.grocery.app.entities.Invitation;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.InvitationRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.FamilyService;
import com.grocery.app.services.InvitationService;
import com.grocery.app.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.grocery.app.config.constant.AppConstants.InvitationStatus.ACCEPTED;
import static com.grocery.app.config.constant.AppConstants.InvitationStatus.REJECTED;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private InvitationRepo invitationRepo;

    @Autowired
    private FamilyService familyService;



    @Override
    public InvitationDTO createInvitation(Long familyId, Long userId) {
        Family family = familyRepo.findById(familyId).orElse(null);
        if (family == null) {
            throw new ServiceException(ResCode.FAMILY_NOT_FOUND.getMessage(), ResCode.FAMILY_NOT_FOUND.getCode());
        }
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new ServiceException(ResCode.USER_NOT_FOUND.getMessage(), ResCode.USER_NOT_FOUND.getCode());
        }
        Invitation invitation = Invitation.builder().family(family).user(user).build();
        invitation=invitationRepo.save(invitation);
        return modelMapper.map(invitation, InvitationDTO.class);

    }

    @Transactional
    @Override
    public InvitationDTO updateInvitation(Boolean isAccepted, Long invitationId, Long userId) {
        Invitation invitation = invitationRepo.findById(invitationId).orElse(null);
        if (invitation == null) {
            throw new ServiceException(ResCode.INVITATION_NOT_FOUND.getMessage(), ResCode.INVITATION_NOT_FOUND.getCode());
        }
        if (!invitation.getUser().getId().equals(userId)) {
            throw new ServiceException(ResCode.NOT_BELONG_TO_INVITATION.getMessage(), ResCode.NOT_BELONG_TO_INVITATION.getCode());
        }
        String status=invitation.getStatus();
        AppConstants.InvitationStatus invitationStatus = AppConstants.InvitationStatus.valueOf(status);

        switch (invitationStatus){
            case PENDING:
                if(isAccepted){
                    familyService.addFamilyMember(invitation.getFamily().getId(),userId);
                    invitation.setStatus(ACCEPTED.name());
                    invitationRepo.delete(invitation);
                }else{
                    invitation.setStatus(REJECTED.name());
                    invitation=invitationRepo.save(invitation);
                }
                break;
            case ACCEPTED:
                throw new ServiceException(ResCode.INVITATION_ALREADY_ACCEPTED.getMessage(), ResCode.INVITATION_ALREADY_ACCEPTED.getCode());
            case REJECTED:
                    throw new ServiceException(ResCode.INVITATION_ALREADY_REJECTED.getMessage(), ResCode.INVITATION_ALREADY_REJECTED.getCode());
            case EXPIRED:
                throw new ServiceException(ResCode.INVITATION_EXPIRED.getMessage(), ResCode.INVITATION_EXPIRED.getCode());

        }
        return modelMapper.map(invitation, InvitationDTO.class);

    }

}

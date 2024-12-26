package com.grocery.app.controllers;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.InvitationDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.exceptions.ControllerException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.FamilyService;
import com.grocery.app.services.InvitationService;
import com.grocery.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class InvitationController {

    @Autowired
    private FamilyService familyService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserService userService;

    @GetMapping("/response-invitation")
    public ResponseEntity<BaseResponse<InvitationDTO>> responseInvitation( @RequestParam Long invitationId, @RequestParam Boolean isAccepted, @RequestParam Long userId,@RequestParam Long familyId) {
        boolean isMember = familyService.verifyMember(familyId,userId);

        if(isMember){
            throw new ControllerException(ResCode.ALREADY_IN_FAMILY.getMessage(), ResCode.ALREADY_IN_FAMILY.getCode());
        }
        InvitationDTO invitationDTO = invitationService.updateInvitation(isAccepted,invitationId,userId);
        BaseResponse<InvitationDTO> response = ResponseFactory.createResponse(invitationDTO, ResCode.RESPONSE_INVITATION_SUCCESSFULLY.getMessage(), ResCode.RESPONSE_INVITATION_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

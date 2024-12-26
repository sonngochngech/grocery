package com.grocery.app.controllers;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.InvitationDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import com.grocery.app.exceptions.ControllerException;
import com.grocery.app.notification.NotificationFactory;
import com.grocery.app.notification.NotificationProducer;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ErrorResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.AuthenticationService;
import com.grocery.app.services.FamilyService;
import com.grocery.app.services.InvitationService;
import com.grocery.app.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users/families")
@SecurityRequirement(name = "bearerAuth")
public class FamilyController {

    @Autowired
    private FamilyService familyService;
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private NotificationFactory notificationFactory;



    @GetMapping("")
    public ResponseEntity<BaseResponse<List<FamilyDTO>>> getFamilies() {
        Long userId = authenticationService.getCurrentUser().getId();
        List<FamilyDTO> families = familyService.getFamilyByUser(userId);
        BaseResponse<List<FamilyDTO>> response = ResponseFactory.createResponse(families, ResCode.GET_FAMILIES_SUCCESSFULLY.getMessage(), ResCode.GET_FAMILIES_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/{familyId}")
    public ResponseEntity<BaseResponse<FamilyDetailDTO>> getFamily(@PathVariable Long familyId) {
        Long userId = authenticationService.getCurrentUser().getId();
        if(familyService.verifyMember(familyId,userId) || familyService.verifyOwner(familyId,userId)){
            FamilyDetailDTO family = familyService.getFamilyInformation(familyId);
            BaseResponse<FamilyDetailDTO> response = ResponseFactory.createResponse(family, ResCode.GET_FAMILY_SUCCESSFULLY.getMessage(), ResCode.GET_FAMILY_SUCCESSFULLY.getCode());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            throw new ControllerException(ResCode.NOT_BELONG_TO_FAMILY.getMessage(), ResCode.NOT_BELONG_TO_FAMILY.getCode());
        }

    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<FamilyDTO>> createFamily( @RequestBody @Valid FamilyDTO familyDTO) {
        UserInfoConfig user = authenticationService.getCurrentUser();
        UserDTO userDTO= new UserDTO(user.getId(),user.getUsername());
        familyDTO.setOwner(userDTO);
        FamilyDTO family = familyService.createFamily(familyDTO);
        BaseResponse<FamilyDTO> response = ResponseFactory.createResponse(family, ResCode.CREATE_FAMILY_SUCCESSFULLY.getMessage(), ResCode.CREATE_FAMILY_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{familyId}/delete")
    public ResponseEntity<BaseResponse<FamilyDTO>> deleteFamily(@PathVariable Long familyId) {
        Long userId = authenticationService.getCurrentUser().getId();
        if(familyService.verifyOwner(familyId,userId)){
            FamilyDTO family = familyService.deleteFamily(familyId);
            BaseResponse<FamilyDTO> response = ResponseFactory.createResponse(family, ResCode.DELETE_FAMILY_SUCCESSFULLY.getMessage(), ResCode.DELETE_FAMILY_SUCCESSFULLY.getCode());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            throw new ControllerException(ResCode.NOT_OWNER_OF_FAMILY.getMessage(), ResCode.NOT_OWNER_OF_FAMILY.getCode());
        }

    }

    @DeleteMapping("/{familyId}/delete-member")
    public ResponseEntity<BaseResponse<FamilyDetailDTO>> deleteFamilyMember(@PathVariable Long familyId, @RequestParam Long userId) {
        Long ownerId = authenticationService.getCurrentUser().getId();
        if(familyService.verifyOwner(familyId,ownerId)){
            FamilyDetailDTO family = familyService.removeFamilyMember(familyId,userId);
            BaseResponse<FamilyDetailDTO> response = ResponseFactory.createResponse(family, ResCode.DELETE_FAMILY_MEMBER_SUCCESSFULLY.getMessage(), ResCode.DELETE_FAMILY_MEMBER_SUCCESSFULLY.getCode());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            throw new ControllerException(ResCode.NOT_OWNER_OF_FAMILY.getMessage(), ResCode.NOT_OWNER_OF_FAMILY.getCode());
        }
    }

    @PutMapping("/{familyId}/leave")
    public ResponseEntity<BaseResponse<FamilyDetailDTO>> leaveFamily(@PathVariable Long familyId) {
        Long userId = authenticationService.getCurrentUser().getId();
        if(familyService.verifyOwner(familyId,userId)){
            throw new ControllerException(ResCode.NOT_REMOVED_OWNER.getMessage(), ResCode.NOT_REMOVED_OWNER.getCode());
        }
        if(familyService.verifyMember(familyId,userId)){
            FamilyDetailDTO family = familyService.removeFamilyMember(familyId,userId);
            BaseResponse<FamilyDetailDTO> response = ResponseFactory.createResponse(family, ResCode.DELETE_FAMILY_MEMBER_SUCCESSFULLY.getMessage(), ResCode.DELETE_FAMILY_MEMBER_SUCCESSFULLY.getCode());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            throw new ControllerException(ResCode.NOT_BELONG_TO_FAMILY.getMessage(), ResCode.NOT_BELONG_TO_FAMILY.getCode());
        }
    }

    @PutMapping("/{familyId}/members/invite")
    public ResponseEntity<BaseResponse<InvitationDTO>> invite(@PathVariable Long familyId,@RequestParam String username) {
        Long ownerId = authenticationService.getCurrentUser().getId();
        boolean isOwner = familyService.verifyOwner(familyId,ownerId);
        UserInfoConfig user = (UserInfoConfig) userDetailsService.loadUserByUsername(username);
        if(!isOwner){
            throw new ControllerException(ResCode.NOT_OWNER_OF_FAMILY.getMessage(), ResCode.NOT_OWNER_OF_FAMILY.getCode());
        }
        InvitationDTO invitationDTO = invitationService.createInvitation(familyId,user.getId());
        String content="Nếu bạn đồng ý tham gia gia đình, hãy ấn vào link  và chấp nhận lời mời: "+ "http://10.13.28.161:8081/api/response-invitation?invitationId="+invitationDTO.getId()+"&isAccepted=true&userId="+user.getId()+"&familyId="+familyId;
        notificationProducer.sendMessage(notificationFactory.sendInvitationNoti(user.getEmail(),content));
        BaseResponse<InvitationDTO> response = ResponseFactory.createResponse(invitationDTO, ResCode.INVITATION_SUCCESSFULLY.getMessage(), ResCode.INVITATION_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


//    @GetMapping("/{familyId}/members/response-invitation")
//    public ResponseEntity<BaseResponse<InvitationDTO>> responseInvitation(@PathVariable Long familyId,@RequestParam Long invitationId,@RequestParam Boolean isAccepted,@RequestParam Long userId) {
//        boolean isMember = familyService.verifyMember(familyId,userId);
//        if(isMember){
//            throw new ControllerException(ResCode.ALREADY_IN_FAMILY.getMessage(), ResCode.ALREADY_IN_FAMILY.getCode());
//        }
//        InvitationDTO invitationDTO = invitationService.updateInvitation(isAccepted,invitationId,userId);
//        BaseResponse<InvitationDTO> response = ResponseFactory.createResponse(invitationDTO, ResCode.RESPONSE_INVITATION_SUCCESSFULLY.getMessage(), ResCode.RESPONSE_INVITATION_SUCCESSFULLY.getCode());
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

}

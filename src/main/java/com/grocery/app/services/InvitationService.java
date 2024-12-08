package com.grocery.app.services;

import com.grocery.app.dto.InvitationDTO;

public interface InvitationService {

    InvitationDTO createInvitation(Long familyId, Long userId);
    InvitationDTO updateInvitation(Boolean isAccepted, Long invitationId,Long userId);



}

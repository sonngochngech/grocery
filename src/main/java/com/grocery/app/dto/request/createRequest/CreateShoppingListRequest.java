package com.grocery.app.dto.request.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShoppingListRequest {
    private Long ownerId;
    private Long familyId;
    private String name;
    private String description;

}

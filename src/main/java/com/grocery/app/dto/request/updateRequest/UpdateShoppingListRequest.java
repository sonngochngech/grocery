package com.grocery.app.dto.request.updateRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateShoppingListRequest {
    private Long shoppingListId;
    private Long ownerId;
    private Long familyId;
    private String Name;
    private String description;
}

package com.grocery.app.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFridgeDTO {

    private String foodName;
    private String email;
    private String fridgeName;
    private Long fridgeItemId;
}

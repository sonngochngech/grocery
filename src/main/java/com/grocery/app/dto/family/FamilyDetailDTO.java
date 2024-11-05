package com.grocery.app.dto.family;

import com.grocery.app.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyDetailDTO {

    private FamilyDTO basicInfo;
    private List<UserDTO> members;
}

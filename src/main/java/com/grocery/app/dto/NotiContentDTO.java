package com.grocery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotiContentDTO {
    private String title;
    private String message;
    private String type;
    private UserDTO sender;
    private Set<UserDTO> receivers=new HashSet<>();
}

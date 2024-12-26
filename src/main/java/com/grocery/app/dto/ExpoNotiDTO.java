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
public class ExpoNotiDTO {

    private List<String> token=new ArrayList<>();
    private String body;
    private String externalData;
}

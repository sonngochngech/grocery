package com.grocery.app.dto.request.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMealRequest {
    Long userId;
    String name;
    String term;

}

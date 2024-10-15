package com.assesment.Meeting_Calender_Assistant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseHere {
    private int statusCode;
    private String message;
}

package com.assesment.Meeting_Calender_Assistant.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

package com.assesment.Meeting_Calender_Assistant.DTO;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRequestDto {
    private String title;
    private List<Long> participantIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

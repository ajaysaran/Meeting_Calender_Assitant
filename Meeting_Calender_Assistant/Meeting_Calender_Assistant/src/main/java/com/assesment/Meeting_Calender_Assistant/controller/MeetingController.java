package com.assesment.Meeting_Calender_Assistant.controller;

import java.time.Duration;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assesment.Meeting_Calender_Assistant.DTO.MeetingRequestDto;
import com.assesment.Meeting_Calender_Assistant.DTO.TimeSlot;
import com.assesment.Meeting_Calender_Assistant.model.Employee;
import com.assesment.Meeting_Calender_Assistant.model.Meeting;
import com.assesment.Meeting_Calender_Assistant.service.MeetingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping("/book/{organizerId}")
    public ResponseEntity<Meeting> bookMeeting(@PathVariable Long organizerId, @RequestBody MeetingRequestDto requestDto) {
        Meeting meeting = meetingService.bookMeeting(organizerId, requestDto);
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/free-slots")
    public ResponseEntity<List<TimeSlot>> getFreeSlots(@RequestParam Long emp1Id, @RequestParam Long emp2Id, @RequestParam Duration duration) {
        List<TimeSlot> freeSlots = meetingService.findFreeSlots(emp1Id, emp2Id, duration);
        return ResponseEntity.ok(freeSlots);
    }

    @PostMapping("/conflicts")
    public ResponseEntity<List<Employee>> findConflicts(@RequestBody MeetingRequestDto requestDto) {
        List<Employee> conflicts = meetingService.findConflictingParticipants(requestDto);
        return ResponseEntity.ok(conflicts);
    }
}


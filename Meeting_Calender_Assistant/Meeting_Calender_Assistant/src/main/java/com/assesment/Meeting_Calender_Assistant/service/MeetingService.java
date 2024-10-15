package com.assesment.Meeting_Calender_Assistant.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.assesment.Meeting_Calender_Assistant.DTO.MeetingRequestDto;
import com.assesment.Meeting_Calender_Assistant.DTO.TimeSlot;
import com.assesment.Meeting_Calender_Assistant.exception.ConflictException;
import com.assesment.Meeting_Calender_Assistant.exception.ResourceNotFoundException;
import com.assesment.Meeting_Calender_Assistant.model.Employee;
import com.assesment.Meeting_Calender_Assistant.model.Meeting;
import com.assesment.Meeting_Calender_Assistant.repository.EmployeeRepository;
import com.assesment.Meeting_Calender_Assistant.repository.MeetingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final EmployeeRepository employeeRepository;

    // 1. Book a meeting
    public Meeting bookMeeting(Long organizerId, MeetingRequestDto requestDto) {
        Employee organizer = employeeRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Meeting meeting = new Meeting();
        meeting.setTitle(requestDto.getTitle());
        meeting.setOrganizer(organizer);
        meeting.setStartTime(requestDto.getStartTime());
        meeting.setEndTime(requestDto.getEndTime());

        List<Employee> participants = employeeRepository.findAllById(requestDto.getParticipantIds());
        meeting.setParticipants(participants);

        // Check for conflicts
        checkForConflicts(meeting, participants);

        return meetingRepository.save(meeting);
    }

    // 2. Find free slots
    public List<TimeSlot> findFreeSlots(Long emp1Id, Long emp2Id, Duration duration) {
        // Retrieve meetings for both employees
        Employee emp1 = employeeRepository.findById(emp1Id).orElseThrow();
        Employee emp2 = employeeRepository.findById(emp2Id).orElseThrow();

        List<Meeting> emp1Meetings = meetingRepository.findAllByOrganizer(emp1);
        List<Meeting> emp2Meetings = meetingRepository.findAllByOrganizer(emp2);

        // Algorithm to find common free slots with the specified duration
        return calculateFreeSlots(emp1Meetings, emp2Meetings, duration);
    }

    // 3. Find meeting conflicts
    public List<Employee> findConflictingParticipants(MeetingRequestDto requestDto) {
        List<Employee> conflictingEmployees = new ArrayList<>();
        List<Employee> participants = employeeRepository.findAllById(requestDto.getParticipantIds());

        for (Employee participant : participants) {
            List<Meeting> meetings = meetingRepository.findAllByParticipant(participant.getId());

            // Check for conflicts with the requested time slot
            for (Meeting meeting : meetings) {
                if (hasConflict(meeting, requestDto.getStartTime(), requestDto.getEndTime())) {
                    conflictingEmployees.add(participant);
                    break;
                }
            }
        }
        return conflictingEmployees;
    }

    private boolean hasConflict(Meeting meeting, LocalDateTime startTime, LocalDateTime endTime) {
        return (startTime.isBefore(meeting.getEndTime()) && endTime.isAfter(meeting.getStartTime()));
    }

    private void checkForConflicts(Meeting meeting, List<Employee> participants) {
        for (Employee participant : participants) {
            List<Meeting> existingMeetings = meetingRepository.findAllByParticipant(participant.getId());
            for (Meeting existingMeeting : existingMeetings) {
                if (hasConflict(existingMeeting, meeting.getStartTime(), meeting.getEndTime())) {
                    throw new ConflictException("Participant " + participant.getName() + " has a conflict.");
                }
            }
        }
    }
    
    private List<TimeSlot> calculateFreeSlots(List<Meeting> emp1Meetings, List<Meeting> emp2Meetings, Duration duration) {
        // Step 1: Merge both employees' meetings into a single sorted list based on start time
        List<Meeting> allMeetings = new ArrayList<>();
        allMeetings.addAll(emp1Meetings);
        allMeetings.addAll(emp2Meetings);
        
        // Sort meetings by their start time
        Collections.sort(allMeetings, Comparator.comparing(Meeting::getStartTime));

        // Step 2: Define the working hours (e.g., 9 AM to 6 PM)
        LocalDateTime startOfDay = LocalDateTime.now().withHour(9).withMinute(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(18).withMinute(0);

        // Step 3: Find free slots between meetings
        List<TimeSlot> freeSlots = new ArrayList<>();
        LocalDateTime lastEndTime = startOfDay;

        for (Meeting meeting : allMeetings) {
            // If there is a gap between the last meeting end and the next meeting start, it's a free slot
            if (lastEndTime.isBefore(meeting.getStartTime())) {
                // Check if the gap is at least the required duration
                Duration gap = Duration.between(lastEndTime, meeting.getStartTime());
                if (!gap.minus(duration).isNegative()) {
                    freeSlots.add(new TimeSlot(lastEndTime, lastEndTime.plus(duration)));
                }
            }
            // Move the end time to the max end time of the meetings
            lastEndTime = meeting.getEndTime().isAfter(lastEndTime) ? meeting.getEndTime() : lastEndTime;
        }

        // Step 4: Check for any free time slot after the last meeting and before the end of the day
        if (lastEndTime.isBefore(endOfDay)) {
            Duration remainingTime = Duration.between(lastEndTime, endOfDay);
            if (!remainingTime.minus(duration).isNegative()) {
                freeSlots.add(new TimeSlot(lastEndTime, lastEndTime.plus(duration)));
            }
        }

        return freeSlots;
    }
}


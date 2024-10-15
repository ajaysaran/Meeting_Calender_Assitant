package com.assesment.Meeting_Calender_Assistant.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.assesment.Meeting_Calender_Assistant.DTO.MeetingRequestDto;
import com.assesment.Meeting_Calender_Assistant.DTO.TimeSlot;
import com.assesment.Meeting_Calender_Assistant.exception.ConflictException;
import com.assesment.Meeting_Calender_Assistant.exception.ResourceNotFoundException;
import com.assesment.Meeting_Calender_Assistant.model.Employee;
import com.assesment.Meeting_Calender_Assistant.model.Meeting;
import com.assesment.Meeting_Calender_Assistant.repository.EmployeeRepository;
import com.assesment.Meeting_Calender_Assistant.repository.MeetingRepository;
import com.assesment.Meeting_Calender_Assistant.service.MeetingService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private MeetingService meetingService;

    private Employee emp1;
    private Employee emp2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize Employee objects
        emp1 = new Employee(1L, "Employee 1", new ArrayList<>(), new ArrayList<>());
        emp2 = new Employee(2L, "Employee 2", new ArrayList<>(), new ArrayList<>());
    }

    @Test
    public void testBookMeeting_successful() {
        // Arrange
        MeetingRequestDto meetingRequest = new MeetingRequestDto("Team Meeting", Arrays.asList(2L), 
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp1));
        when(employeeRepository.findAllById(meetingRequest.getParticipantIds())).thenReturn(Arrays.asList(emp2));

        // Act
        Meeting bookedMeeting = meetingService.bookMeeting(1L, meetingRequest);

        // Assert
        assertNotNull(bookedMeeting);
        assertEquals("Team Meeting", bookedMeeting.getTitle());
        assertEquals(emp1, bookedMeeting.getOrganizer());
        assertEquals(1, bookedMeeting.getParticipants().size());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
    }

    @Test
    public void testBookMeeting_employeeNotFound() {
        // Arrange
        MeetingRequestDto meetingRequest = new MeetingRequestDto("Team Meeting", Arrays.asList(2L), 
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            meetingService.bookMeeting(1L, meetingRequest);
        });
    }

    @Test
    public void testBookMeeting_conflictException() {
        // Arrange
        MeetingRequestDto meetingRequest = new MeetingRequestDto("Team Meeting", Arrays.asList(2L),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp1));
        when(employeeRepository.findAllById(meetingRequest.getParticipantIds())).thenReturn(Arrays.asList(emp2));

        Meeting existingMeeting = new Meeting(1L, "Existing Meeting", emp2, Arrays.asList(emp2),
                LocalDateTime.now().plusMinutes(90), LocalDateTime.now().plusMinutes(150));

        when(meetingRepository.findAllByParticipant(2L)).thenReturn(Arrays.asList(existingMeeting));

        // Act & Assert
        assertThrows(ConflictException.class, () -> {
            meetingService.bookMeeting(1L, meetingRequest);
        });
    }

    @Test
    public void testFindFreeSlots_successful() {
        // Arrange
        List<Meeting> emp1Meetings = Arrays.asList(
                new Meeting(1L, "Meeting 1", emp1, new ArrayList<>(),
                        LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2))
        );
        List<Meeting> emp2Meetings = Arrays.asList(
                new Meeting(2L, "Meeting 2", emp2, new ArrayList<>(),
                        LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4))
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp1));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(emp2));
        when(meetingRepository.findAllByOrganizer(emp1)).thenReturn(emp1Meetings);
        when(meetingRepository.findAllByOrganizer(emp2)).thenReturn(emp2Meetings);

        // Act
        List<TimeSlot> freeSlots = meetingService.findFreeSlots(1L, 2L, Duration.ofMinutes(30));

        // Assert
        assertNotNull(freeSlots);
        assertFalse(freeSlots.isEmpty());
        verify(meetingRepository, times(1)).findAllByOrganizer(emp1);
        verify(meetingRepository, times(1)).findAllByOrganizer(emp2);
    }

    @Test
    public void testFindConflictingParticipants_successful() {
        // Arrange
        MeetingRequestDto meetingRequest = new MeetingRequestDto("Team Meeting", Arrays.asList(2L),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(employeeRepository.findAllById(meetingRequest.getParticipantIds())).thenReturn(Arrays.asList(emp2));

        Meeting existingMeeting = new Meeting(1L, "Existing Meeting", emp2, Arrays.asList(emp2),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(meetingRepository.findAllByParticipant(2L)).thenReturn(Arrays.asList(existingMeeting));

        // Act
        List<Employee> conflictingParticipants = meetingService.findConflictingParticipants(meetingRequest);

        // Assert
        assertNotNull(conflictingParticipants);
        assertEquals(1, conflictingParticipants.size());
        assertEquals(emp2, conflictingParticipants.get(0));
    }
}

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

}

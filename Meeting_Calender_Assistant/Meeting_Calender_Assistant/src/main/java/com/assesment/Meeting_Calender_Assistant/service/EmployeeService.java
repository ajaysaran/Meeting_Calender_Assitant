package com.assesment.Meeting_Calender_Assistant.service;

import org.springframework.stereotype.Service;

import com.assesment.Meeting_Calender_Assistant.model.Employee;
import com.assesment.Meeting_Calender_Assistant.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    // Add a new employee
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}


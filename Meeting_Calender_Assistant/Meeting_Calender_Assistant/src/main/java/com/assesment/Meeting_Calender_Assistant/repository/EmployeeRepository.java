package com.assesment.Meeting_Calender_Assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assesment.Meeting_Calender_Assistant.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}

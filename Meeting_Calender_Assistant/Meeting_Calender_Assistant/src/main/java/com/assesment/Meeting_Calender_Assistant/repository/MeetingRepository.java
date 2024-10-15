package com.assesment.Meeting_Calender_Assistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.assesment.Meeting_Calender_Assistant.model.Employee;
import com.assesment.Meeting_Calender_Assistant.model.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

	List<Meeting> findAllByOrganizer(Employee employee);

    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :employeeId")
    List<Meeting> findAllByParticipant(@Param("employeeId") Long employeeId);
    
}

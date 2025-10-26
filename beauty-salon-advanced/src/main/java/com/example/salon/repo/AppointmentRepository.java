package com.example.salon.repo;

import com.example.salon.entity.Appointment;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  @Query("SELECT COUNT(a) FROM Appointment a WHERE a.startAt BETWEEN :start AND :end")
  long countByStartAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
  
  long countByCustomerEmailOrCustomerPhone(String email, String phone);
}

package com.example.salon.repo;

import com.example.salon.entity.Appointment;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  long countByStartAtBetween(LocalDateTime start, LocalDateTime end);
  long countByCustomerEmailOrCustomerPhone(String email, String phone);
}

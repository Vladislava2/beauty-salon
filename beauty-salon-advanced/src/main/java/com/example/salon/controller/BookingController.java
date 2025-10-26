package com.example.salon.controller;

import com.example.salon.entity.Appointment;
import com.example.salon.repo.ManicureServiceRepository;
import com.example.salon.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingController {
  private final BookingService bookingService;
  private final ManicureServiceRepository serviceRepo;

  public BookingController(BookingService bookingService, ManicureServiceRepository serviceRepo) {
    this.bookingService = bookingService;
    this.serviceRepo = serviceRepo;
  }

  @PostMapping("/booking")
  public String submit(@Valid @ModelAttribute("appointment") Appointment appointment,
                       BindingResult binding, Model model) {
    model.addAttribute("services", serviceRepo.findAll());
    if (binding.hasErrors()) {
      return "booking";
    }
    try {
      bookingService.createWithBusinessLogic(appointment);
      model.addAttribute("ok", true);
    } catch (IllegalStateException ex) {
      model.addAttribute("error", ex.getMessage());
    }
    return "booking";
  }

  // Прост календар/availability API: връща свободни 30-мин интервали за деня (9:00-18:00) при default услуга 60м
  @GetMapping("/api/availability")
  @ResponseBody
  public ResponseEntity<?> availability(@RequestParam String date,
                                        @RequestParam(defaultValue = "60") int durationMinutes) {
    try {
      System.out.println("=== Availability API called ===");
      System.out.println("Date: " + date + ", Duration: " + durationMinutes);
      
      LocalDate d = LocalDate.parse(date);
      LocalDateTime start = d.atTime(LocalTime.of(9,0));
      LocalDateTime end = d.atTime(LocalTime.of(18,0));
      
      System.out.println("Start: " + start + ", End: " + end);
      
      List<String> free = new ArrayList<>();
      for (LocalDateTime t = start; !t.isAfter(end.minusMinutes(durationMinutes)); t = t.plusMinutes(30)) {
        boolean isFree = bookingService.isFree(t, durationMinutes);
        if (isFree) {
          free.add(t.toString());
        }
      }
      
      System.out.println("Found " + free.size() + " free slots");
      return ResponseEntity.ok(free);
    } catch (Exception e) {
      System.err.println("=== ERROR in availability API ===");
      e.printStackTrace();
      return ResponseEntity.internalServerError()
              .body("Error: " + e.getMessage());
    }
  }
}

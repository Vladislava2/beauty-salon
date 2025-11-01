package com.example.salon.controller;

import com.example.salon.entity.Appointment;
import com.example.salon.repo.ManicureServiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
  private final ManicureServiceRepository serviceRepo;

  public PageController(ManicureServiceRepository serviceRepo) {
    this.serviceRepo = serviceRepo;
  }

  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("services", serviceRepo.findAll());
    return "index";
  }

  @GetMapping("/services")
  public String services(Model model) {
    model.addAttribute("services", serviceRepo.findAll());
    return "services";
  }

  @GetMapping("/pricing")
  public String pricing(Model model) {
    model.addAttribute("services", serviceRepo.findAll());
    return "pricing";
  }

  @GetMapping("/gallery")
  public String gallery() { return "gallery"; }

  @GetMapping("/salon")
  public String salon() { return "salon"; }

  @GetMapping("/booking")
  public String bookingForm(Model model) {
    model.addAttribute("services", serviceRepo.findAll());
    model.addAttribute("appointment", new Appointment());
    return "booking";
  }
}

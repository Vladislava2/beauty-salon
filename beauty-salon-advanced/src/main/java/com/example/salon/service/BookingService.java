package com.example.salon.service;

import com.example.salon.entity.Appointment;
import com.example.salon.entity.ManicureService;
import com.example.salon.repo.AppointmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {
  private final AppointmentRepository appointmentRepository;
  private final JavaMailSender mailSender;

  public BookingService(AppointmentRepository appointmentRepository, JavaMailSender mailSender) {
    this.appointmentRepository = appointmentRepository;
    this.mailSender = mailSender;
  }


  private boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  public boolean isFree(LocalDateTime start, int durationMinutes) {
    try {
      LocalDateTime end = start.plusMinutes(durationMinutes);
      long overlaps = appointmentRepository.countByStartAtBetween(
              start.minusMinutes(durationMinutes - 1), end
      );
      return overlaps == 0;
    } catch (Exception e) {
      System.err.println("Error checking availability: " + e.getMessage());
      e.printStackTrace();
      return true;
    }
  }

  private String generateReferralCode(String id) {
    if (id == null || id.trim().isEmpty()) {
      int random = (int)(Math.random() * 1_000_000);
      return "REF" + String.format("%06d", random);
    }
    int h = Math.abs(id.hashCode());
    return "REF" + String.format("%06d", h % 1_000_000);
  }

  @Transactional
  public Appointment createWithBusinessLogic(Appointment a) {
    ManicureService s = a.getService();
    if (s == null) throw new IllegalArgumentException("Missing service");
    if (!isFree(a.getStartAt(), s.getDurationMinutes())) {
      throw new IllegalStateException("Слотът е зает, изберете друг час.");
    }

    // Count previous visits by email or phone
    String email = isBlank(a.getCustomerEmail()) ? "" : a.getCustomerEmail();
    String phone = isBlank(a.getCustomerPhone()) ? "" : a.getCustomerPhone();
    
    long past = appointmentRepository.countByCustomerEmailOrCustomerPhone(email, phone);
    
    System.out.println("\n===== 6TH VISIT DISCOUNT CHECK =====");
    System.out.println("Email: " + email);
    System.out.println("Phone: " + phone);
    System.out.println("Previous visits: " + past);
    System.out.println("This will be visit #" + (past + 1));
    
    // 50% discount on 6th visit (after 5 completed visits)
    boolean hasDiscount = (past + 1 == 6);
    a.setDiscountPercent(hasDiscount ? 50 : 0);
    
    System.out.println("Discount applied: " + (hasDiscount ? "YES (50%)" : "NO"));
    System.out.println("===================================\n");

    a.setPriceAtBooking(s.getPrice());

    if (a.getReferralCode() == null || isBlank(a.getReferralCode())) {
      String idForCode = !isBlank(a.getCustomerEmail())
              ? a.getCustomerEmail()
              : a.getCustomerPhone();
      String generatedCode = generateReferralCode(idForCode);
      a.setReferralCode(generatedCode);
      System.out.println("Generated referral code: " + generatedCode + " from: " + idForCode);
    } else {
      System.out.println("Using existing referral code: " + a.getReferralCode());
    }

    Appointment saved = appointmentRepository.save(a);

    // Email уведомление (ако има имейл)
    if (!isBlank(a.getCustomerEmail())) {
      try {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("v123jordanova2002@gmail.com");
        msg.setTo(a.getCustomerEmail());
        msg.setSubject("Потвърждение на час - Nail District");
        
        System.out.println("Изпращане на емейл до: " + a.getCustomerEmail());

        // Форматиране на датата и часа
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
        String date = a.getStartAt().format(dateFormatter);
        String time = a.getStartAt().format(timeFormatter);

        String body = String.format(
                "Здравей, прекрасна! 💅\n\n" +
                        "Ти запази час на %s от %s.\n\n" +
                        "Услуга: %s\n" +
                        "Цена: %s лв\n" +
                        (a.getDiscountPercent() > 0 ? "Отстъпка: %s%%\n\n" : "\n") +
                        "Реферален код: %s\n" +
                        "Споделете" + " " + "го" + " " + "с" + " " + "приятелки" + " " + "за" + " " + "бонус! 🎁\n\n" +
                        "Чакаме те с нетърпение!\n" +
                        "Nail District 💅💖",
                date,
                time,
                s.getName(),
                s.getPrice(),
                a.getDiscountPercent(),
                a.getReferralCode()
        );

        msg.setText(body);
        mailSender.send(msg);
      } catch (Exception e) {
        System.err.println("Грешка при изпращане на имейл: " + e.getMessage());
      }
    }

    return saved;
  }
}

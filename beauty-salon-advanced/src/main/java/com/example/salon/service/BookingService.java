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

  // Java 8 helper вместо String.isBlank()
  private boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  public boolean isFree(LocalDateTime start, int durationMinutes) {
    try {
      LocalDateTime end = start.plusMinutes(durationMinutes);
      // Check if any appointment overlaps with this time slot
      long overlaps = appointmentRepository.countByStartAtBetween(
              start.minusMinutes(durationMinutes - 1), end
      );
      return overlaps == 0;
    } catch (Exception e) {
      System.err.println("Error checking availability: " + e.getMessage());
      e.printStackTrace();
      return true; // If error, assume slot is free
    }
  }

  private String generateReferralCode(String id) {
    if (id == null) return null;
    int h = Math.abs(id.hashCode());
    return "REF" + (h % 1_000_000);
  }

  @Transactional
  public Appointment createWithBusinessLogic(Appointment a) {
    ManicureService s = a.getService();
    if (s == null) throw new IllegalArgumentException("Missing service");
    if (!isFree(a.getStartAt(), s.getDurationMinutes())) {
      throw new IllegalStateException("Слотът е зает, изберете друг час.");
    }

    long past = appointmentRepository.countByCustomerEmailOrCustomerPhone(
            a.getCustomerEmail(), a.getCustomerPhone()
    );
    // 50% discount on 6th visit (after 5 completed visits)
    boolean hasDiscount = (past + 1 == 6);
    a.setDiscountPercent(hasDiscount ? 50 : 0);

    a.setPriceAtBooking(s.getPrice());

    if (a.getReferralCode() == null || isBlank(a.getReferralCode())) {
      String idForCode = !isBlank(a.getCustomerEmail())
              ? a.getCustomerEmail()
              : a.getCustomerPhone();
      a.setReferralCode(generateReferralCode(idForCode));
    }

    Appointment saved = appointmentRepository.save(a);

    // Email уведомление (ако има имейл)
    if (!isBlank(a.getCustomerEmail())) {
      try {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(a.getCustomerEmail());
        msg.setSubject("Потвърждение на час");

        String body = String.format(
                "Здравейте, %s!\n\n" +
                        "Вашият час е записан за %s.\n" +
                        "Услуга: %s\n" +
                        "Цена: %s лв\n" +
                        "Отстъпка: %s%%\n" +
                        "Реферален код (споделете на приятел за бонус): %s\n\n" +
                        "Благодарим!",
                a.getCustomerName(),
                a.getStartAt(),
                s.getName(),
                s.getPrice(),
                a.getDiscountPercent(),
                a.getReferralCode()
        );

        msg.setText(body);
        mailSender.send(msg);
      } catch (Exception ignored) {
        // В демо режим без SMTP може да хвърли изключение - игнорираме
      }
    }

    return saved;
  }
}

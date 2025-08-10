package com.example.salon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Appointment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private ManicureService service;

  @NotBlank private String customerName;
  @Email private String customerEmail;
  @Pattern(regexp = "^[+0-9 ]{6,20}$", message = "Невалиден телефон")
  private String customerPhone;

  @Future private LocalDateTime startAt;
  private String note;

  // нови полета за дипломната
  private BigDecimal priceAtBooking;
  private int discountPercent; // 0 или 20
  private String referralCode; // твоят код
  private String referredBy;   // кой те е препоръчал

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public ManicureService getService() { return service; }
  public void setService(ManicureService service) { this.service = service; }
  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }
  public String getCustomerEmail() { return customerEmail; }
  public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
  public String getCustomerPhone() { return customerPhone; }
  public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
  public LocalDateTime getStartAt() { return startAt; }
  public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
  public BigDecimal getPriceAtBooking() { return priceAtBooking; }
  public void setPriceAtBooking(BigDecimal priceAtBooking) { this.priceAtBooking = priceAtBooking; }
  public int getDiscountPercent() { return discountPercent; }
  public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
  public String getReferralCode() { return referralCode; }
  public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
  public String getReferredBy() { return referredBy; }
  public void setReferredBy(String referredBy) { this.referredBy = referredBy; }
}

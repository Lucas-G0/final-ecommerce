package com.unicesumar.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.unicesumar.paymentMethods.PaymentType;

public class Sale extends Entity {
    private UUID userId;
    private PaymentType paymentType;
    private LocalDateTime saleDate = LocalDateTime.now();

    public Sale(UUID uuid, UUID user, String paymentType, LocalDateTime saleDate) {
        super(uuid);
        this.userId = user;
        this.paymentType = PaymentType.valueOf(paymentType.toUpperCase());
        this.saleDate = saleDate;
    }

    public Sale(UUID user, PaymentType paymentType) {
        this.userId = user;
        this.paymentType = paymentType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID user) {
        this.userId = user;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String toString(){
        return "\nVenda: " + getUuid() + "\n" +
                "Id do usuário: " + userId + "\n" +
                "Forma de pagamento: " + paymentType + "\n" +
                "Data da venda: " + saleDate + "\n";
    }
}

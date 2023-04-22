package br.com.alurafood.pedidos.pedidos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private BigDecimal value;
    private String name;
    private String number;
    private String expiration;
    private String code;
    private StatusPayment status;
    private Long orderId;
    private Long formOfPaymentId;

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "id=" + id +
                ", value=" + value +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", expiration='" + expiration + '\'' +
                ", code='" + code + '\'' +
                ", status=" + status +
                ", orderId=" + orderId +
                ", formOfPaymentId=" + formOfPaymentId +
                '}';
    }
}
package br.com.alurafood.pedidos.pedidos.amqp;

import br.com.alurafood.pedidos.pedidos.dto.PaymentDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    @RabbitListener(queues = "pagamentos.detalhes-pedido")
    public void getMessageFromPayment(PaymentDTO paymentDTO) {

        System.out.println("Rebeci a mensagem" + paymentDTO.toString());
    }
}

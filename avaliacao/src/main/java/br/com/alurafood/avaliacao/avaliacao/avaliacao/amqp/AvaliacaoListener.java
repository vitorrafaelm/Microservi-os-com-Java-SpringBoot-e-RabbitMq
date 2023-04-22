package br.com.alurafood.avaliacao.avaliacao.avaliacao.amqp;

import br.com.alurafood.avaliacao.avaliacao.avaliacao.dto.PagamentoDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoListener {
    @RabbitListener(queues = "pagamentos.detalhes-avaliacao")
    public void recebeMensagem(PagamentoDto pagamento) {

        System.out.println(pagamento.getId());
        System.out.println(pagamento.getNumero());

        if(pagamento.getNumero().equals("0001")) {
            throw new RuntimeException("Não consegui processar");
        }

        System.out.println(pagamento.toString());
    }
}

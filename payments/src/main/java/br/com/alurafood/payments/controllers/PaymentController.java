package br.com.alurafood.payments.controllers;

import br.com.alurafood.payments.models.Payment;
import br.com.alurafood.payments.models.dtos.PaymentDTO;
import br.com.alurafood.payments.services.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public Page<PaymentDTO> list(@PageableDefault(size=10) Pageable pagination) {
        return paymentService.listAll(pagination);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getById(@PathVariable @NotNull Long id) {
        PaymentDTO paymentDTO = paymentService.findById(id);

        return ResponseEntity.ok(paymentDTO);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody @Valid PaymentDTO paymentDTO, UriComponentsBuilder uriBuilder) {
        paymentDTO = paymentService.createPayment(paymentDTO);
        URI address = uriBuilder.path("payments/{id}").buildAndExpand(paymentDTO.getId()).toUri();

        rabbitTemplate.convertAndSend("pagamentos.ex", "",paymentDTO);
        return ResponseEntity.created(address).body(paymentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@RequestBody @Valid PaymentDTO paymentDTO, @PathVariable @NotNull Long id) {
        paymentDTO = paymentService.updatePayment(id, paymentDTO);
        return ResponseEntity.ok(paymentDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaymentDTO> deletePayment(@PathVariable @NotNull Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "authorizedPaymentWithPendentInformation")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        paymentService.confirmarPagamento(id);
    }

    public void authorizedPaymentWithPendentInformation(Long id, Exception e){
        paymentService.changeStatus(id);
    }
}

package br.com.alurafood.payments.services;

import br.com.alurafood.payments.http.OrderClient;
import br.com.alurafood.payments.models.dtos.PaymentDTO;
import br.com.alurafood.payments.models.Payment;
import br.com.alurafood.payments.models.enums.Status;
import br.com.alurafood.payments.repository.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderClient orderClient;

    public Page<PaymentDTO> listAll(Pageable pagination) {
        return paymentRepository
                .findAll(pagination)
                .map(p -> modelMapper.map(p, PaymentDTO.class));
    }

    public PaymentDTO findById(Long id) {
        Payment payment=  paymentRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException());

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setStatus(Status.CREATED);
        paymentRepository.save(payment);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        payment.setId(id);

        payment = paymentRepository.save(payment);

        return modelMapper.map(payment, PaymentDTO.class);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<Payment> pagamento = paymentRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMED);
        paymentRepository.save(pagamento.get());
        orderClient.updatePayment(pagamento.get().getOrderId());
    }

    public void changeStatus(Long id) {
        Optional<Payment> pagamento = paymentRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMED_NO_INTEGRATION);
        paymentRepository.save(pagamento.get());

    }
}

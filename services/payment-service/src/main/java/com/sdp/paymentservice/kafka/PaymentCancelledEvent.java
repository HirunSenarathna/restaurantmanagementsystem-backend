package com.sdp.paymentservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelledEvent {

    private Long paymentId;
    private Long orderId;
    private Long customerId;
    private Long processedBy;
    private LocalDateTime timestamp;

//    private Long paymentId;
//    private Long orderId;
//    private Long customerId;
//    private LocalDateTime timestamp;
}

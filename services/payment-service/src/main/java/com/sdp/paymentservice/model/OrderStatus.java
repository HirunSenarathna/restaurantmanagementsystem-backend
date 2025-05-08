package com.sdp.paymentservice.model;

public enum OrderStatus {
    PLACED,         // Order created but payment not confirmed
    CONFIRMED,      // Payment confirmed
    PREPARING,      // Kitchen is preparing
    READY,          // Ready for pickup/delivery
    DELIVERED,      // Completed
    CANCELLED       // Cancelled

}

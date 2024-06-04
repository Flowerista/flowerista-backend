package ua.flowerista.shop.models.order;

public enum OrderStatus {
    PLACED,     // Order has been placed
    PENDING,    // Order is started payment and waiting
    IN_PROCESS, // Order paid and in process of forming
    SHIPPED,    // Order has been shipped
    COMPLETED,  // Order has been delivered and completed
    CANCELLED   // Order has been cancelled
}

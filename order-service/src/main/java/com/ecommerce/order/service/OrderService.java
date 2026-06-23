package com.ecommerce.order.service;

import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.order.events.OrderCreatedEvent;
import com.ecommerce.order.events.OrderItemEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository,
                        InventoryClient inventoryClient,
                        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = request.getItems().stream()
                .map(item -> {
                    boolean inStock = inventoryClient.isInStock(item.getProductSku(), item.getQuantity());
                    if (!inStock) {
                        throw new RuntimeException("Product " + item.getProductSku() + " is out of stock");
                    }
                    return new OrderItem(item.getProductSku(), item.getQuantity(), item.getUnitPrice());
                })
                .collect(Collectors.toList());

        order.setItems(items);

        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        publishOrderCreatedEvent(saved);
        return toResponse(saved);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByCustomer(String email) {
        return orderRepository.findByCustomerEmail(email)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void publishOrderCreatedEvent(Order order) {
        List<OrderItemEvent> eventItems = order.getItems().stream()
                .map(item -> new OrderItemEvent(item.getProductSku(), item.getQuantity()))
                .collect(Collectors.toList());

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getOrderNumber(),
                order.getCustomerEmail(),
                eventItems
        );

        logger.info("Publishing order-created event for orderNumber={}", order.getOrderNumber());
        kafkaTemplate.send(ORDER_CREATED_TOPIC, order.getOrderNumber(), event);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductSku(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                itemResponses);
    }
}

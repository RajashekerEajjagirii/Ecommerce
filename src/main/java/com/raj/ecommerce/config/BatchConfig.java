package com.raj.ecommerce.config;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.domain.Inventory;
import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.OrderItem;
import com.raj.ecommerce.domain.mongo.MongoOrder;
import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.ProcessedOrder;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.*;
import com.raj.ecommerce.service.InventoryService;
import com.raj.ecommerce.service.OrderService;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMongoRepository mongoRepository;

    public BatchConfig(JobRepository jobRepo,PlatformTransactionManager transactionManager,
                       EntityManagerFactory entityManagerFactory){
        this.jobRepository=jobRepo;
        this.transactionManager=transactionManager;
        this.entityManagerFactory=entityManagerFactory;
    }

    @Bean
    public Job processOrdersJob(){
        return new JobBuilder("processOrdersJob",jobRepository)
                .start(processOrdersStep())
                .build();
    }

    @Bean
    public Step processOrdersStep() {
        return new StepBuilder("processOrdersStep", jobRepository)
                .<Order, ProcessedOrder>chunk(10, transactionManager)
                .reader(orderReader())
                .processor(orderProcessor())
                .writer(orderWriter( inventoryRepository, redisTemplate))
                .build();
    }

    @Bean
    public ItemReader<Order> orderReader(){
        return new JpaPagingItemReaderBuilder<Order>()
                .name("orderReader")
                .entityManagerFactory(entityManagerFactory)
                // JPQL/HQL queries entity names, not table names.
                .queryString("SELECT o FROM Order o where o.status = 'CREATED' ")
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<Order,ProcessedOrder> orderProcessor(){
        return order->new ProcessedOrder(order.getId(),"COMPLETED");
    }

    @Bean
    public ItemWriter<ProcessedOrder> orderWriter(InventoryRepository inventoryRepository,
                                                  StringRedisTemplate redisTemplate) {
        return items -> {
            for (ProcessedOrder processed : items) {
                log.info("Batch process : {}",processed.toString());
                // Update DB inventory
                Order order=orderRepository.findById(processed.getId()).orElseThrow();
                List<OrderItem> orderItems=orderItemRepo.findAllByOrderId(order.getId());
                log.info("Batch process order: {}",order.toString());
                int quantity=0;
                for (OrderItem orderItem : orderItems) {
                    log.info("Batch process orderItem: {}",orderItem.toString());
                    quantity+=orderItem.getQty();
                    inventoryService.reserveStock(orderItem.getProduct().getId(), orderItem.getQty());

                    // Keep the existing cache key strategy, but update per inventory change.
                    if (redisTemplate != null) {
                        try {
                            redisTemplate.opsForValue().set(order.getUser().toString(), String.valueOf(orderItem.getProduct().getName()));
                        } catch (Exception ex) {
                            // Redis is optional in local/dev; don't fail the whole job/app if it's down.
                            log.warn("Redis unavailable; skipping cache update (key={})", order.getUser(), ex);
                        }
                    }

                    System.out.println("Updated inventory for " + orderItem.getProduct().getName() +
                            " -> Stock: " + orderItem.getQty()+" -> order: " + order.getId());
                }

                // preparing payment request
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setName(order.getUser().getUsername());
                paymentRequest.setCurrency("INR");
                paymentRequest.setQuantity(quantity);
                paymentRequest.setAmount(order.getTotalAmount());
                paymentRequest.setReceipt("txn" + System.currentTimeMillis());
                Optional<MongoOrder> mongoOrder=mongoRepository.findByOrderId(order.getId());
                if(mongoOrder.isPresent()) {
                    log.info("coming to processPaymentshipmentjdjd: ");
                    orderService.processPaymentAndShipment(paymentRequest, order, mongoOrder.get());
                }else {
                    MongoOrder newOrder=new MongoOrder();
                    newOrder.setOrderId(order.getId());
                    newOrder.setUserId(order.getUser().getId());
                    orderService.processPaymentAndShipment(paymentRequest, order, newOrder);
                }
                log.error("order after batch try: {}",order.getStatus());
            }
        };
    }

}

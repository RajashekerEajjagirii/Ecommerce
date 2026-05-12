package com.raj.ecommerce.config;

import com.raj.ecommerce.domain.Order;
import com.raj.ecommerce.domain.OrderItem;
import com.raj.ecommerce.domain.mongo.MongoOrder;
import com.raj.ecommerce.domain.mongo.ProductInfo;
import com.raj.ecommerce.dto.PaymentRequest;
import com.raj.ecommerce.dto.ProcessedOrder;
import com.raj.ecommerce.repo.*;
import com.raj.ecommerce.service.InventoryService;
import com.raj.ecommerce.service.OrderService;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
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
                .<Order, ProcessedOrder>chunk(2)
                .transactionManager(transactionManager)
                .reader(orderReader())
                .processor(orderProcessor())
                .writer(orderWriter( inventoryRepository, redisTemplate))
                .build();
    }

    @Bean
    public ItemReader<Order> orderReader(){
        System.out.println("order reader executing:");
        return new JpaPagingItemReaderBuilder<Order>()
                .name("orderReader")
                .entityManagerFactory(entityManagerFactory)
                // JPQL/HQL queries entity names, not table names.
                .queryString("SELECT o FROM Order o where o.status = 'CREATED' ")
                .pageSize(2)
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
                log.info("Batch process : {}",processed);
                // Update DB inventory
                Order order=orderRepository.findById(processed.getId()).orElseThrow();
                List<OrderItem> orderItems=orderItemRepo.findAllByOrderId(order.getId());
                log.info("Batch process order: {}",order);
                int quantity=0;
                //for Mongo
                List<ProductInfo> productInfoList=new ArrayList<>();
                for (OrderItem orderItem : orderItems) {
                    ProductInfo productInfo=new ProductInfo();
                    productInfo.setProductId(orderItem.getId());
                    productInfo.setPrice(orderItem.getPrice());
                    productInfo.setName(orderItem.getProduct().getName());
                    productInfo.setQuantity(orderItem.getQty());
                    productInfoList.add(productInfo);
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
                    orderService.processPaymentAndShipment(paymentRequest, order, mongoOrder.get());
                }else {
                    MongoOrder newOrder=new MongoOrder();
                    newOrder.setOrderId(order.getId());
                    newOrder.setUserId(order.getUser().getId());
                    newOrder.setProducts(productInfoList);
                    orderService.processPaymentAndShipment(paymentRequest, order, newOrder);
                }
                log.error("order after batch try: {}",order.getStatus());
            }
        };
    }

    /**
     * Spring Batch 6 deprecates {@code JobLauncher} in favor of {@code JobOperator}.
     * Provide a {@link JobOperator} bean so callers can launch jobs without using deprecated APIs.
     */
    @Bean
    public JobOperator jobOperator(ApplicationContext applicationContext) throws Exception {
        JobOperatorFactoryBean factoryBean = new JobOperatorFactoryBean();
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setJobRepository(jobRepository);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}

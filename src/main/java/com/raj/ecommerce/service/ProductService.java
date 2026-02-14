package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.Product;
import com.raj.ecommerce.dto.ProductRequest;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.repo.ProductRepository;
import com.raj.ecommerce.util.DomainConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public final ProductRepository productRepo;
    public final DomainConverter domainConverter;

    public ProductService(ProductRepository productRepo, DomainConverter domainConverter) {
        this.productRepo = productRepo;
        this.domainConverter = domainConverter;
    }

    public String addProduct(@Valid ProductRequest request) {
        try{
            Product product=domainConverter.productDtoToProduct(request);
            productRepo.save(product);
            return "Product added successfully!";
        } catch (Exception ex) {
            throw new BadRequestException("Error Due to: "+ex);
        }
    }


    public Page<Product> listActive(Pageable pageable) {

        return productRepo.findByActiveTrue(pageable);
    }
}

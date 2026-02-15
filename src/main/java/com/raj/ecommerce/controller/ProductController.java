package com.raj.ecommerce.controller;

import com.raj.ecommerce.domain.Product;
import com.raj.ecommerce.dto.ExceptionResponse;
import com.raj.ecommerce.dto.ProductRequest;
import com.raj.ecommerce.dto.ProductResponse;
import com.raj.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@PreAuthorize("hasAuthority('ADMIN')")
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/products")
@Tag(name = "product-module")
public class ProductController {

    public final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Onboarding product", description = "Adding Product to the website")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content =@Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content =@Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<String> addProduct(@RequestBody @Valid ProductRequest request){

        return new ResponseEntity<>(productService.addProduct(request), HttpStatus.CREATED);
    }

    @GetMapping
    public Page<Product> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size){
        return productService.listActive(PageRequest.of(page, size));
    }

    @GetMapping("/{page}/{pageSize}/{fieldName}")
    @Operation(summary = "Get products details", description = "Fetch products with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content =@Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content =@Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public ResponseEntity<List<ProductResponse>> getAllSortedProductsUsingPagination(@PathVariable int page,
                                                                                     @PathVariable int pageSize,
                                                                                     @PathVariable String fieldName){
        List<ProductResponse> products=productService.getProductsInSortingUsingPagination(page,pageSize,fieldName);
        return new ResponseEntity<>(products,HttpStatus.FOUND);
    }
}

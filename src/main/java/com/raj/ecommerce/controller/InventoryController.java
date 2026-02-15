package com.raj.ecommerce.controller;

import com.raj.ecommerce.dto.InventoryRequest;
import com.raj.ecommerce.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<String> addingStock(@RequestBody @Valid InventoryRequest request){
        return new ResponseEntity<>(inventoryService.onBoardingStock(request), HttpStatus.CREATED);
    }
}

package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.Inventory;
import com.raj.ecommerce.dto.InventoryRequest;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.InventoryRepository;
import com.raj.ecommerce.util.DomainConverter;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private DomainConverter domainConverter;
    @Autowired
    private InventoryRepository inventoryRepo;

    public String onBoardingStock(@Valid InventoryRequest request) {
        try {
            Inventory inventoryInfo=domainConverter.inventoryDtoToInventory(request);
            inventoryRepo.save(inventoryInfo);
            return "Stock updated successfully to Warehouse!";
        } catch (Exception ex) {
            throw new BadRequestException("Error due to the: "+ex);
        }
    }

    @Transactional
    public void reserveStock(Long productId, Integer qty) {
        Inventory inv=inventoryRepo.findByProductId(productId)
                .orElseThrow(()->new RecordNotFoundException("No inventory found"));
        if(inv.getQuantity()<qty)
            throw new BadRequestException("Insufficient stock!");
        inv.setQuantity(inv.getQuantity()-qty);
        inventoryRepo.save(inv);
    }
}

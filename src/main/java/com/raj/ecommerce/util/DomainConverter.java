package com.raj.ecommerce.util;

import com.raj.ecommerce.constants.Constants;
import com.raj.ecommerce.domain.primary.*;
import com.raj.ecommerce.dto.*;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.primary.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class DomainConverter {

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private InventoryRepository inventoryRepository;

    public  User registerDtoToUser(RegisterRequest request,String password){

        Set<Role> roles=new HashSet<>();
        Role role= roleRepo.findByName(Constants.ROLE_TYPE)
                .orElseThrow(()-> new IllegalArgumentException("Role not found"));
        roles.add(role);

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(password)
                .mobileNumber(request.getMobileNumber())
                .roles(roles)
                .createdAt(new Date().toInstant())
                .build();
    }

    public Address addressDtoToAddress(AddressRequest address, Long userId) {
          User userInfo=userRepository.findById(userId).orElseThrow(()-> new RecordNotFoundException("User not found !"));
          return  Address.builder().user(userInfo)
                .line1(address.getLine1())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry()).isActive(false)
                .build();
    }

    public Product productDtoToProduct(@Valid ProductRequest request) {

        try {
            Category category = categoryRepo.findByName(request.getCategory()).orElseThrow(() -> new RecordNotFoundException("Category not found!"));

            return Product.builder()
                    .sku(request.getSku())
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .category(category)
                    .active(true)
                    .build();
        } catch (Exception ex) {
            throw new BadRequestException("Error occurred due to the: "+ex);
        }
    }

    public CartItemResponse cartItemToCartItemResponseDto(CartItem item) {

        return CartItemResponse.builder()
                .id(item.getId())
                .productName(item.getProduct().getName())
                .qty(item.getQty())
                .priceSnapshot(item.getPriceSnapshot())
                .build();
    }

    public Inventory inventoryDtoToInventory(@Valid InventoryRequest request) {
        // Verifying product details in database
        Product productInfo=productRepo.findById(request.getProductId()).orElseThrow(()->new RecordNotFoundException("Product was not available!"));
        // verifying product record in Inventory
        Optional<Inventory> inventory=inventoryRepository.findByProductId(request.getProductId());
        if(inventory.isEmpty()) {
            return Inventory.builder()
                    .product(productInfo)
                    .version(request.getVersion())
                    .stock(request.getQuantity())
                    .warehouse(request.getWarehouse())
                    .build();
        }else{
            inventory.get().setStock(request.getQuantity());
            return inventory.get();
        }
    }

    public TrackerResponse trackerToTrackerResponse(ShipmentTracker trackInfo) {
        return TrackerResponse.builder()
                .trackingNumber(trackInfo.getTrackingNumber())
                .eventType(trackInfo.getEventType())
                .location(trackInfo.getLocation())
                .remarks(trackInfo.getRemarks())
                .eventTime(trackInfo.getEventTime())
                .build();
    }
}

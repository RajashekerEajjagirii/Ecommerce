package com.raj.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryRequest {

    @NotNull
    private Long productId;
    @Min(1)
    private int quantity;
    @NotBlank
    private String warehouse;
    @NotNull
    private int version;
}

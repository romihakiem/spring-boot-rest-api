package com.skeleton.api.dto.request;

import com.skeleton.api.entity.ItemStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String category;

    @DecimalMin(value = "0.0", message = "Price must not be negative")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must not be negative")
    private Integer stock;

    private ItemStatus status;
}

package com.Code.request;

import lombok.Data;

@Data
public class AddItemRequest {
    private String size;
    private int quantity;
    private long productId;
}

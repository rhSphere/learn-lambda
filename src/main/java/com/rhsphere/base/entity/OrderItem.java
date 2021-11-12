package com.rhsphere.base.entity;

import lombok.Data;

/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-03 11:10
 */
@Data
public class OrderItem {
    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer productQuantity;
}

package com.itheima.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderDetail implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    private String name;
    private Long orderId;
    private Long dishId;
    private Long setmealId;
    private String dishFlavor;
    private Integer number;//数量
    private BigDecimal amount;//金额
    private String image;//图片
}

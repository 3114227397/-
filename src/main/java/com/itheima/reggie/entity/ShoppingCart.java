package com.itheima.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShoppingCart implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;//购物车自己的id
    private String name;//名字
    private Long userId;//用户id
    private Long dishId;//菜品id
    private Long setmealId;//套餐id
    private String dishFlavor;
    private Integer number;//数量
    private BigDecimal amount;//数量
    private String image;//图片
    private LocalDateTime createTime;//创建时间

}

package com.itheima.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    private String number;
    private Integer status;//1代付款、2待派送。3已派送。4已完成。5已取消
    private Long userId;//下单用户
    private Long addressBookId;
    private LocalDateTime orderTime;
    private LocalDateTime checkoutTime;
    private Integer payMethod;//1微信。2支付宝
    private BigDecimal amount;
    private String remark;
    private String username;
    private String phone;
    private String address;
    private String consignee;//收货人
}

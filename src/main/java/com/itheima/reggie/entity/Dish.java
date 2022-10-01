package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Dish<LocalDateTime> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private String code;//商品码
    private String image;//图片地址
    private String description;
    private Integer status;//1：正在售卖。0：停售
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)//插入更新的时候填充子弹
    private Long updateUser;

}

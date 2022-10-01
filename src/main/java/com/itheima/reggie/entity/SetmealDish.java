package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SetmealDish implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    private Long setmealId;
    private Long dishId;
    private String name;
    private BigDecimal price;
    private Integer copies;//分数
    private Integer sort;//排序
    private Integer isDeleted;

    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill=FieldFill.INSERT)
    private Long createUser;
    @TableField(fill=FieldFill.INSERT_UPDATE)//插入更新的时候填充子弹
    private Long updateUser;

}

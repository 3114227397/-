package com.itheima.reggie.entity;
//import org.springframework.*;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Setmeal implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    private Long categoryId;//对应的菜品分类
    private String name;//套餐名字
    private BigDecimal price;
    private Integer status;
    private String code;//编码
    private String image;//图片

    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill=FieldFill.INSERT)
    private Long createUser;
    @TableField(fill=FieldFill.INSERT_UPDATE)//插入更新的时候填充子弹
    private Long updateUser;
}

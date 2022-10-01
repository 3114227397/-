package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AddressBook implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    private Long userId;
    private String consignee;//收货人
    private String sex;//0:女生,1:男生
    private String phone;
    private String provinceCode;//省级邮政编码
    private String cityCode;//城市邮编
    private String districtCode;//区级邮编
    private String detail;//详细地址
    private String label;//标签
    private Integer isDefault;//是否默认
    private Integer isDeleted;//是否删除

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    //修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}

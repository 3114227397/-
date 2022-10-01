package com.itheima.reggie.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID=1L;
    private Long id;
    private String name;
    private String phone;
    private String sex;
    private String idNumber;
    private String avatar;//头像
    private Integer status;//状态1：正常。0：禁用
}
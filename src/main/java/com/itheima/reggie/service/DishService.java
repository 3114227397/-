package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveVege(DishDto dishDto);

    public DishDto getByIdAddFlavor(Long id);

    public void updateAddFlavor(DishDto dishDto);

    public void deleteWithIds(List<Long> ids);

    public List<DishDto> getAll(Long id);
}

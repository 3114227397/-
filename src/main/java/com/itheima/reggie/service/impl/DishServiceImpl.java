package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishDto;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    //class DishServiceImpl
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public void saveVege(DishDto dishDto){
        this.save(dishDto);
        Long id=dishDto.getId();
        List<DishFlavor> fal=dishDto.getFlavors();
        for(DishFlavor d:fal){
            d.setDishId(id);//设置是哪个菜的
        }
        dishFlavorService.saveBatch(fal);
    }

    //class DishServiceImpl

    @Override
    public DishDto getByIdAddFlavor(Long id){
        Dish dish=this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,id);
        List<DishFlavor> listDf=dishFlavorService.list(lqw);
        dishDto.setFlavors(listDf);
        return dishDto;
    }

    /**
     * 修改
     * @param dishDto
     */
    //class DishServiceImpl
    @Override
    @Transactional
    public void updateAddFlavor(DishDto dishDto){
        this.updateById(dishDto);
        Long id=dishDto.getId();
        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();

       lqw.eq(DishFlavor::getDishId,id);

        dishFlavorService.remove(lqw);//移除
        //插入的话需要设置关联的dishId
        List<DishFlavor> listDf=dishDto.getFlavors();
        for(DishFlavor df:listDf){
            df.setDishId(id);
        }
        dishFlavorService.saveBatch(listDf);//批量插入
    }

    /**
     * 根据ids删除
     * @param ids
     */
    @Override
    public void deleteWithIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.in(Dish::getId,ids);
        lqw.eq(Dish::getStatus,1);
        int count = this.count(lqw);
        if(count>0)throw new CustomException("正在售卖中，不可以删除");
        LambdaQueryWrapper<Dish> lqw2=new LambdaQueryWrapper<>();
        lqw2.in(Dish::getId,ids);
        this.remove(lqw2);//先删除dish在删除口味表
        LambdaQueryWrapper<DishFlavor> lqw3=new LambdaQueryWrapper();
        lqw3.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lqw3);

    }


}

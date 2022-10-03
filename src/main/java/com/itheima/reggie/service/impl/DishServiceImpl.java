package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    //class DishServiceImpl
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    public void saveVege(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        List<DishFlavor> fal = dishDto.getFlavors();
        for (DishFlavor d : fal) {
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
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        lqw.eq(Dish::getStatus, 1);
        int count = this.count(lqw);
        if (count > 0) throw new CustomException("正在售卖中，不可以删除");
        LambdaQueryWrapper<Dish> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(Dish::getId, ids);
        this.remove(lqw2);//先删除dish在删除口味表
        LambdaQueryWrapper<DishFlavor> lqw3 = new LambdaQueryWrapper();
        lqw3.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(lqw3);

    }

    /**
     * 获取全部菜品
     *
     * @param id
     * @return
     */
    @Override
    public List<DishDto> getAll(Long id) {
        Dish dish = this.getById(id);
        DishDto dd = new DishDto();
        List<DishDto> list2 = new ArrayList<>();
        List<Dish> list1 = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        if (dish == null) {
            LambdaQueryWrapper<SetmealDish> lqw_sd = new LambdaQueryWrapper<>();
            lqw_sd.eq(SetmealDish::getSetmealId, id);
            List<SetmealDish> list_sd = setmealDishService.list(lqw_sd);
            ids = new ArrayList<>();
            for (SetmealDish sd : list_sd) {
                ids.add(sd.getDishId());
            }
            LambdaQueryWrapper<Dish> lqw_d = new LambdaQueryWrapper<>();
            lqw_d.in(Dish::getId, ids);
            list1 = this.list(lqw_d);
        }
        if (list1.size() == 0) {
            list1.add(dish);
        }
        for (Dish di : list1) {
            BeanUtils.copyProperties(di, dd);
            list2.add(dd);
        }
        for (DishDto dishDto : list2) {
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            if (list2.size() == 1) {
                lqw.eq(DishFlavor::getDishId, id);
            } else {
                lqw.in(DishFlavor::getDishId, ids);
            }
            List<DishFlavor> list = dishFlavorService.list(lqw);
            dishDto.setFlavors(list);
        }
        return list2;
    }


}

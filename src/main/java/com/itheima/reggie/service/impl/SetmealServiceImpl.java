package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.SetmealDto;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     */
    //SetmealServiceImpl
    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        this.save(setmealDto);
        List<SetmealDish> list_smd=setmealDto.getSetmealDishes();
        Long id=setmealDto.getId();
        for(SetmealDish smd:list_smd){
            smd.setSetmealId(id);
        }
        setmealDishService.saveBatch(list_smd);
    }


    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(lqw);//移除
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        for(SetmealDish sd:list){
            sd.setSetmealId(id);
        }
        setmealDishService.saveBatch(list);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void removeAddDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        lqw.eq(Setmeal::getStatus,1);
        int count=this.count(lqw);
        if(count>0)throw new CustomException("套餐正在售卖中，不可以删除");
        this.removeByIds(ids);//先删除套餐
        LambdaQueryWrapper<SetmealDish> lqw2=new LambdaQueryWrapper<>();
        lqw2.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw2);
    }


}

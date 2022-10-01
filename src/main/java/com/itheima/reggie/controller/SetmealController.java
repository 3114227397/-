package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.SetmealDto;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
//import org.springframework.web.*;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){


        Page<Setmeal> page1=new Page<>(page,pageSize);
        Page<SetmealDto> page2=new Page<>();
        BeanUtils.copyProperties(page1,page2,"records");
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1,queryWrapper);
        List<Setmeal> records=page1.getRecords();
        List<SetmealDto> list_sd=new ArrayList<>();
        for(Setmeal s:records){
            SetmealDto sd=new SetmealDto();
            BeanUtils.copyProperties(s,sd);
            Long CategoryId=s.getCategoryId();
            LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();
            lqw.eq(Category::getId,CategoryId);
            Category c= categoryService.getOne(lqw);
            if(c!=null){
                sd.setCategoryName(c.getName());
                list_sd.add(sd);
            }
        }
        page2.setRecords(list_sd);
        return R.success(page2);
    }

    /**
     * 套餐添加
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐添加成功");
    }


    /**
     * 根据套餐id去返回信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getbyid(@PathVariable Long id){
        Setmeal s1 = setmealService.getById(id);//setmeal的Id
        SetmealDto s2=new SetmealDto();
        BeanUtils.copyProperties(s1,s2);
        LambdaQueryWrapper<SetmealDish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lqw);
        s2.setSetmealDishes(list);
        return R.success(s2);
    }

    /**
     * 套餐修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    @PostMapping("/status/0")
    public R<String> status0(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(lqw);
        Integer status =null;
        for(Setmeal s:list){
            status= s.getStatus();
            status=status==1?0:1;
            s.setStatus(status);
        }//修改状态
        setmealService.updateBatchById(list);
        return R.success("修改成功");
    }

    @PostMapping("/status/1")
    public R<String> status1(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(lqw);
        Integer status =null;
        for(Setmeal s:list){
            status= s.getStatus();
            status=status==1?0:1;
            s.setStatus(status);
        }//修改状态
        setmealService.updateBatchById(list);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeAddDish(ids);
        return R.success("套餐删除成功");
    }


    //SetmealController
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lqw=new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus()==1,Setmeal::getStatus,1);
        return R.success(setmealService.list(lqw));
    }

}

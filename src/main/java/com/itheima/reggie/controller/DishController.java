package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        LambdaQueryWrapper<Dish> lqw1=new LambdaQueryWrapper<>();
        lqw1.like(name!=null,Dish::getName,name);
        lqw1.orderByDesc(Dish::getUpdateTime);

        Page<Dish> page1=new Page<>(page,pageSize);
        Page<DishDto> page2=new Page<>();
        dishService.page(page1,lqw1);
        BeanUtils.copyProperties(page1,page2,"records");//除了records这个属性，其他都拷贝
        List<Dish> records=page1.getRecords();//这里包含了主要信息
        List<DishDto> list=new ArrayList<>();
        //
        for(Dish d:records){
            DishDto dd=new DishDto();
            BeanUtils.copyProperties(d,dd);
            Long id=d.getCategoryId();
            LambdaQueryWrapper<Category> lqw=new LambdaQueryWrapper<>();
            lqw.eq(Category::getId,id);
            Category c=categoryService.getOne(lqw);//获取Category的表信息
            if(c!=null){
                dd.setCategoryName(c.getName());//设置名字
                list.add(dd);
            }
        }//
        page2.setRecords(list);
        return R.success(page2);
    }

    /**
     * 新增菜品分类
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        dishService.saveVege(dishDto);
        return R.success("新增成功");
    }

    /**
     * 根据id获取信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getByiii(@PathVariable Long id) {
        DishDto d = dishService.getByIdAddFlavor(id);
        return R.success(d);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        dishService.updateAddFlavor(dishDto);
        return R.success("修改成功");
    }


    /**
     * 根据CategoryId返回菜品信息.
     * 移动端分页
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getbyid(Dish dish) {
//        Long id = category.getId();
        Long id = dish.getCategoryId();
        String key = "dish_" + id + "_1";
        List<DishDto> o = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (o != null && dish.getStatus() != 1) {
            return R.success(o);
        }
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, id);
        lqw.eq(Dish::getStatus, 1);
        List<Dish> list = dishService.list(lqw);
        List<DishDto> list2 = new ArrayList<>();
        for (Dish d : list) {
            DishDto dd = new DishDto();
            BeanUtils.copyProperties(d, dd);
            Long categoryId = d.getCategoryId();
            Category c = categoryService.getById(categoryId);
            if (c != null) {
                dd.setCategoryName(c.getName());
            }
            LambdaQueryWrapper<DishFlavor> lqw_df = new LambdaQueryWrapper<>();
            lqw_df.eq(DishFlavor::getDishId, d.getId());
            List<DishFlavor> list_f = dishFlavorService.list(lqw_df);
            dd.setFlavors(list_f);
            list2.add(dd);
        }
        redisTemplate.opsForValue().set(key, list2, 30, TimeUnit.MINUTES);//30min存活时间
        return R.success(list2);
    }

    /**
     * 批量停售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> status0(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        List<Dish> list = dishService.list(lqw);
        Integer status = null;
        for (Dish s : list) {
            status = s.getStatus();
//            String key="dish_"+s.getCategoryId()+"_"+status;
//            redisTemplate.delete(key);
            status = status == 1 ? 0 : 1;
            s.setStatus(status);
        }//修改状态
        dishService.updateBatchById(list);
        return R.success("修改成功");
    }

    /**
     * 批量起售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> status1(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        List<Dish> list = dishService.list(lqw);
        Integer status = null;
        for (Dish s : list) {
            status = s.getStatus();
            status = s.getStatus();
//            String key="dish_"+s.getCategoryId()+"_1";
//            redisTemplate.delete(key);
            status = status == 1 ? 0 : 1;
            s.setStatus(status);
        }//修改状态
        dishService.updateBatchById(list);
        return R.success("修改成功");
    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.deleteWithIds(ids);
        return R.success("删除成功");
    }



}

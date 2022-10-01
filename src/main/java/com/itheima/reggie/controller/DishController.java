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
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveVege(dishDto);
        return R.success("新增成功");

    }

    /**
     * 修改菜品分类
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> update(@PathVariable Long id){
        DishDto d=dishService.getByIdAddFlavor(id);
        return R.success(d);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateAddFlavor(dishDto);
        return R.success("修改成功");
    }


    /**
     * 根据CategoryId返回菜品信息
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> getbyid(Dish dish){
//        Long id = category.getId();
        Long id = dish.getCategoryId();
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId,id);
        List<Dish> list = dishService.list(lqw);
        return R.success(list);
    }

    @PostMapping("/status/0")
    public R<String> status0(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.in(Dish::getId,ids);
        List<Dish> list = dishService.list(lqw);
        Integer status =null;
        for(Dish s:list){
            status= s.getStatus();
            status=status==1?0:1;
            s.setStatus(status);
        }//修改状态
        dishService.updateBatchById(list);
        return R.success("修改成功");
    }

    @PostMapping("/status/1")
    public R<String> status1(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.in(Dish::getId,ids);
        List<Dish> list = dishService.list(lqw);
        Integer status =null;
        for(Dish s:list){
            status= s.getStatus();
            status=status==1?0:1;
            s.setStatus(status);
        }//修改状态
        dishService.updateBatchById(list);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithIds(ids);
        return R.success("删除成功");
    }


//    @GetMapping("/list")
//    public R<List<DishDto>> list(Dish dish){
//        List<DishDto> dishDtoList=null;
//        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
//        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
//        if(dishDtoList!=null){
//            return R.success(dishDtoList);
//        }
//
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
//        //添加条件，查询状态为1（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        dishDtoList = list.stream().map((item) -> {
//            DishDto dishDto = new DishDto();
//
//            BeanUtils.copyProperties(item,dishDto);
//
//            Long categoryId = item.getCategoryId();//分类id
//            //根据id查询分类对象
//            Category category = categoryService.getById(categoryId);
//
//            if(category != null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//
//            //当前菜品的id
//            Long dishId = item.getId();
//            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
//            //SQL:select * from dish_flavor where dish_id = ?
//            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
//            dishDto.setFlavors(dishFlavorList);
//            return dishDto;
//        }).collect(Collectors.toList());
//        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
//        return R.success(dishDtoList);
//    }


}

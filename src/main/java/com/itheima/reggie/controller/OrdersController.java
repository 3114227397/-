package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.impl.OrdersServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        log.info("用户分页");
        Page<Orders> pageInfo=new Page<>(page,pageSize);
//        LambdaQueryWrapper<Orders> lqw=new LambdaQueryWrapper<>();
//        lqw.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo);
        return R.success(pageInfo);
    }

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }


}

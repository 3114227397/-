package com.itheima.reggie.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders){
        Long userId= BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw_sc=new LambdaQueryWrapper<>();
        lqw_sc.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list_sc=shoppingCartService.list(lqw_sc);
        if(list_sc==null||list_sc.size()==0)throw new CustomException("购物车已空，不能下单");
        User u=userService.getById(userId);
        Long addressBookId=orders.getAddressBookId();
        AddressBook ab=addressBookService.getById(addressBookId);
        if(ab==null)throw new CustomException("地址信息有误，不能下单");
        Long orderId= IdWorker.getId();//MP提供方法获取订单号
        AtomicInteger amount=new AtomicInteger(0);
        List<OrderDetail> orderDetails=new ArrayList<>();
        for(ShoppingCart sc:list_sc){
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(sc.getNumber());
            orderDetail.setDishFlavor(sc.getDishFlavor());
            orderDetail.setDishId(sc.getDishId());
            orderDetail.setSetmealId(sc.getSetmealId());
            orderDetail.setName(sc.getName());
            orderDetail.setImage(sc.getImage());
            orderDetail.setAmount(sc.getAmount());
            amount.addAndGet(sc.getAmount().multiply(new BigDecimal(sc.getNumber())).intValue());//amount=sc.getAmount()*sc.getNumber().单价*数量
            orderDetails.add(orderDetail);
        }
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//钱
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(u.getName());
        orders.setConsignee(ab.getConsignee());
        orders.setPhone(ab.getPhone());
        orders.setAddress(ab.getProvinceCode()==null?"":ab.getProvinceCode()
                +(ab.getCityCode()==null?"":ab.getCityCode())
                +(ab.getDistrictCode()==null?"":ab.getDistrictCode())
                +(ab.getDetail()==null?"":ab.getDetail())
        );
        this.save(orders);
        orderDetailService.saveBatch(orderDetails);
        shoppingCartService.remove(lqw_sc);
    }
}

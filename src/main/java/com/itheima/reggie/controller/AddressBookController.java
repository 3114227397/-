package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
//    @PutMapping("/default")
//    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
//        LambdaUpdateWrapper<AddressBook> lqw=new LambdaUpdateWrapper<>();
//        lqw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
//        lqw.set(AddressBook::getUserId,0);//把所有都设置成非默认
//        addressBook.setIsDefault(1);//单独把这个设置成默认
//        addressBookService.updateById(addressBook);//select * from addreeBook set 全部信息=传进来的实体类 去更新 where id=#{addressBook.getUserId};
//        return R.success(addressBook);
//    }


    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> lqw=new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getIsDefault,1);
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        AddressBook ab=addressBookService.getOne(lqw);
        if(ab==null)return R.error("没有找到对象");
        return R.success(ab);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());//设置指向那个用户
        addressBookService.save(addressBook);
        return R.success(addressBook);//返回，因为要回显
    }


    /**
     * 根据id获取用户地址信息
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> lqw=new LambdaQueryWrapper<>();
        lqw.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        lqw.orderByDesc(AddressBook::getUpdateTime);
        return R.success(addressBookService.list(lqw));
    }

    /**
     * 根据id查询地址信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> update(@PathVariable Long id){
        log.info("根据id查询地址信息");
        AddressBook ab = addressBookService.getById(id);
        return R.success(ab);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("修改地址信息");
        addressBookService.updateById(addressBook);
        return R.success("修改地址信息成功");
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除中");
        addressBookService.delete(ids);
        return R.success("删除成功");
    }

}

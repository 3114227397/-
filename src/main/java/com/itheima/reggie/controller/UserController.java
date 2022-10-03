package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        log.info("发送验证码");
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            redisTemplate.opsForValue().set(phone,code);//放进缓存
            //SMSUtils可以发送信息了
            log.info("验证码是:{}",code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpServletRequest request){
        String code = (String) map.get("code");
        String phone = (String) map.get("phone");
        String codeInRedis =(String) redisTemplate.opsForValue().get(phone);
        if(codeInRedis!=null&&codeInRedis.equals(code)){//登录成功
            LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User u=userService.getOne(lqw);
            if(u==null){
                u=new User();
                u.setPhone(phone);
                u.setStatus(1);
                u.setName("张三");
                userService.save(u);
            }
            request.getSession().setAttribute("user",u.getId());//放进session
//            Long currentId = BaseContext.getCurrentId();
//            System.out.println(currentId);
            redisTemplate.delete(phone);
            return R.success("登录成功");
        }


        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}

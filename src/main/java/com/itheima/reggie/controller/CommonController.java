package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController{
    @Value("${reggie.basePath}")//D:\img\
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//file是临时的，所以我们需要把用户选择的file进行转存
        String originalFilename=file.getOriginalFilename();
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));//截取的是".jpg"后缀
        String fileName= UUID.randomUUID().toString()+suffix;//随机生成UUID序列toString之后加上后缀
        File dir=new File(basePath);//防止目录不存在，准备创建目录
        if(!dir.exists()){
            dir.mkdirs();//目录不存在，创建目录
        }
        try{
            file.transferTo(new File(basePath+fileName));//转存到D:\img\目录下的名为fileName的地方
        }catch(IOException e){
            e.printStackTrace();
        }
        return R.success(fileName);//返回文件名
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try{
            FileInputStream fis=new FileInputStream(basePath+name);
            ServletOutputStream os=response.getOutputStream();//这里获取并确定输出的地点是浏览器
            response.setContentType("image/jpeg");//确认输出格式
            int len =0;
            byte []bytes=new byte[1024];
            while((len=fis.read(bytes))!=-1){
                //os.write(len);这种格式写的话不会报错，但是限制大小，很慢。所以不可取
                os.write(bytes,0,len);//输出到浏览器
                os.flush();
            }
            os.close();
            fis.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

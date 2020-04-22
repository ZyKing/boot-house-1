package com.etoak.controller;

import com.etoak.Utils.ValidationUtil;
import com.etoak.bean.House;
import com.etoak.exception.ParamException;
import com.etoak.service.HouseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/house")
public class HouseController {
    @Value("${upload.dir}")
    private String uploadDirectory;
    @Value("${upload.savePathPrefix}")
    private String savePathPrefix;

    @Autowired
    HouseService houseService;

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "house/add";
    }
    @PostMapping("/add")
    public String add(@RequestParam("file")MultipartFile file, House house) throws Exception {
        ValidationUtil.validate(house);
        String originalFilename = file.getOriginalFilename();
        String prefix = UUID.randomUUID().toString().replaceAll("-","");
        String newFilename = prefix + "_" + originalFilename;
        File destFile = new File(this.uploadDirectory,newFilename);
        file.transferTo(destFile);
        // 设置pic访问地址
        house.setPic(this.savePathPrefix + newFilename);
        houseService.addHouse(house);
        return "redirect:/house/toAdd";
    }
    @PostMapping("/add2")
    public String add2(@RequestParam("file")MultipartFile file, @Valid House house, BindingResult bingResult) throws Exception {
        List<ObjectError>allErrors = bingResult.getAllErrors();
        if (CollectionUtils.isNotEmpty(allErrors)){
            StringBuffer msgBuffer = new StringBuffer();
            for(ObjectError objectError : allErrors){
                String message = objectError.getDefaultMessage();
                msgBuffer.append(message).append(";");
            }
            throw new ParamException("参数校验失败："+msgBuffer.toString());
        }

        /*String originalFilename = file.getOriginalFilename();
        String prefix = UUID.randomUUID().toString().replaceAll("-","");
        String newFilename = prefix + "_" + originalFilename;
        File destFile = new File(this.uploadDirectory,newFilename);
        file.transferTo(destFile);
        // 设置pic访问地址
        house.setPic(this.savePathPrefix + newFilename);
        houseService.addHouse(house);*/
        return "redirect:/house/toAdd";
    }

}

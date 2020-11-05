package com.sdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @RequestMapping("/hello")
    //@ResponseBody
    public String HelloWorld(ModelMap map){
        map.addAttribute("host","hello");
        return "t";
    }
}

package com.example.tournament.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// หน้าเว็บ Vue ใช้ history mode จึงต้องส่ง index.html กลับเมื่อเปิด URL ของหน้าเว็บตรงๆ
@Controller
public class SpaController {

    @GetMapping({"/tournaments", "/tournaments/**", "/admin", "/admin/**"})
    public String index() {
        return "forward:/index.html";
    }
}

package com.shopdevjava.springboot.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class Hello {
    @GetMapping("/v1/api/hello")
    public String Status() {
        return "Hello World";
    }
}

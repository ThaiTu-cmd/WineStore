package com.doan.ProFit.controller.client;

import com.doan.ProFit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ClientController {
    @RequestMapping("/home")
    public String client() {
        return "client/index";
    }
}
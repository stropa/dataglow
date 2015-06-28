package ru.rbs.stats.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UsersController {

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }


}

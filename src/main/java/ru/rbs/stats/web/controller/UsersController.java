package ru.rbs.stats.web.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.rbs.stats.service.UserService;
import ru.rbs.stats.web.dto.UserAccountDto;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {

    @Resource
    private UserService userService;

    @Resource
    private ModelMapper modelMapper;

    @RequestMapping("/list")
    @ResponseBody
    public List<UserAccountDto> listAll() {

        //return mappingHelper.mapList(userService.getAll(), UserAccountDto.class);
        return modelMapper.map(userService.getAll(), new TypeToken<List<UserAccountDto>>() {}.getType());
    }

}

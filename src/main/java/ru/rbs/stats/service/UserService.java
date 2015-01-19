package ru.rbs.stats.service;

import org.springframework.stereotype.Service;
import ru.rbs.stats.model.tables.daos.UserAccountDao;
import ru.rbs.stats.model.tables.pojos.UserAccount;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserAccountDao userAccountDao;

    public List<UserAccount> getAll() {
        return userAccountDao.findAll();
    }

}

package com.gujiedmc.demo.service;

/**
 * @author admin
 * @date 2019/12/19
 */
public class UserServiceImpl implements UserService {

    private String name;

    public UserServiceImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {

        return name;
    }
}

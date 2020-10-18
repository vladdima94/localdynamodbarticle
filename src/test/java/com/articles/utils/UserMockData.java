package com.articles.utils;

import com.articles.persistance.users.dao.UserModel;

public interface UserMockData {
    

    public static final UserModel USER_NEW = UserModel.builder()
        .username("userNew")
        .email("userNew@example.com")
        .isActive(true)
        .rating(Double.valueOf(4.3)).build();

    public static final UserModel USER_EXISTS = UserModel.builder()
        .username("userExists")
        .email("userExists@example.com")
        .isActive(false)
        .rating(Double.valueOf(4.3)).build();
}

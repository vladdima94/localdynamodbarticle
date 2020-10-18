package com.articles.persistance.users.impl;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.articles.exceptions.UserAlreadyExistsException;
import com.articles.exceptions.UserNotFoundException;
import com.articles.persistance.users.UserDAO;
import com.articles.persistance.users.dao.UserModel;

import lombok.extern.log4j.Log4j2;


@Log4j2
public class UserDAODynamoDB implements UserDAO {

    private final DynamoDBMapper ddbClient;

    public UserDAODynamoDB(DynamoDBMapper ddbClient) {
        this.ddbClient = ddbClient;
    }

    @Override
    public void createUser(UserModel user) {
        log.info("Beginning UserRepoDynamoDB::createUser()");

        getUser(user).ifPresent(foundUser -> { throw new UserAlreadyExistsException("User already exists"); });

        ddbClient.save(user);
    }

    @Override
    public Optional<UserModel> getUser(UserModel user) {
        log.info("Beginning UserRepoDynamoDB::getUser()");

        return Optional.ofNullable(ddbClient.load(UserModel.class, user.getUsername()));
    }

    @Override
    public void updateUser(UserModel user) {
        log.info("Beginning UserRepoDynamoDB::updateUser()");
        getUser(user).orElseThrow(() -> new UserNotFoundException("User does not exist"));

        ddbClient.save(user);
    }

    @Override
    public void deleteUser(UserModel user) {
        log.info("Beginning UserRepoDynamoDB::deleteUser()");
        ddbClient.delete(user);
    }
    
}

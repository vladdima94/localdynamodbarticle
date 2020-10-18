package com.articles.persistance.users.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.articles.exceptions.UserAlreadyExistsException;
import com.articles.exceptions.UserNotFoundException;
import com.articles.persistance.users.UserDAO;
import com.articles.persistance.users.dao.UserModel;
import com.articles.utils.DynamoDBLocalStarterTest;
import com.articles.utils.UserMockData;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserRepoDynamoDBTest {

    private static final String USERS_TABLE_NAME = "beta-us-west-2-users";
    private static AmazonDynamoDB ddb;

    private DynamoDBMapper ddbClient;
    private UserDAO userDAO;

    @BeforeAll
    public static void setUp() {
        ddb = DynamoDBLocalStarterTest.startDDBLocal();
    }

    @AfterAll
    public static void cleanUp() {
        DynamoDBLocalStarterTest.stopDDBLocal(ddb);
    }

    @BeforeEach
    public void beforeEachSetUp() {
        ddbClient = new DynamoDBMapper(ddb);

        DynamoDBLocalStarterTest.createTable(
            ddb, USERS_TABLE_NAME, UserModel.HASH_KEY, "");

        userDAO = new UserDAODynamoDB(ddbClient);

        userDAO.createUser(UserMockData.USER_EXISTS);
    }
    
    @AfterEach
    public void afterEachCleanUp() {
        DynamoDBLocalStarterTest.deleteTable(ddb, USERS_TABLE_NAME);
    }

    @Test
    public void Given_UserRepoDynamoDB_Then_CreateAndReadUserSuccessfully() {

        userDAO.createUser(UserMockData.USER_NEW);

        UserModel loadedUser = userDAO.getUser(UserMockData.USER_NEW).get();

        assertEquals(UserMockData.USER_NEW, loadedUser);
    }

    @Test
    public void Given_UserRepoDynamoDB_Then_UpdateAndReadUserSuccessfully() {

        UserMockData.USER_EXISTS.setIsActive(true);
        userDAO.updateUser(UserMockData.USER_EXISTS);

        UserModel loadedUser = userDAO.getUser(UserMockData.USER_EXISTS).get();

        assertEquals(UserMockData.USER_EXISTS, loadedUser);
    }

    @Test
    public void Given_UserRepoDynamoDBAndExistingUser_Then_CreateUserThrowsException() {
        assertThrows(UserAlreadyExistsException.class, () -> {
            userDAO.createUser(UserMockData.USER_EXISTS);
        });
    }

    @Test
    public void Given_UserRepoDynamoDBAndNonExistingUser_Then_UpdateUserThrowsException() {
        assertThrows(UserNotFoundException.class, () -> {
            userDAO.updateUser(UserMockData.USER_NEW);
        });
    }

    @Test
    public void Given_UserRepoDynamoDB_Then_DeleteSuccessfully() {
        userDAO.deleteUser(UserMockData.USER_EXISTS);

        assertFalse(userDAO.getUser(UserMockData.USER_EXISTS).isPresent());
    }
    
}

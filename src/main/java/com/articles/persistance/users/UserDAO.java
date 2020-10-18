package com.articles.persistance.users;

import java.util.Optional;

import com.articles.persistance.users.dao.UserModel;

public interface UserDAO {
    
    /**
     * Create a new user by storing it in database.
     * @param user POJO containing the information to be persisted in storage.
     * @throws UserAlreadyExistsException excetion is thrown if user already exists. 
     */
    public void createUser(UserModel user);
    
    /**
     * Retrieve user from database.
     * @param user POJO containing the username which is used to query the database
     * @return Optional<UserModel> optional that could contain the UserModel from database.
     *         if it was found.
     */
    public Optional<UserModel> getUser(UserModel user);
    
    /**
     * Update user from database.
     * @param user POJO containing the information that will be updated in database.
     * @throws UserNotFoundException excetion is thrown if user already exists. 
     */
    public void updateUser(UserModel user);
    
    /**
     * Delete user from database.
     * @param user POJO containing the username which is used to query the database
     */
    public void deleteUser(UserModel user);

}

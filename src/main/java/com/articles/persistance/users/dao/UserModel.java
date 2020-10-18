package com.articles.persistance.users.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "beta-us-west-2-users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    public static final String HASH_KEY = "username";
    
    @DynamoDBHashKey(attributeName = HASH_KEY)
    private String username;
    
    @DynamoDBAttribute(attributeName = "email")
    private String email;
    
    @DynamoDBAttribute(attributeName = "rating")
    private Double rating;
    
    @DynamoDBAttribute(attributeName = "isActive")
    private Boolean isActive;

}

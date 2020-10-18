package com.articles.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.google.common.base.Strings;

import org.junit.jupiter.api.Test;

public class DynamoDBLocalStarterTest {

    private static final String TEST_TABLE_NAME = "user_tests";
    private static final String TEST_TABLE_HASH_KEY = "user_id";
    private static final String TEST_TABLE_RANGE_KEY = "user_type";

    /**
     * Start local DynamoDB. This is recomended to be used in @BeforeAll so
     * you don't end up starting and stopping it after each test case. 
     * @return AmazonDynamoDB instance used to access the DDB
     */
    public static AmazonDynamoDB startDDBLocal() {
        return DynamoDBEmbedded.create().amazonDynamoDB();
    }

    /**
     * Stop the Local DynamoDB. This is recomended to be used in @AfterAll so
     * you don't end up starting and stopping it after each test case.
     * @param ddb AmazonDynamoDB interface used to stop the Local DDB
     */
    public static void stopDDBLocal(AmazonDynamoDB ddb) {
        ddb.shutdown();
    }

    /**
     * Create a DynamoDB table using the AmazonDynamoDB client.
     * @param ddb AmazonDynamoDB instance used to access the DDB
     * @param tableName name of the table that will to be created
     * @param hashKeyName HASH Key of the table that will to be created
     * @param rangeKeyName RANGE Key of the table that will to be created. If no RANGE Key is
     *                     provided, then the table will not have a RANGE Key set.
     * @return
     */
    public static CreateTableResult createTable(AmazonDynamoDB ddb, String tableName, String hashKeyName, String rangeKeyName) {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        List<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();

        attributeDefinitions.add(new AttributeDefinition(hashKeyName, ScalarAttributeType.S));
        ks.add(new KeySchemaElement(hashKeyName, KeyType.HASH));

        if(!Strings.isNullOrEmpty(rangeKeyName)) {
            attributeDefinitions.add(new AttributeDefinition(rangeKeyName, ScalarAttributeType.S));
            ks.add(new KeySchemaElement(rangeKeyName, KeyType.RANGE));
        }

        ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);

        CreateTableRequest request =
            new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(ks)
                .withProvisionedThroughput(provisionedthroughput);

        return ddb.createTable(request);
    }

    /**
     * Delete a DynamoDB table using the AmazonDynamoDB client
     * @param ddb AmazonDynamoDB instance used to access the DDB
     * @param tableName name of the table that will to be deleted
     * @return
     */
    public static DeleteTableResult deleteTable(AmazonDynamoDB ddb, String tableName) {
        return ddb.deleteTable(tableName);
    }

    @Test
    public void createTableTest() {
        AmazonDynamoDB ddb = startDDBLocal();

        try {
            CreateTableResult res = createTable(ddb, TEST_TABLE_NAME, TEST_TABLE_HASH_KEY, TEST_TABLE_RANGE_KEY);

            TableDescription tableDesc = res.getTableDescription();
            assertEquals(TEST_TABLE_NAME, tableDesc.getTableName());
            assertEquals("[{AttributeName: " + TEST_TABLE_HASH_KEY + ",KeyType: HASH}, {AttributeName: " + TEST_TABLE_RANGE_KEY + ",KeyType: RANGE}]",
                tableDesc.getKeySchema().toString());
            assertEquals("[{AttributeName: " + TEST_TABLE_HASH_KEY + ",AttributeType: S}, {AttributeName: " + TEST_TABLE_RANGE_KEY + ",AttributeType: S}]",
                tableDesc.getAttributeDefinitions().toString());
            assertEquals(Long.valueOf(1000L), tableDesc.getProvisionedThroughput().getReadCapacityUnits());
            assertEquals(Long.valueOf(1000L), tableDesc.getProvisionedThroughput().getWriteCapacityUnits());
            assertEquals("ACTIVE", tableDesc.getTableStatus());
            assertEquals("arn:aws:dynamodb:ddblocal:000000000000:table/user_tests", tableDesc.getTableArn());

            assertEquals(1, ddb.listTables().getTableNames().size());
        } finally {
            stopDDBLocal(ddb);
        }
    }

}
package com.mani.cucumber;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class DynamoDBClientInfo implements ClientInfo {

	public AmazonWebServiceClient getClient() {
		return new AmazonDynamoDBClient();
	}

	public String getPackageName() {
		return "com.amazonaws.services.dynamodbv2";
	}
}
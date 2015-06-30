package com.mani.cucumber;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;

public class CognitoClientInfo implements ClientInfo {

    public AmazonWebServiceClient getClient() {
        return new AmazonCognitoIdentityClient();
    }

    public String getPackageName() {
        return "com.amazonaws.services.cognitoidentity";
    }
}
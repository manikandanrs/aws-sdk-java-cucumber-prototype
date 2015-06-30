package com.mani.cucumber;

import com.amazonaws.AmazonWebServiceClient;

public interface ClientInfo {
    AmazonWebServiceClient getClient();
    String getPackageName();
}
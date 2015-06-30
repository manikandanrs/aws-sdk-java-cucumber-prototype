package com.mani.cucumber;

import com.amazonaws.AmazonWebServiceClient;

/**
 * Information about every AWS service Java client that is needed to run the test test scenarios.
 */
public interface ClientInfo {
    AmazonWebServiceClient getClient();
    String getPackageName();
}
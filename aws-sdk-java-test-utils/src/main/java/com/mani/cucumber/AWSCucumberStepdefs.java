package com.mani.cucumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceClient;
import com.google.inject.Inject;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.java.guice.ScenarioScoped;

@ScenarioScoped
public class AWSCucumberStepdefs {

    private AmazonWebServiceClient client;
    private String packageName;
    private Object result;
    private AmazonClientException exception;

    @Inject
    public AWSCucumberStepdefs(ClientInfo clientInfo) {
        this.client = clientInfo.getClient();
        this.packageName = clientInfo.getPackageName();
    }

    @When("^I call the \"(.*?)\" API$")
    public void when_I_call_the_API(String operation)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        call(operation, null, false);
    }

    @When("^I call the \"(.*?)\" API with:$")
    public void when_I_call_the_API(String operation, Map<String, String> args)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        call(operation, args, false);
    }

    @When("^I attempt to call the \"(.+?)\" API with:$")
    public void when_I_attempt_to_call_API(String operation, Map<String, String> args) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        call(operation, args, true);
    }

    @Then("^the value at \"(.*?)\" should be a list")
    public void then_the_value_at_should_be_a_list(String memberName) {

        Object member = ReflectionUtils.getByPath(result,
                Arrays.asList(memberName));

        assertTrue(member instanceof java.util.List);
    }

    @Then("^I expect the response error code to be \"(.+?)\"$")
    public void then_I_expect_response_error_code(String expected) {
        assertNotNull(exception);
        String actual = (String)ReflectionUtils.getByPath(exception, Arrays.asList("errorCode"));
        assertEquals("Error code doesn't match. Expected : " + expected + ". Actual :" + actual, expected, actual);
    }

    @And("^I expect the response error message to include:$")
    public void and_I_expect_the_response_error_message_include(String expected) {
        assertNotNull(exception);
        String actual = (String)ReflectionUtils.getByPath(exception, Arrays.asList("errorMessage"));
        assertTrue("Error message doesn't match. Expected : " + expected + ". Actual :" + actual, actual.contains(expected));
    }

    private void call(String operation, Map<String, String> args, boolean allowError)
            throws IllegalAccessException, IllegalArgumentException {

        final String requestClassName = packageName + ".model." + operation
                + "Request";
        final String operationMethodName = operation.substring(0, 1).toLowerCase() + operation.substring(1);
        Class<Object> requestClass = ReflectionUtils.loadClass(this.getClass(),
                requestClassName);
        Object requestObject = ReflectionUtils.newInstance(requestClass);

        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, String> entry : args.entrySet()) {

                Object value = convertTo(
                        ReflectionUtils.getParameterTypes(requestObject,
                                Arrays.asList(entry.getKey())),
                        entry.getValue());

                ReflectionUtils.setByPath(requestObject, value,
                        Arrays.asList(entry.getKey()));
            }
        }

        Method method = ReflectionUtils.findMethod(client, operationMethodName,
                requestClass);

        try {
            result = method.invoke(client, requestObject);
        } catch (InvocationTargetException ite) {
            if (allowError) {
                exception = (AmazonClientException)ite.getCause();
            } else {
                fail("Failed to execute the operation " + operation);
            }
        }
    }

    private Object convertTo(Class<?> type, String value) {

        if (type.equals(Integer.class))
            return Integer.valueOf(value);
        else if (type.equals(Boolean.class))
            return Boolean.valueOf(value);
        return value;
    }


    static class ServiceConfig {
        private String serviceInterfaceName;
        private String sourcePackageName;

        public void setServiceInterfaceName(String serviceInterfaceName) {
            this.serviceInterfaceName = serviceInterfaceName;
        }

        public String getServiceInterfaceName() {
            return this.serviceInterfaceName;
        }

        public void setSourcePackageName(String sourcePackageName) {
            this.sourcePackageName = sourcePackageName;
        }

        public String getSourcePackageName() {
            return this.sourcePackageName;
        }
    }
}

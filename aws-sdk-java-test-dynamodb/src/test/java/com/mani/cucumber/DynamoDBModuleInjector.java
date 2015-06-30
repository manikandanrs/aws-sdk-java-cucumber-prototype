package com.mani.cucumber;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import cucumber.api.guice.CucumberModules;
import cucumber.runtime.java.guice.InjectorSource;

/**
 * Injector that binds the ClientInfo interface to the DynamoDBClientInfo.
 */

public class DynamoDBModuleInjector implements InjectorSource {

    @Override
    public Injector getInjector() {
        return Guice.createInjector(Stage.PRODUCTION, CucumberModules.SCENARIO, new DynamoDBModule());
    }

    static class DynamoDBModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(ClientInfo.class).to(DynamoDBClientInfo.class);
        }
    }
}
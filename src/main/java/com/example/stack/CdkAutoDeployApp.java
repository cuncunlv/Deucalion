package com.example.stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkAutoDeployApp {
    public static void main(final String[] args) {
        App app = new App();
        new CdkAutoDeployStack(app, "AwscdkStack", StackProps.builder()
                .env(Environment.builder()
//                        .account("")
                        .region("us-east-1")
                        .build())
                .build());
        app.synth();
    }
}


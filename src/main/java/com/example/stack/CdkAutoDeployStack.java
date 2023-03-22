package com.example.stack;

import com.example.stack.function.HelloLambda;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class CdkAutoDeployStack extends Stack {
    public CdkAutoDeployStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CdkAutoDeployStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        final Function hello = Function.Builder.create(this, "HelloHandle")
                .runtime(Runtime.JAVA_8)    // execution environment
                .code(Code.fromAsset("target/Deucalion-0.1.jar"))  // code loaded from the "lambda" directory
                .handler("com.example.stack.function.HelloLambda::handle")        // file is "hello", function is "handler"
                .build();
        HelloLambda helloLambda = new HelloLambda();
        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "CdkAutoDeployQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();
    }
}

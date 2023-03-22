package com.example.auto.application;

import com.example.auto.timer.AutoDeployTask;

import java.util.ResourceBundle;
import java.util.Timer;

public class AutoDeployApplication {
    private final static Long EXECUTE_PERIOD_TIME;

    private final static String PROJECT_PATH;

    private final static String DEPLOY_SHELL_LOCATION;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("config");
        EXECUTE_PERIOD_TIME = Long.parseLong(bundle.getString("cdk.watch.time"));
        PROJECT_PATH = bundle.getString("cdk.stack.location");
        DEPLOY_SHELL_LOCATION = bundle.getString("cdk.deploy.shell.location");
    }

    public static void execute() {
        AutoDeployTask autoDeployTask = AutoDeployTask.getInstance(PROJECT_PATH, DEPLOY_SHELL_LOCATION);
        Timer timer = new Timer();
        timer.schedule(autoDeployTask, 0, EXECUTE_PERIOD_TIME);
    }
}

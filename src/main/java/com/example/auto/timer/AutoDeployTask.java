package com.example.auto.timer;

import com.example.auto.util.ClassAbsPathUtils;
import com.example.auto.util.FileContentReader;
import com.example.auto.util.MD5DigestUtils;
import com.example.auto.util.SingleThreadHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutoDeployTask extends TimerTask {

    private final String PATH;

    private static volatile AutoDeployTask uniqueTask;

    private final String SHELL_LOCATION;

    /**
     * use Double-Checked Locking to create singleton instance.
     */
    public static AutoDeployTask getInstance(String path, String shellLocation) {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Please initialize path in your configuration files or check your configuration had project absolutely path.");
        }
        if (uniqueTask == null) {
            synchronized (AutoDeployTask.class) {
                if (uniqueTask == null) {
                    uniqueTask = new AutoDeployTask(path, shellLocation);
                }
            }
        }
        return uniqueTask;
    }

    private AutoDeployTask(String path, String shellLocation) {
        if (path == null || "".equals(path)) {
            throw new IllegalArgumentException("Please initialize path in your configuration files or check your configuration had project absolutely path.");
        }
        PATH = path;
        SHELL_LOCATION = shellLocation;
    }

    private static ConcurrentHashMap<String, String> oldFiles = new ConcurrentHashMap<String, String>();

    private static ConcurrentHashMap<String, String> newFiles = new ConcurrentHashMap<String, String>();

    @Override
    public void run() {
        SingleThreadHelper.singleProcess(() -> {
            oldFiles = newFiles;
            newFiles = new ConcurrentHashMap<String, String>();
            Set<String> classesPath = ClassAbsPathUtils.findPackageClassesPath(PATH);
            if (classesPath.isEmpty()) {
                return;
            }
            this.fillInfo(classesPath);
            if (oldFiles.size() == 0 && newFiles.size() == 0) {
                return;
            }
            Boolean checkMD5 = this.checkMD5();
            if (Boolean.FALSE.equals(checkMD5)) {
                executeShell();
            }
        });
    }

    private Boolean checkMD5() {
        if (oldFiles.size() != newFiles.size()) {
            return Boolean.FALSE;
        }
        for (Map.Entry<String, String> entryNew : newFiles.entrySet()) {
            String key = entryNew.getKey();
            String oldMD5 = oldFiles.get(key);
            if (oldMD5 == null) return Boolean.FALSE;
            if (!oldMD5.equals(entryNew.getValue())) return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void fillInfo(Set<String> classesPath) {
        for (String classPath : classesPath) {
            String fileContent = FileContentReader.readFileContent(new File(classPath));
            String md5 = MD5DigestUtils.digestMD5(fileContent);
            if (md5 == null) continue;
            newFiles.put(classPath, md5);
        }
    }

    private void executeShell() {
        System.out.println("start deploy CDK application on AWS.");
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac") || os.contains("linux")) {
                Process process = Runtime.getRuntime().exec("sh " + SHELL_LOCATION);
                printProcess(process);
            }
            System.out.println("deploy CDK application on AWS finished.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printProcess(Process process) throws InterruptedException, IOException {
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        process.waitFor();
        List<String> strList = new ArrayList<>();
        while ((line = input.readLine()) != null) {
            strList.add(line);
        }
        for (String result : strList) {
            System.out.println(result);
        }
    }
}
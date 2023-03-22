package com.example.auto.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassAbsPathUtils {
    public static Set<String> findPackageClassesPath(String pack) {
        Set<String> classesPath = new LinkedHashSet<>();
        String packageDirName = pack.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(pack, filePath, classesPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classesPath;
    }

    private static void findAndAddClassesInPackageByFile(String packageName, String filePath, Set<String> classesPath) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        if (dirfiles == null) return;
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classesPath);
            } else {
                classesPath.add(file.getAbsolutePath());
            }
        }
    }
}

package com.tdeado.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

@Component
public class PackagUtils {

    /**
     * 获得包下面的所有的class
     *
     * @param
     * @return List包含所有class的实例
     */
    private static Map<Integer, Class<?>> aliasesMap;
    private static Map<Integer, Class<?>> enumsMap;
    @Value("${mybatis-plus.type-aliases-package}")
    private String entityPackage;
    @Value("${mybatis-plus.typeEnumsPackage}")
    private String typeEnumsPackage;

//    @PostConstruct
    public void init() {
        List<Class<?>> aliasesMaps = getClasssFromPackage(entityPackage);
        aliasesMap = new HashMap<>();
        for (Class<?> aClass : aliasesMaps) {
            aliasesMap.put(aClass.getSimpleName().hashCode(), aClass);
        }
        List<Class<?>> enumsMaps = getClasssFromPackage(typeEnumsPackage);
        enumsMap = new HashMap<>();
        for (Class<?> aClass : enumsMaps) {
            enumsMap.put(aClass.getSimpleName().hashCode(), aClass);
        }
    }

    public Map<Integer, Class<?>> aliasesMap() {
        if (null == aliasesMap) {
            init();
        }
        return aliasesMap;
    }

    public Map<Integer, Class<?>> enumsMap() {
        if (null == enumsMap) {
            init();
        }
        return enumsMap;
    }
    public static List<Class<?>> getClasssFromPackage(String packageName) {
        List<Class<?>> clazzs = new ArrayList<>();
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            String[] arr = packageDirName.split("\\*");
            dirs = Thread.currentThread().getContextClassLoader().getResources(arr[0]);
            while (dirs.hasMoreElements()) {

                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                File dir = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
                // 在给定的目录下找到所有的文件，并且进行条件过滤
                File[] dirFiles = dir.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                        boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                        return acceptDir || acceptClass;
                    }
                });
                if (null!=dirFiles){
                    for (File dirFile : dirFiles) {
                        findClassInPackageByFile(packageName.replace("*",dirFile.getName()), dirFile.getPath()+arr[1], recursive, clazzs);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazzs;
    }

    /**
     * 在package对应的路径下找到所有的class
     */
    public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive,
                                                List<Class<?>> clazzs) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

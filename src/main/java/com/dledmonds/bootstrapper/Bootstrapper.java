package com.dledmonds.bootstrapper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs the bootstrapping
 * 
 * @author dledmonds
 */
public class Bootstrapper {

    private final Logger log = LoggerFactory.getLogger(Bootstrapper.class);

    Bootstrapper() {

    }

    /**
     * Load all jar files within base directory and run main method
     * 
     * @param baseDir
     * @param mainClassName
     */
    void loadAndRun(Configuration config, LocalConfiguration localConfig) throws MalformedURLException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        File baseDir = new File(config.getLibDir(), "1");
        List<URL> urls = new ArrayList<URL>();
        for (File file : baseDir.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().endsWith("jar")) {
                log.debug("Loading into classpath {}", file.getAbsolutePath());
                urls.add(file.toURI().toURL());
            }
        }

        ClassLoader classloader = new URLClassLoader(urls.toArray(new URL[0]),
                ClassLoader.getSystemClassLoader().getParent());

        Class mainClass = classloader.loadClass(localConfig.getMainClass());
        Method main = mainClass.getMethod("main", new Class[] { String[].class });

        Thread.currentThread().setContextClassLoader(classloader);

        main.invoke(null, new Object[] { new String[] {} });
    }

}

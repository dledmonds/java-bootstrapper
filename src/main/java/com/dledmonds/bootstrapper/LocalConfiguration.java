package com.dledmonds.bootstrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local configuration holding details of currently installed version
 * 
 * @author dledmonds
 */
public class LocalConfiguration {

    private static final String PROP_VERSION = "local.version";
    private static final String PROP_MAIN_CLASS = "local.mainClass";

    private final Logger log = LoggerFactory.getLogger(LocalConfiguration.class);
    
    // saved properties
    Properties props;
    
    // currently installed version
    private int currentVersion;

    // main class to execute
    private String mainClass;

    LocalConfiguration() {
        props = new Properties();
    }

    void load(InputStream in) throws IOException {
        props.load(in);

        String versionStr = props.getProperty(PROP_VERSION, "0");
        setCurrentVersion(Integer.parseInt(versionStr));
        
        setMainClass(props.getProperty(PROP_MAIN_CLASS));
    }

    void update(int version, String mainClass, File file) throws IOException {
        props.setProperty(PROP_VERSION, Integer.toString(version));
        setCurrentVersion(version);
        
        props.setProperty(PROP_MAIN_CLASS, mainClass);
        setMainClass(mainClass);
        
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "");
        }
    }
    
    public int getCurrentVersion() {
        return currentVersion;
    }

    private void setCurrentVersion(int version) {
        this.currentVersion = version;
    }
    
    public String getMainClass() {
        return mainClass;
    }

    private void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
}

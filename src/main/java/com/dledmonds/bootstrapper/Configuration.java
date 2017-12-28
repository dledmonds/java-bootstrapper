package com.dledmonds.bootstrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration holding details of how to update
 * 
 * @author dledmonds
 */
public class Configuration {

    private static final String DEFAULT_CONFIG_FILE = "/config.properties";
    private static final String PROP_BASE_DIR = "baseDir";
    private static final String PROP_VERSION_URL = "versionUrl";

    private final Logger log = LoggerFactory.getLogger(Configuration.class);
    
    // base directory for all file storage and configuration
    private File baseDir;

    // storage area for jars
    private File libDir;

    // url to check for latest version details
    private URL versionCheckUrl;
    
    // file to store local version details
    private File localVersionFile;

    Configuration() {
    }

    void load(InputStream in) throws IOException {
        Properties props = new Properties();

        if (in == null) {
            // load internal configuration
            in = this.getClass().getResourceAsStream(DEFAULT_CONFIG_FILE);
        }
        props.load(in);

        String strBaseDirectory = props.getProperty(PROP_BASE_DIR);
        if (strBaseDirectory == null)
            throw new IllegalArgumentException(PROP_BASE_DIR + " cannot be null");
        baseDir = new File(strBaseDirectory);
        if (!baseDir.exists()) {
            log.info("Base directory does not exist, creating {}", baseDir.getAbsolutePath());
            baseDir.mkdir();
        }
        
        libDir = new File(baseDir, "lib");
        if (!libDir.exists()) {
            log.info("Library directory does not exist, creating {}", libDir.getAbsolutePath());
            libDir.mkdir();
        }

        String strVersionUrl = props.getProperty(PROP_VERSION_URL);
        versionCheckUrl = new URL(strVersionUrl);
        
        localVersionFile = new File(baseDir, "version.properties");
        if (!localVersionFile.exists()) {
            log.info("Local version file does not exist, creating {}", localVersionFile.getAbsolutePath());
            localVersionFile.createNewFile();
        }
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getLibDir() {
        return libDir;
    }

    public File getLocalVersionFile() {
        return localVersionFile;
    }
    
    public URL getVersionCheckUrl() {
        return versionCheckUrl;
    }

}

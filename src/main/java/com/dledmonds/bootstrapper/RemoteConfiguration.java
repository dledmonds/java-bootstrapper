package com.dledmonds.bootstrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote configuration holding details of latest version to install
 * 
 * @author dledmonds
 */
public class RemoteConfiguration {

    private static final String PROP_VERSION = "latest.version";
    private static final String PROP_DOWNLOAD_FILE = "latest.downloadFile";
    private static final String PROP_MAIN_CLASS = "latest.mainClass";

    private final Logger log = LoggerFactory.getLogger(RemoteConfiguration.class);
    
    // latest available version
    private int latestVersion;

    // latest available version
    private URL downloadUrl;
    
    // main class to execute
    private String mainClass;

    RemoteConfiguration() {
    }

    void load(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        
        String versionStr = props.getProperty(PROP_VERSION);
        latestVersion = Integer.parseInt(versionStr);

        String downloadUrlStr = props.getProperty(PROP_DOWNLOAD_FILE);
        downloadUrl = new URL(downloadUrlStr);
        
        mainClass = props.getProperty(PROP_MAIN_CLASS);
    }

    public int getLatestVersion() {
        return latestVersion;
    }

    public URL getDownloadUrl() {
        return downloadUrl;
    }

    public String getMainClass() {
        return mainClass;
    }

}

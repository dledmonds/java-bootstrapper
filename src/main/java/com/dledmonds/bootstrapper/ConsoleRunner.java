package com.dledmonds.bootstrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class
 * 
 * @author dledmonds
 */
public class ConsoleRunner {

    private final static Logger LOG = LoggerFactory.getLogger(ConsoleRunner.class);

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();

        if (args.length > 0) {
            // assume config file as argument
            File configFile = new File(args[0]);
            if (configFile.exists()) {
                try (InputStream in = new FileInputStream(configFile)) {
                    LOG.info("Configuring with file {}", configFile.getAbsolutePath());
                    config.load(in);
                }
            }
        } else {
            config.load(null);
        }

        // load locally installed version information
        LocalConfiguration localConfig = new LocalConfiguration();
        try (InputStream in = new FileInputStream(config.getLocalVersionFile())) {
            LOG.info("Configuring local version with file {}", config.getLocalVersionFile().getAbsolutePath());
            localConfig.load(in);
        }

        // load remote available version information
        RemoteConfiguration remoteConfig = new RemoteConfiguration();
        try (InputStream in = config.getVersionCheckUrl().openStream()) {
            LOG.info("Configuring available version with {}", config.getVersionCheckUrl().toString());
            remoteConfig.load(in);
        }

        LOG.info("Checking for updates");
        boolean versionUpdated = new Updater().update(config, localConfig, remoteConfig);

        if (versionUpdated) {
            // transfer setting to local properties
            localConfig.update(remoteConfig.getLatestVersion(), remoteConfig.getMainClass(),
                    config.getLocalVersionFile());
        }

        LOG.info("Bootstrapping");
        new Bootstrapper().loadAndRun(config, localConfig);
    }

}

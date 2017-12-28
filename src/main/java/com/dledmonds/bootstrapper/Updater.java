package com.dledmonds.bootstrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs version updates
 * 
 * @author dledmonds
 */
public class Updater {

    private final Logger log = LoggerFactory.getLogger(Updater.class);

    boolean update(Configuration config, LocalConfiguration localConfig, RemoteConfiguration remoteConfig)
            throws UpdateException {

        log.info("Installed version is {}", localConfig.getCurrentVersion());
        log.info("Latest version is {}", remoteConfig.getLatestVersion());
        if (remoteConfig.getLatestVersion() > localConfig.getCurrentVersion()) {
            log.info("Starting update ...");
            return downloadAndUnpack(config, remoteConfig);
        }
        
        return false;
    }

    private boolean downloadAndUnpack(Configuration config, RemoteConfiguration remoteConfig) throws UpdateException {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("bootstrapper-", "-download");
        } catch (IOException ioe) {
            throw new UpdateException("Unable to create temporary file for download", ioe);
        }

        FileOutputStream fos = null;
        try {
            log.info("Downloading {} to {}", remoteConfig.getDownloadUrl().toString(), tmpFile.getAbsolutePath());
            ReadableByteChannel rbc = Channels.newChannel(remoteConfig.getDownloadUrl().openStream());
            fos = new FileOutputStream(tmpFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException ioe) {
            throw new UpdateException("Unable to download file from url " + remoteConfig.getDownloadUrl().toString(),
                    ioe);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    log.error("Unable to close file {}", tmpFile.getAbsolutePath());
                }
            }
        }

        File unpackDir = new File(config.getLibDir(), Integer.toString(remoteConfig.getLatestVersion()));
        if (!unpackDir.exists()) {
            log.info("Version directory does not exist, creating {}", unpackDir.getAbsolutePath());
            unpackDir.mkdir();
        }

        try (ZipFile zf = new ZipFile(tmpFile)) {
            Enumeration entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) entries.nextElement();
                if (ze.isDirectory())
                    continue;
                // TODO deal with files inside directories not yet created
                log.info("Unzipping {} from downloaded file", ze.getName());
                try (InputStream in = zf.getInputStream(ze)) {
                    Files.copy(in, new File(unpackDir, ze.getName()).toPath());
                }
            }
        } catch (IOException ioe) {
            throw new UpdateException("Unable to unzip downloaded file", ioe);
        }

        if (tmpFile != null) {
            log.info("Deleting temporary file {}", tmpFile.getAbsolutePath());
            tmpFile.delete();
        }
        
        return true;
    }

}

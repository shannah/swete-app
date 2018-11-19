package com.codename1.onsen;


import com.codename1.io.Log;
import com.codename1.io.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class UpdaterImpl implements com.codename1.onsen.Updater{
    
    public static final String HARNESS_DIR = "src/html/cn1-htmltk-harness";
    
    public static class Version implements Comparable<Version> {
        private int majorVersion;
        private int minorVersion;
        
        public Version(String versionStr) {
            majorVersion = Integer.parseInt(versionStr.substring(0, versionStr.indexOf('.')));
            minorVersion = Integer.parseInt(versionStr.substring(versionStr.indexOf('.')+1));  
        }

        @Override
        public String toString() {
            return majorVersion+"."+minorVersion;
        }
        
        
        
        @Override
        public int compareTo(Version o) {
            if (o == null) {
                return 1;
            }
            if (majorVersion < o.majorVersion) {
                return -1;
            } else if (majorVersion > o.majorVersion) {
                return 1;
            } else {
                if (minorVersion < o.minorVersion) {
                    return -1;
                } else if (minorVersion > o.minorVersion) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        
    }
    public static final Version HARNESS_VERSION=new Version("1.3");
    public static final String HARNESS_URL = "https://github.com/shannah/cn1-htmltk-harness/archive/"+HARNESS_VERSION+".zip";
    
    public static File getHarnessDir() {
        return new File(HARNESS_DIR);
    }
    
    public static File getVersionFile() {
        return new File(getHarnessDir(), "version.txt");
    }
    
    public static Version getFileSystemVersion() {
        File versionFile = getVersionFile();
        if (!versionFile.exists()) {
            return new Version("0.0");
        }
        try (FileInputStream fis = new FileInputStream(versionFile)) {
            return new Version(Util.readToString(fis).trim());
        } catch (IOException ex) {
            return new Version("0.0");
        }
    }
    
    public static boolean requiresUpdate() {
        File f = new File("codenameone_settings.properties");        
        if(!f.exists()) {
            
            return false;
        }
        return HARNESS_VERSION.compareTo(getFileSystemVersion()) > 0;
    }
    
    public static void updateWebHarnessImpl() throws IOException, ZipException {
        if (requiresUpdate()) {
            URL url = new URL(HARNESS_URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setInstanceFollowRedirects(true);
            getHarnessDir().getParentFile().mkdirs();
            File destZip = File.createTempFile("cn1-htmltk-harness", ".zip", getHarnessDir().getParentFile());
            try {
                destZip.deleteOnExit();
                try (InputStream is = conn.getInputStream(); FileOutputStream fos = new FileOutputStream(destZip)) {
                    Util.copy(is, fos);
                }

                File expandedDir = extract(destZip, getHarnessDir().getParentFile());

                if (expandedDir.exists()) {
                    if (getHarnessDir().exists()) {
                        delTree(getHarnessDir());
                    }
                    expandedDir.renameTo(getHarnessDir());
                } else {
                    throw new IOException("Failed to extract harness zip file in update");
                }
            } finally {
                destZip.delete();
            }
            
            
        }
    }
    
    public static boolean delTree(File root) {
        if (root == null) {
            return false;
        }
        if (root.isDirectory()) {
            for (File f : root.listFiles()) {
                if (!delTree(f)) {
                    return false;
                }
            }
        }
        return root.delete();
    }
    
    private static File extract(File zip, File destFolder) throws ZipException, IOException {
        Set<String> fileList = new HashSet<String>();
        fileList.addAll(Arrays.asList(destFolder.list()));
        ZipFile zipFile = new ZipFile(zip);
        // Extracts all files to the path specified
        zipFile.extractAll(destFolder.getAbsolutePath());
        for (String name : destFolder.list()) {
            if (!fileList.contains(name)) {
                return new File(destFolder, name);
            }
        }
        throw new IOException("Extracted zip file but couldn't find the root file extracted");
    }
    
    @Override
    public void updateWebHarness() {
        try {
            updateWebHarnessImpl();
        } catch (Throwable t) {
            Log.e(t);
        }
    }

    public boolean isSupported() {
        return true;
    }

}

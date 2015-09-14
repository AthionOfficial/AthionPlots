package net.athion.athionplots.Utils;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public boolean checkFile(final String path) {
        final File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public boolean checkDir(final String path) {
        final File file = new File(path);
        if (file.isDirectory()) {
            return true;
        }
        return false;
    }

    public void createFile(final String name) {
        final File file = new File(name);
        if (!file.exists() && !file.isDirectory()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void createDir(final String name) {
        final File file = new File(name);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
    }

}

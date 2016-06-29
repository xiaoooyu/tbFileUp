package com.xiaoooyu;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by xiaoooyu on 6/8/16.
 */
public class ZipJob {

    static final int BUFFER = 2048;
    static final String OUTPUT_FILENAME = "archive.zip";

    File invoke(String path) {

        File outputFile = null;
        String outputFileName = OUTPUT_FILENAME;

        try {
            BufferedInputStream origin = null;

            outputFile = new File(path + "/" + outputFileName);
            if (outputFile.exists()) {
                outputFile.delete();
            }

            // get a list of files from current directory
            File f = new File(path);
            String files[] = f.list();

            FileOutputStream dest = new
                    FileOutputStream(outputFile);
            ZipOutputStream out = new ZipOutputStream(new
                    BufferedOutputStream(dest));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];


            for (int i=0; i<files.length; i++) {
                File childFile = new File(path + "/" + files[i]);
                if (!childFile.exists() || childFile.isDirectory() || childFile.isHidden()){
                    continue;
                }

                System.out.println("Adding: " + path + "/" + files[i]);

                FileInputStream fi = new
                        FileInputStream(childFile);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files[i]);

                out.putNextEntry(entry);
                int count;
                while((count = origin.read(data, 0,
                        BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return outputFile;
    }
}

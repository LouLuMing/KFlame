package com.china.fortune.compress;

import com.china.fortune.global.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor {
    static final int BUFFER = 8192;
    private File zipFile;
    private byte data[] = new byte[BUFFER];

    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
    }

    public boolean compress(ArrayList<String> lsPathName) {
        boolean rs = false;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(
                    zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(
                    fileOutputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            for (String srcPathName : lsPathName) {
                File file = new File(srcPathName);
                if (file.exists()) {
                    compress(file, out, basedir);
                } else {
                    Log.logClassError("miss:" + srcPathName);
                }
            }
            out.close();
            rs = true;
        } catch (Exception e) {
            Log.logClassError(e.getMessage());
        }

        return rs;
    }

    public boolean compress(String srcPathName) {
        boolean rs = false;
        File file = new File(srcPathName);
        if (file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(
                        zipFile);
                CheckedOutputStream cos = new CheckedOutputStream(
                        fileOutputStream, new CRC32());
                ZipOutputStream out = new ZipOutputStream(cos);
                String basedir = "";
                compress(file, out, basedir);
                out.close();
                rs = true;
            } catch (Exception e) {
                Log.logClassError(e.getMessage());
            }
        } else {
            Log.logClassError("miss:" + srcPathName);
        }
        return rs;
    }

    private void compress(File file, ZipOutputStream out, String basedir) {
        if (file.isDirectory()) {
            compressDirectory(file, out, basedir);
        } else {
            compressFile(file, out, basedir);
        }
    }

    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                compress(file, out, basedir + dir.getName() + "/");
            }
        }
    }

    private void compressFile(File file, ZipOutputStream out, String basedir) {
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;

            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
    }
}

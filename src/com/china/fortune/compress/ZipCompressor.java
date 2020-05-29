package com.china.fortune.compress;

import com.china.fortune.global.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
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
            WritableByteChannel outChannel = Channels.newChannel(out);
            String basedir = "";
            for (String srcPathName : lsPathName) {
                File file = new File(srcPathName);
                if (file.exists()) {
                    compress(file, out, outChannel, basedir);
                } else {
                    Log.logClassError("miss:" + srcPathName);
                }
            }
            outChannel.close();
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
                WritableByteChannel writableByteChannel = Channels.newChannel(out);
                String basedir = "";
                compress(file, out, writableByteChannel, basedir);
                writableByteChannel.close();
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

    private void compress(File file, ZipOutputStream out, WritableByteChannel outChannel, String basedir) {
        if (file.isDirectory()) {
            compressDirectory(file, out, outChannel, basedir);
        } else {
            compressFile(file, out, outChannel, basedir);
        }
    }

    private void compressDirectory(File dir, ZipOutputStream out, WritableByteChannel outChannel, String basedir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                compress(file, out, outChannel, basedir + dir.getName() + "/");
            }
        }
    }

    private void compressFile(File file, ZipOutputStream out, WritableByteChannel outChannel, String basedir) {
        try {
            FileChannel fileChannel = new FileInputStream(file).getChannel();

            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);

//            WritableByteChannel outChannel = Channels.newChannel(out);
            int iPos = 0;
            long iLen = fileChannel.size();
            do {
                long lTrans = fileChannel.transferTo(iPos, iLen, outChannel);
                if (lTrans > 0) {
                    iLen -= lTrans;
                    if (iLen == 0) {
                        break;
                    }
                    iPos += lTrans;
                } else {
                    break;
                }
            } while (true);
            fileChannel.close();
        } catch (Exception e) {
            Log.logClass(e.getMessage());
        }
    }

//    private void compressFile(File file, ZipOutputStream out, String basedir) {
//        try {
//            BufferedInputStream bis = new BufferedInputStream(
//                    new FileInputStream(file));
//            ZipEntry entry = new ZipEntry(basedir + file.getName());
//            out.putNextEntry(entry);
//            int count;
//
//            while ((count = bis.read(data, 0, BUFFER)) != -1) {
//                out.write(data, 0, count);
//            }
//            bis.close();
//        } catch (Exception e) {
//            Log.logClass(e.getMessage());
//        }
//    }
}

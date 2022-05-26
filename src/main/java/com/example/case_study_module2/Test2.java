package com.example.case_study_module2;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Test2 {
    public static final String OUTPUT_ZIP_FILE = "C:\\Users\\PC\\Desktop\\Test_Zip_Flie_Java\\abc.zip";
    public static final String SOURCE_FOLDER = "C:\\Users\\PC\\Desktop\\Test_Zip_Flie_Java\\product-refactor";
    public static final String PASSWORD = "12345";

    public static void main(String[] args) throws ZipException, IOException {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        if (PASSWORD.length() > 0) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(PASSWORD);
        }

        // Zip files inside a folder
        // An exception will be thrown if the output file already exists
        File outputFile = new File(OUTPUT_ZIP_FILE);
        ZipFile zipFile = new ZipFile(outputFile);

        File sourceFolder = new File(SOURCE_FOLDER);
        List<File> files = listChildFiles(new File(SOURCE_FOLDER));
        for (File file : files) {
            zipFile.createZipFile(file,parameters);
        }

    }
    private static List<File> listChildFiles(File dir) throws IOException {
        List<File> allFiles = new ArrayList<>();

        File[] childFiles = dir.listFiles();
        for (File file : childFiles) {
            if (file.isFile()) {
                allFiles.add(file);
            } else {
                List<File> files = listChildFiles(file);
                allFiles.addAll(files);
            }
        }
        return allFiles;
    }

}

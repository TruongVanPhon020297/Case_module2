package com.example.case_study_module2;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestExtract {
    public static final String ZIP_FILE = "C:\\Users\\PC\\Desktop\\Test_Zip_Flie_Java\\systematic (1).psd.zip";
    public static final String DESTINATION_FOLDER = "C:\\Users\\PC\\Desktop\\Test_Zip_Flie_Java";
    public static final String PASSWORD = "yourPassword";

    public static void main(String[] args) throws ZipException {
        ZipFile zipFile = new ZipFile(ZIP_FILE);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(PASSWORD);
        }
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        for (FileHeader fileHeader : fileHeaders) {
            try {
                zipFile.extractFile(fileHeader,DESTINATION_FOLDER);
            }catch (ZipException ex){
                ex.printStackTrace();
                String result = ex.getMessage().substring(84,98);
                System.out.println(result);
            }
        }
    }
}

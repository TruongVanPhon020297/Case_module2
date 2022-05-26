package com.example.case_study_module2;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class WinrarController {
    private Stage stage;
    @FXML
    private TextArea showArea;
    @FXML
    private TextField showInfo;
    public List<File> files;
    public List<File> files1;
    public List<File> files3;
    @FXML
    private TextField outputLocation;
    @FXML
    private PasswordField showPass;
    doWorkZipFile task;
    doWorkZipFileZip4j task4j;
    doWorkZipFolder taskFolder;
    doWorkExtractFile taskExtract;
    doWorkExtractFileZip4j taskExtractZip4j;
    doWorkZipFolderZip4j taskFolderZip4j;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator proIndi;
    @FXML
    private  Label labelWrong;

    public void openFolder(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(stage);
        showInfo.clear();
        outputLocation.clear();
        showPass.clear();
        files = Arrays.asList(dir);
        printLog(showInfo,files);

    }
    private void printLog(TextField txtfield, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            txtfield.appendText(file.getAbsolutePath() + "\n");
        }
    }

    public void saveOutputLocation(ActionEvent actionEvent) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        File dir = directoryChooser.showDialog(stage);
        files1 = Arrays.asList(dir);
        printLog(outputLocation,files1);
    }

    public void openFile(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        showInfo.clear();
        outputLocation.clear();
        showPass.clear();
        files = fileChooser.showOpenMultipleDialog(stage);
        System.out.println("File3 size : "  + files.size());
        printLog(showInfo, files);
    }

    public void compressAll(ActionEvent actionEvent) throws ZipException, IOException {
        showArea.setText("");
        labelWrong.setText("");
        if (showInfo.getText().equals("")) {
            labelWrong.setText("You need to select file !!!");
            return;
        }
        if (outputLocation.getText().equals("")) {
            labelWrong.setText("Choose folder to save !!!");
            return;
        }
        if (files.size() == 1 && files.get(0).isDirectory()) {
            if (showPass.getText().equals("")){
                taskFolder = new doWorkZipFolder();
                taskFolder.setInputDir(files.get(0));
                File file = new File(outputLocation.getText() + "\\" + files.get(0).getName() + ".zip");
                taskFolder.setOutputZipFile(file);
                taskFolder.messageProperty().addListener((observableValue, s, t1) -> {
                    showArea.appendText( "\n" + t1);
                });
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(taskFolder.progressProperty());
                proIndi.progressProperty().unbind();
                proIndi.progressProperty().bind(taskFolder.progressProperty());
                new Thread(taskFolder).start();

            }else {
                taskFolderZip4j = new doWorkZipFolderZip4j();
                taskFolderZip4j.setInputDir(files.get(0));
                taskFolderZip4j.setPassword(outputLocation.getText());
                taskFolderZip4j.setOutputZipFile(outputLocation.getText() + "\\" + files.get(0).getName() + ".zip");
                taskFolderZip4j.messageProperty().addListener((observableValue, s, t1) -> {
                    System.out.println("update message: " + t1);
                    showArea.appendText( "\n" + t1);
                });
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(taskFolderZip4j.progressProperty());
                proIndi.progressProperty().unbind();
                proIndi.progressProperty().bind(taskFolderZip4j.progressProperty());
                new Thread(taskFolderZip4j).start();
            }

        }else {
            if (showPass.getText().equals("")){
                task = new doWorkZipFile();
                task.setFiles(files);
                task.setOutputFile(outputLocation.getText() + "\\" + files.get(0).getName() + ".zip");
                System.out.println(outputLocation.getText() + "\\" + files.get(0).getName() + ".zip");
                task.messageProperty().addListener((observableValue, s, t1) -> {
                    showArea.appendText( "\n" + t1);
                });
                task.progressProperty().addListener((observableValue, number, t1) -> {
                    System.out.println("t1--->"  +t1 + "number ---> " + number);
                });
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(task.progressProperty());
                proIndi.progressProperty().unbind();
                proIndi.progressProperty().bind(task.progressProperty());
                new Thread(task).start();
            }else {
                String password = showPass.getText();
                String outputFile = outputLocation.getText() + "\\" + files.get(0).getName() + ".zip";
                task4j = new doWorkZipFileZip4j();
                task4j.setFiles(files);
                task4j.setPassword(password);
                task4j.setOutputFile(outputFile);
                task4j.messageProperty().addListener((observableValue, s, t1) -> {
                    showArea.appendText( "\n" + t1);
                });
                progressBar.progressProperty().bind(task4j.progressProperty());
                proIndi.progressProperty().bind(task4j.progressProperty());
                new Thread(task4j).start();
            }
        }
    }
    public void unZipFile(ActionEvent actionEvent) throws ZipException {
        showArea.setText("");
        labelWrong.setText("");
        if (showInfo.getText().equals("")) {
            labelWrong.setText("You need to select file !!!");
            return;
        }
        if (outputLocation.getText().equals("")) {
            labelWrong.setText("Choose folder to save !!!");
            return;
        }
        if (files.size() == 1 && files.get(0).getName().contains(".zip")) {
            ZipFile fileZip = new ZipFile(files.get(0));
            if (!fileZip.isEncrypted()){
                if (!showPass.getText().equals("")) {
                    labelWrong.setText("File without password !!!");
                    return;
                }
                String outputFile = files.get(0).getAbsolutePath();
                taskExtract  = new doWorkExtractFile();
                taskExtract.setFileZip(outputFile);
                taskExtract.setOutputFolder(outputLocation.getText());
                taskExtract.messageProperty().addListener((observableValue, s, t1) -> {
                    showArea.appendText("\n" + t1);
                });
                progressBar.progressProperty().bind(taskExtract.progressProperty());
                proIndi.progressProperty().bind(taskExtract.progressProperty());
                new Thread(taskExtract).start();
            }else {
                if (showPass.getText().equals("")){
                    labelWrong.setText("Enter password to unzip !!!");
                    return;
                }
                String fileZip4j = files.get(0).getAbsolutePath();
                String password = showPass.getText();
                String outputFolder = outputLocation.getText();
                taskExtractZip4j = new doWorkExtractFileZip4j();
                taskExtractZip4j.setFileZip(fileZip4j);
                taskExtractZip4j.setPassword(password);
                taskExtractZip4j.setOutputFolder(outputFolder);
                taskExtractZip4j.messageProperty().addListener((observableValue, s, t1) -> {
                    if (t1.equals("Wrong password")){
                        labelWrong.setText("Wrong password !!!");
                        return;
                    }
                    labelWrong.setText("");
                    showArea.appendText("\n" + t1);
                });
                progressBar.progressProperty().bind(taskExtractZip4j.progressProperty());
                proIndi.progressProperty().bind(taskExtractZip4j.progressProperty());
                new Thread(taskExtractZip4j).start();
            }
        }else {
            if (files.size() != 1) {
                labelWrong.setText("Extract only one file !!!");
                return;
            }
            labelWrong.setText("Not a file zip !!!");
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
    private static void closeStream(OutputStream out) {
        try {
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
class doWorkZipFile extends Task<Void> {
    private  void closeStream(OutputStream out) {
        try {
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    List<File> files;
    String outputFile;
    public void setFiles(List<File> list){
        this.files = list;
    }
    public void setOutputFile(String file){
        this.outputFile = file;
    }

    @Override
    protected Void call(){
        System.out.println("Filesize--->" + files.size());
        FileOutputStream fileOutput = null;
        ZipOutputStream zipOutput = null;
        try{
            fileOutput = new FileOutputStream(outputFile);
            zipOutput = new ZipOutputStream(fileOutput);
            for (int i = 0 ; i < files.size() ; i++) {
                if (isCancelled()) {
                    updateMessage("cancel");
                    break;
                }
                FileInputStream fileInput = new FileInputStream(files.get(i));
                ZipEntry zipEntry = new ZipEntry(files.get(i).getName());
                zipOutput.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fileInput.read(bytes)) >= 0) {
                    zipOutput.write(bytes,0,length);
                }
                this.updateProgress(i + 1,files.size());
                this.updateMessage(files.get(i).getAbsolutePath());
                fileInput.close();
            }
            updateProgress(0,0);
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            closeStream(zipOutput);
            closeStream(fileOutput);
        }
        return null;
    }
}
class doWorkZipFileZip4j extends Task<Void> {
    List<File> files;
    String outputFileZip;
    String password;

    void setFiles(List<File> files) {
        this.files = files;
    }
    void setOutputFile(String outputFile) {
        this.outputFileZip = outputFile;
    }
    void setPassword(String password) {
        this.password = password;
    }
    @Override
    protected Void call() throws Exception {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        if (password.length() > 0) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password);
        }
        File outputFile ;
        ZipFile zipFile ;
        outputFile = new File(outputFileZip);
        zipFile = new ZipFile(outputFile);
        int count = 0;
        for (File file : files) {
            count++;
            updateProgress(count,files.size());
            updateMessage(file.getAbsolutePath());
            zipFile.addFile(file,parameters);
        }
        updateProgress(0,0);
        return null;
    }
}
class doWorkZipFolder extends Task<Void> {
    File outputZipFile;
    File inputDir;

    public void setInputDir(File inputDir) {
        this.inputDir = inputDir;
    }

    public void setOutputZipFile(File outputZipFile) {
        this.outputZipFile = outputZipFile;
    }

    private  void closeStream(OutputStream out) {
        try {
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
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


    @Override
    protected Void call() throws Exception {
        outputZipFile.getParentFile().mkdirs();

        String inputDirPath = inputDir.getAbsolutePath();

        FileOutputStream fos = null;
        ZipOutputStream zipOs = null;
        try {

            List<File> allFiles = listChildFiles(inputDir);

            // Tạo đối tượng ZipOutputStream để ghi file zip.
            fos = new FileOutputStream(outputZipFile);
            zipOs = new ZipOutputStream(fos);
            int count = 0;
            for (File file : allFiles) {
                count++;
                updateProgress(count,allFiles.size());
                updateMessage(file.getAbsolutePath());
                String filePath = file.getAbsolutePath();

                String entryName = filePath.substring(inputDirPath.length() + 1);

                System.out.println("entryName " + entryName);
                ZipEntry ze = new ZipEntry(entryName);
                // Thêm entry vào file zip.
                zipOs.putNextEntry(ze);
                // Đọc dữ liệu của file và ghi vào ZipOutputStream.
                FileInputStream fileIs = new FileInputStream(filePath);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = fileIs.read(bytes)) > 0) {
                    zipOs.write(bytes, 0, len);
                }
                fileIs.close();
            }
            updateProgress(0,0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(zipOs);
            closeStream(fos);
        }

        return null;
    }
}
class doWorkZipFolderZip4j extends Task<Void> {
    String outputZipFile;
    File inputDir;
    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setInputDir(File inputDir) {
        this.inputDir = inputDir;
    }

    public void setOutputZipFile(String outputZipFile) {
        this.outputZipFile = outputZipFile;
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
    @Override
    protected Void call() throws Exception {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        if (password.length() > 0) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password);
        }

        // Zip files inside a folder
        // An exception will be thrown if the output file already exists
        File outputFile = new File(outputZipFile);
        ZipFile zipFile = new ZipFile(outputFile);
        // false - indicates archive should not be split and 0 is split length
        List<File> files1 = listChildFiles(inputDir);
        /*files1.stream().forEach(file -> {
            System.out.println("file name: " + file.getName() + " file path: " + file.getAbsolutePath());
        });*/
        int i = 0;
        for (File file : files1) {
            i++;
            updateProgress(i,files1.size());
            updateMessage(file.getAbsolutePath());
            Thread.sleep(500);
            System.out.println("file name: " + file.getName() + " file path: " + file.getAbsolutePath());
        }
        updateProgress(0,0);
        zipFile.createZipFileFromFolder(inputDir, parameters, false, 0);
        return null;
    }
}
class doWorkExtractFile extends Task<Void> {
    private String fileZip;
    private String outputFolder;


    public void setFileZip(String fileZip) {
        this.fileZip = fileZip;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public int countFileZip() {
        ZipInputStream zis = null;
        int count = 0;
        try {
            // Tạo đối tượng ZipInputStream để đọc file zip.
            zis = new ZipInputStream(new FileInputStream(fileZip));

            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                count++;
            }
            System.out.println("Count--->" + count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zis.close();
            } catch (Exception e) {
            }
        }
        return count;
    }

    @Override
    protected Void call() throws Exception {
        byte[] BUFFER = new byte[1024];
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry entry;
            File file;
            OutputStream os;
            String entryName;
            String outFileName;
            float count = 0;
            int allFile = countFileZip();
            while ((entry = zis.getNextEntry()) != null) {
                count++;
                entryName = entry.getName();
                outFileName = outputFolder + File.separator + entryName;
                System.out.println("Unzip: " + outFileName);
                updateMessage(outFileName);
                updateProgress(count,allFile);
                file = new File(outFileName);
                if (entry.isDirectory()) {
                    // Tạo các thư mục.
                    file.mkdirs();
                } else {
                    // Tạo các thư mục nếu không tồn tại
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    // Tạo một Stream để ghi dữ liệu vào file.
                    os = new FileOutputStream(outFileName);
                    int len;
                    while ((len = zis.read(BUFFER)) > 0) {
                        os.write(BUFFER, 0, len);
                    }
                    os.close();
                }
            }
            updateProgress(0,0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
}
class doWorkExtractFileZip4j extends Task<Void> {
    private String fileZip;
    private String outputFolder;
    private String password;

    public void setFileZip(String fileZip) {
        this.fileZip = fileZip;
    }
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected Void call() throws Exception {
        ZipFile zipFile = new ZipFile(fileZip);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        int i = 0;
        for(FileHeader fileHeader : fileHeaders) {
            try{
                zipFile.extractFile(fileHeader, outputFolder);
            }catch (ZipException ex){
                String result = ex.getMessage().substring(84,98);
                System.out.println(result);
                System.out.println("Thread name: " + Thread.currentThread().getName() + " id: " + Thread.currentThread().getId() );
                updateProgress(0,0);
                updateMessage("Wrong password");
                return null;
            }
            i++;
            System.out.println(fileHeader.getFileName());
            updateProgress(i,fileHeaders.size());
            updateMessage(outputFolder + "\\" + fileHeader.getFileName());
        }
        updateProgress(0,0);
        return null;
    }
}
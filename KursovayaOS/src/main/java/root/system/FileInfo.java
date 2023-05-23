package root.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private String fileName;
    private long lenght;

    public FileInfo(String fileName, long lenght){
        this.fileName = fileName;
        this.lenght = lenght;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLenght() {
        return lenght;
    }

    public void setLenght(long lenght) {
        this.lenght = lenght;
    }

    public  boolean isDirectory(){
        return lenght == -1L;
    }

    public  boolean isUpElement(){
        return lenght == -2L;
    }

    public boolean isSystem(){
        return fileName.toString().equals("system");
    }

    public boolean isTrash(){
        return fileName.toString().equals("trash");
    }


    public FileInfo(Path path){
        try {
            this.fileName = path.getFileName().toString();
            if(Files.isDirectory(path)){
                this.lenght = -1L;
            } else {
                this.lenght = Files.size(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Something wrong with file: "+ path.toAbsolutePath().toString());
        }
    }

}

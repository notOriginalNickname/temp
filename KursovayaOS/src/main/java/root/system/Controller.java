package root.system;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    @FXML
    ListView<FileInfo> filesList;
    @FXML
    TextField pathField;
    Path root;

    Path selectedFile;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        //генерация ячеек
        filesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> fileInfoListView) {
                return new ListCell<FileInfo>(){
                    @Override
                    protected void  updateItem(FileInfo item, boolean empty){
                        super.updateItem(item, empty);
                        if(item==null|| empty){
                            setText(null);
                            setStyle("");
                        } else{
                            String forrmatedFilename = String.format("%-30s", item.getFileName());
                            String forrmatedFileLenght = String.format("%,d bytes", item.getLenght());
                            if(item.getLenght() == -1L){
                                forrmatedFileLenght = String.format("%s", "[ DIR ]");
                            }
                            if(item.getLenght() == -2L){
                                forrmatedFileLenght = "";
                            }

                            String  text = String.format("%s %-20s", forrmatedFilename, forrmatedFileLenght);
                            setText(text);
                        }
                    }
                };
            }
        });
        goToPath(Paths.get("src/main/java/root"));//создаём объект пути через метод, тк path это интерфейс
    }

    //переход в другой каталог
    public void goToPath(Path path){
        root = path;
        pathField.setText(root.toString());
        filesList.getItems().clear();
        if (!root.equals(Paths.get("src/main/java/root"))){
            filesList.getItems().add(new FileInfo("..",-2L));
        }
        filesList.getItems().addAll(scanFiles(path));
        filesList.getItems().sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {

                    if((int)Math.signum(o1.getLenght()) == (int)Math.signum(o2.getLenght())){
                    return o1.getFileName().compareTo(o2.getFileName());
                } return (int) (long) (o1.getLenght() - o2.getLenght());

            }
        });
    }

    public List<FileInfo> scanFiles(Path root){

            try {
            List<FileInfo> out = new ArrayList<>();
            List<Path> pathsInRoot = null;
            pathsInRoot = Files.list(root).collect(Collectors.toList());
            for (Path p : pathsInRoot){
                out.add(new FileInfo(p));
        }
        return out;
        //return Files.list(root).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Files scan exception: " + root);
        }

    }

    public void refresh(){
        goToPath(root);
    }

    public void filesListClicked(MouseEvent mouseEvent) {
     if(mouseEvent.getClickCount() == 2 && mouseEvent.getButton().name().equals(MouseButton.PRIMARY.name())){
         FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
         if(fileInfo != null){
             if(fileInfo.isDirectory()){
                 Path pathTo = root.resolve(fileInfo.getFileName());
                 goToPath(pathTo);
             }
             if (fileInfo.isUpElement() ){
                 Path pathTo = root.getParent();
                 goToPath(pathTo);
             }
         }
     }
     if (mouseEvent.getButton().name().equals(MouseButton.SECONDARY.name())){
         FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
         System.out.println(fileInfo.getFileName());
         if(fileInfo != null){
             ContextMenu contextMenu = new ContextMenu();
             // contextMenu.hide();

             MenuItem openAFile = new MenuItem("open a file");
             MenuItem copyAFile = new MenuItem("copy");
             MenuItem pasteAFile = new MenuItem("paste");
             MenuItem cutAFile = new MenuItem("cut");
             MenuItem deleteAFile = new MenuItem("delete");
             contextMenu.getItems().addAll(openAFile, copyAFile,pasteAFile,cutAFile,deleteAFile);
             filesList.setOnContextMenuRequested(e ->
                     contextMenu.show(filesList, e.getScreenX(), e.getScreenY()));


             copyAFile.setOnAction(e ->{
                    if (fileInfo == null ||fileInfo.isSystem()||fileInfo.isTrash()||fileInfo.isUpElement()){
                        return;
                    }

                        selectedFile = root.resolve(fileInfo.getFileName());

             });

             pasteAFile.setOnAction(e ->{
                 if (selectedFile != null){
                     try {
                         Files.copy(selectedFile,root.resolve(selectedFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                         selectedFile =null;
                         refresh();
                     } catch (IOException ex) {
                         Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно вставить");
                         alert.showAndWait();
                     }
                 } else {
                     Alert alert = new Alert(Alert.AlertType.INFORMATION,"Вы не выбрали файл");
                     alert.showAndWait();
                 }
             });

             deleteAFile.setOnAction(e -> {
                 System.out.println(fileInfo.getFileName());


             });
            // contextMenu.setAutoHide(true);
             if (mouseEvent.getButton().equals(MouseButton.SECONDARY)){

                 contextMenu.setOnHidden(new EventHandler<WindowEvent>() {
                     @Override
                     public void handle(WindowEvent windowEvent) {
                         refresh();
                     }
                 });
                 contextMenu.hide();
                 System.out.println("Hide");
                 contextMenu.setAutoHide(true);
             }

         }


     }
    }
}
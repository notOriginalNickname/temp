module root.kursovayaos {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens root.system to javafx.fxml;
    exports root.system;
}
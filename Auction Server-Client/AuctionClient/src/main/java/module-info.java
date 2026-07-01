module exercise3.auctionclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires AuctionModel;
    requires java.desktop;

    opens exercise3.auctionclient to javafx.fxml;
    exports exercise3.auctionclient;
}
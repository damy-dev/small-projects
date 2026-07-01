package exercise3.auctionclient;

import exercise3.auctionclient.ConnectThread.Connection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.AuctionItem;
import model.Bid;
import model.GameStatus;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import javafx.util.Duration;
import java.util.ResourceBundle;

import static java.lang.String.valueOf;

public class AuctionClientController implements Initializable {
    @FXML
    public TextField tfServerIP;
    @FXML
    public TextField tfPort;
    @FXML
    public TextField tfUserName;
    @FXML
    public Button btConnect;
    @FXML
    public ImageView ivItemExpose;
    @FXML
    public Label lbItemName;
    @FXML
    public Label lbItemDescription;
    @FXML
    public Label lbCurrentPrice;
    @FXML
    public Label lbTimeLeft;
    @FXML
    public TextField tfPlaceBid;
    @FXML
    public Button btPlaceBid;
    @FXML
    public Label lbLead;

    String SERVER_IP = "localhost";
    String PORT = "8443";
    SSLSocket clientSocket;
    Connection connectionClient;
    int timeRemaining;
    Timeline timer  = new Timeline();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfServerIP.setText(SERVER_IP);
        tfPort.setText(PORT);

        btPlaceBid.setDisable(true);
    }

    public void ConnectToServer(ActionEvent actionEvent) {
        System.setProperty("javax.net.ssl.trustStore", "client.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            clientSocket = (SSLSocket) ssf.createSocket(tfServerIP.getText(), Integer.parseInt(tfPort.getText()));

            Bid client = new Bid(-1, tfUserName.getText(), 0, false);
            connectionClient = new Connection(clientSocket, client, this);

            if (connectionClient.isClientConnected()) {
                btConnect.setDisable(true);
                connectionClient.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btMakeBid(ActionEvent actionEvent) throws IOException {
        double bid = Double.parseDouble(tfPlaceBid.getText());
        tfPlaceBid.setText("");
        connectionClient.makeBid(bid);
    }

    public void updateStatus(GameStatus gameStatus) {
        ivItemExpose.setImage(byteArrayToImage(gameStatus.getCurrentItem().getImage()));
        lbItemName.setText(gameStatus.getCurrentItem().getName());
        lbItemDescription.setText(gameStatus.getCurrentItem().getDescription());
        lbCurrentPrice.setText(String.format("%.2f", gameStatus.getCurrentHighestBid()) + "€");
        if(gameStatus.getMessage().contains("winner")) {
            Alert winnerInfo = new Alert(Alert.AlertType.INFORMATION);
            winnerInfo.setTitle("Winner");
            winnerInfo.setHeaderText("Information");
            winnerInfo.setContentText("Item Sold to " + gameStatus.getWinnerName());
            winnerInfo.show();
        } else {
            lbLead.setText(gameStatus.getMessage() + " " + gameStatus.getWinnerName());
            startTimer();
        }
    }

    private void startTimer() {
        if (timer.getStatus() == Animation.Status.STOPPED) {
            timeRemaining = 30;
            lbTimeLeft.setText(timeRemaining + " sec.");

            timer = new Timeline(new KeyFrame(Duration.seconds(1), e ->
            {
                timeRemaining--;
                lbTimeLeft.setText(timeRemaining + " sec.");
            }));

            timer.setCycleCount(30);
            timer.play();
        }
    }

    private Image byteArrayToImage(byte[] bytesImage) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytesImage)) {
            Image image = new Image(bis);
            return image;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
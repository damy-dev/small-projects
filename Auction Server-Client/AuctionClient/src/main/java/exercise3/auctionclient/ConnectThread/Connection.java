package exercise3.auctionclient.ConnectThread;

import exercise3.auctionclient.AuctionClientController;
import javafx.application.Platform;
import model.Bid;
import model.GameStatus;

import javax.net.ssl.SSLSocket;
import java.io.*;

public class Connection extends Thread {

    SSLSocket clientConnection;
    Bid client;
    boolean clientConnected = true;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    AuctionClientController mainWindow;

    public Bid getClient() { return client; }
    public void setClient(Bid client) { this.client = client; }
    public boolean isClientConnected() { return clientConnected; }

    public Connection(SSLSocket socket, Bid client, AuctionClientController mainWindow) {
        this.clientConnection = socket;
        this.client = client;
        this.mainWindow = mainWindow;

        try {
            this.oos = new ObjectOutputStream(clientConnection.getOutputStream());
            this.ois = new ObjectInputStream(clientConnection.getInputStream());
            clientConnected = true;
            oos.writeObject(client);
            oos.flush();
            oos.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (clientConnected) {
            Platform.runLater(() -> mainWindow.lbItemName.setText("Please wait..."));
            do {
                try {
                    Object object = ois.readObject();
                    Platform.runLater(() -> mainWindow.btPlaceBid.setDisable(false));

                    if (object instanceof GameStatus) {
                        GameStatus gameStatus = (GameStatus) object;
                        client.setItemId(gameStatus.getCurrentItem().getId());
                        Platform.runLater(() -> mainWindow.updateStatus(gameStatus));
                    }

                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } while(true);
        }
    }

    public void makeBid(double bid) throws IOException {
        client.setAmount(bid);
        oos.writeObject(client);
        oos.flush();
        oos.reset();
    }
}

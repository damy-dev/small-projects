import model.AuctionItem;
import model.Bid;
import model.GameStatus;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ClientHandler extends Thread {

    SSLSocket service;
    Bid clientBid;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public Bid getClientBid() {
        return clientBid;
    }

    public void setClientBid(Bid clientBid) {
        this.clientBid = clientBid;
    }

    public ClientHandler(SSLSocket socket) throws IOException {
        this.service = socket;
        ois = new ObjectInputStream(service.getInputStream());
        oos = new ObjectOutputStream(service.getOutputStream());
    }

    @Override
    public void run() {
        try {
            clientBid = (Bid) ois.readObject();
            System.out.println(clientBid.getBidderName() + " Joined");

            do {
                clientBid = (Bid) ois.readObject();
                bidHandler();
            } while(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendStatus(GameStatus currentStatus) {
        try {
            oos.writeObject(currentStatus);
            oos.flush();
            oos.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bidHandler(){
        if (clientBid.getAmount() > Main.currentStatus.getCurrentHighestBid()) {
            for(ClientHandler client : Main.connectedClients) {
                client.getClientBid().setWinningBid(false);
            }
            clientBid.setWinningBid(true);
            Main.currentStatus.setMessage("New high bid from");
            Main.currentStatus.setCurrentHighestBid(clientBid.getAmount());
            Main.currentStatus.setWinnerName(clientBid.getBidderName());
            try {
                Main.updateStatus();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

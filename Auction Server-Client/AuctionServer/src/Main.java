import model.AuctionItem;
import model.Bid;
import model.GameStatus;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static CopyOnWriteArrayList<ClientHandler> connectedClients = new CopyOnWriteArrayList<>();
    public static GameStatus currentStatus = null;
    public static List<AuctionItem> auctionItems = loadAuctionItems();
    public static int currentItem = 0;
    static int CLIENTS_MIN = 2;

    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "server.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try(SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(8443)) {

            while(connectedClients.size() < CLIENTS_MIN) {
                System.out.println("Current clients: " + connectedClients.size());
                System.out.println("Waiting clients... At least: " + CLIENTS_MIN);

                SSLSocket socket = (SSLSocket) serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                connectedClients.add(clientHandler);
                clientHandler.start();
            }

            startLoop();

        } catch (IOException e) {
            System.out.println("Server failed");;
        }
    }

    static byte[] imageToByteArray(String path) throws IOException {
        BufferedImage bi = ImageIO.read(new File(path));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos.toByteArray();
    }

    static void updateStatus() throws IOException {
        for(ClientHandler client : connectedClients) {
            client.sendStatus(currentStatus);
        }
    }

    static void startLoop() {
        for(AuctionItem auctionItem : auctionItems) {
            currentStatus = new GameStatus("No Bids!", auctionItem,
                    auctionItem.getStartingPrice(), "");
            try {
                updateStatus();
                Thread.sleep(Duration.ofSeconds(30));

                currentStatus.setMessage("winner");
                updateStatus();

                Thread.sleep(Duration.ofSeconds(5));

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<AuctionItem> loadAuctionItems() {
        List<AuctionItem> listToLoad = new ArrayList<>();

        try {
            listToLoad.add(new AuctionItem(1, "Apple I Computer", "1st Apple's Computer", 1500.0, imageToByteArray("images/apple1.png")));
            listToLoad.add(new AuctionItem(2, "Picasso Sketch", "Painting by Picasso", 23000.0, imageToByteArray("images/art.png")));
            listToLoad.add(new AuctionItem(3, "1st Ed. Harry Potter", "First edition of Harry Potter's book", 2800.0, imageToByteArray("images/book.png")));
            listToLoad.add(new AuctionItem(4, "1967 Shelby GT500", "60' Classical car", 21000.0, imageToByteArray("images/car.png")));
            listToLoad.add(new AuctionItem(5, "Diamond ring", "Real diamond ring", 4500.0, imageToByteArray("images/diamond.png")));
            listToLoad.add(new AuctionItem(6, "Enigma Machine", "Enigma machine", 50000.0, imageToByteArray("images/enigma.png")));
            listToLoad.add(new AuctionItem(7, "T-Rex Tooth", "Authentic T-Rex tooth", 105000.0, imageToByteArray("images/fossil.png")));
            listToLoad.add(new AuctionItem(8, "Vintage Rolex", "A limited edition of Rolex", 9300.0, imageToByteArray("images/rolex.png")));
            listToLoad.add(new AuctionItem(9, "Star Wars Prop", "Movie laser sword", 13000.0, imageToByteArray("images/saber.png")));
            listToLoad.add(new AuctionItem(10, "Ming Dynasty Vase", "Old Ming Dynasty vase", 4700.0, imageToByteArray("images/vase.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return listToLoad;
    }
}
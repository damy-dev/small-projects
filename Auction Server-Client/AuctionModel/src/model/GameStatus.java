package model;

import java.io.Serializable;

public class GameStatus implements Serializable {
    private String message;
    private AuctionItem currentItem;
    private double currentHighestBid;
    private String winnerName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuctionItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(AuctionItem currentItem) {
        this.currentItem = currentItem;
    }

    public double getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(double currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public GameStatus(String message, AuctionItem currentItem, double currentHighestBid, String winnerName) {
        this.message = message;
        this.currentItem = currentItem;
        this.currentHighestBid = currentHighestBid;
        this.winnerName = winnerName;
    }
}

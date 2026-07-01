package model;

import java.io.Serializable;

public class Bid implements Serializable {
    private int itemId;
    private String bidderName;
    private double amount;
    private boolean isWinningBid;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isWinningBid() {
        return isWinningBid;
    }

    public void setWinningBid(boolean winningBid) {
        isWinningBid = winningBid;
    }

    public Bid(int itemId, String bidderName, double amount, boolean isWinningBid) {
        this.itemId = itemId;
        this.bidderName = bidderName;
        this.amount = amount;
        this.isWinningBid = isWinningBid;
    }

    @Override
    public boolean equals(Object obj) {
        Bid ob = (Bid) obj;
        return bidderName.equals(ob.getBidderName());
    }
}

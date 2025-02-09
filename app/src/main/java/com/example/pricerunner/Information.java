package com.example.pricerunner;

public class Information {
    private String image;
    private String title;
    private String price;
    private String seller;

    public Information(String image, String title, String price, String seller) {
        this.image = image;
        this.title = title;
        this.price = price;
        this.seller = seller;
    }
    public String getImageURL() {
        return image;
    }

    public void setImageURL(String imageURL) {
        this.image = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

}

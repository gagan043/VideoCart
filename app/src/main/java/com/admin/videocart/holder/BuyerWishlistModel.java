package com.admin.videocart.holder;

public class BuyerWishlistModel
{
    public BuyerWishlistModel(String name, String color, String emi, String price)
    {
        this.name = name;
        this.color = color;
        this.emi = emi;
        this.price = price;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmi() {
        return emi;
    }

    public void setEmi(String emi) {
        this.emi = emi;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    String name,color,emi,price;
}

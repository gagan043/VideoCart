package com.admin.videocart.holder;

public class BuyerOrderModel
{
    String name,color,progress;

    public BuyerOrderModel(String name, String color, String progress)
    {
        this.name = name;
        this.color = color;
        this.progress = progress;
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

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}

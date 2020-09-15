package org.ichooselifeafrica.mydata.models;

public class Item {
    private String title;
    private int total;


    public Item(String title, int total) {
        this.title = title;
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

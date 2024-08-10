package com.qsa.lock.model;

public class Theme {

    int backGroundImage;

    public Theme(int backGroundImage) {
        this.backGroundImage = backGroundImage;
    }

    public Theme() {

    }

    public int getBackGround() {
        return backGroundImage;
    }

    public void setBackGround(int backGroundImage) {
        this.backGroundImage = backGroundImage;
    }
}

package com.example.smartdispenserv1.admin;

public class NotificationArray {
    boolean fullCap;              //boolean 100-40
    boolean quarterCap;           //boolean 39-1
    boolean emptyCap;              //boolean 0

    public NotificationArray(boolean fullCap, boolean quarterCap, boolean emptyCap) {
        this.fullCap = fullCap;
        this.quarterCap = quarterCap;
        this.emptyCap = emptyCap;
    }

    public NotificationArray(boolean value) {
        this.fullCap = value;
        this.quarterCap = value;
        this.emptyCap = value;
    }

    public boolean isFullCap() {
        return fullCap;
    }

    public void setFullCap(boolean fullCap) {
        this.fullCap = fullCap;
    }

    public boolean isQuarterCap() {
        return quarterCap;
    }

    public void setQuarterCap(boolean quarterCap) {
        this.quarterCap = quarterCap;
    }

    public boolean isEmptyCap() {
        return emptyCap;
    }

    public void setEmptyCap(boolean emptyCap) {
        this.emptyCap = emptyCap;
    }
}

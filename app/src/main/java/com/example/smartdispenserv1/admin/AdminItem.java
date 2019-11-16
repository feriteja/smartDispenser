package com.example.smartdispenserv1.admin;

public class AdminItem {
    public Integer volume;
    public  String name;
    public String key;

    public AdminItem(Integer volume, String name, String key) {
        this.volume = volume;
        this.name = name;
        this.key=key;
    }
    public AdminItem(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

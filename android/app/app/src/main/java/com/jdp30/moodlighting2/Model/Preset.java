package com.jdp30.moodlighting2.Model;

public class Preset {

    private String type,name,id;

    public Preset(){

    }

    public Preset(String type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}

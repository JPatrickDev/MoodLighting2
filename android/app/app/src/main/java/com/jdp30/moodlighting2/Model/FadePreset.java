package com.jdp30.moodlighting2.Model;

import java.util.ArrayList;

public class FadePreset extends Preset{
    private double pauseTime,fadeTime;
    private ArrayList<Integer> colors;

    public FadePreset(){
        super();
    }

    public FadePreset(String name,String id,double pauseTime, double fadeTime, ArrayList<Integer> colors) {
        super("fade",name,id);
        this.pauseTime = pauseTime;
        this.fadeTime = fadeTime;
        this.colors = colors;
    }

    public double getPauseTime() {
        return pauseTime;
    }

    public double getFadeTime() {
        return fadeTime;
    }

    public ArrayList<Integer> getColors() {
        return colors;
    }
}

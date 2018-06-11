package com.jdp30.moodlighting2.Model;

import java.util.ArrayList;

/**
 * Created by jackp on 11/06/2018.
 */

public class Group {
    private String name,groupID;
    private ArrayList<String> clients = new ArrayList<String>();

    public String getName() {
        return name;
    }

    public String getGroupID() {
        return groupID;
    }

    public ArrayList<String> getClients() {
        return clients;
    }
}

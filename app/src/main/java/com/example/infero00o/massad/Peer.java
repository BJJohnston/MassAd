package com.example.infero00o.massad;

/**
 * Created by Brian on 2/19/2018.
 */

public class Peer {
    private String uuid;
    private String name;
    private boolean connected;



    public Peer(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;

    }

    public String getId() {
        return uuid;
    }

    public void setId(String id) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }




}

package com.example.infero00o.massad;

/**
 * Created by Brian on 2/19/2018.
 */

public class Peer {
    private String uuid;
    private String name;
    private boolean connected;



    public Peer(String id, String name) {
        this.id = id;
        this.name = name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String[] getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(String[] connectedTo) {
        this.connectedTo = connectedTo;
    }
}

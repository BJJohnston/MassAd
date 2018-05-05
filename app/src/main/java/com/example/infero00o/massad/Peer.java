package com.example.infero00o.massad;

/**
 * Created by Brian on 2/19/2018.
 */

public class Peer {
    private String uuid;
    private String make;
    private String model;
    private Boolean connected;



    private Boolean outside;
    private Boolean admin;


    public Peer(String uuid, String make, String model) {
        this.uuid = uuid;
        this.make = make;
        this.model = model;

    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Boolean getOutside() {
        return outside;
    }

    public void setOutside(Boolean outside) {
        this.outside = outside;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}

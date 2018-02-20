package com.example.infero00o.massad;

/**
 * Created by Brian on 2/20/2018.
 */

public class connectedPeers<T> {
    private T peers = null;
    private List<connectedPeers> children = null;
    private connectedPeers parent = null;

public connectedPeers(T peers){
    this.peers = peers;
}

public void addChild(connectedPeers child){
    child.setParent(this);
    this.children.add(child);
}



}

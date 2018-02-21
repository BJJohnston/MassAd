package com.example.infero00o.massad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 2/20/2018.
 */

public class connectedPeers<T> {
    private T peers = null;
    private List<connectedPeers> children = new ArrayList<>();
    private connectedPeers parent = null;

    public connectedPeers(T peers) {
        this.peers = peers;
    }

    public void addChild(connectedPeers child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(T peers) {
        connectedPeers<T> newChild = new connectedPeers<>(peers);
        newChild.setParent(this);
        children.add(newChild);

    }

    public void addChildren(List<connectedPeers> children) {
        for (connectedPeers t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<connectedPeers> getChildren() {
        return children;
    }

    public T getPeers() {
        return peers;
    }

    public void setPeers(T peers) {
        this.peers = peers;
    }

    private void setParent(connectedPeers parent) {
        this.parent = parent;
    }

    public connectedPeers getParent() {
        return parent;
    }

    public boolean contains(T peers){

    }

}

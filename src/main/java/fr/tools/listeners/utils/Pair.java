package fr.tools.listeners.utils;

public class Pair<K, V> {
    private K first;
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getKey() {
        return first;
    }

    public void setKey(K first) {
        this.first = first;
    }

    public V getValue() {
        return second;
    }

    public void setValue(V second) {
        this.second = second;
    }
}

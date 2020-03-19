package com.china.fortune.restfulHttpServer;

import java.util.HashMap;

public abstract class ObjectsForThread {
    private HashMap<Class<?>, Object> objsForThread = new HashMap<>();
    private HashMap<Class<?>, Object> objsForRequest = new HashMap<>();

    public abstract void onFreeThread();
    public abstract void onFreeRequest();

    public void addObjectForThread(Object o) {
        objsForThread.put(o.getClass(), o);
    }

    public void addObjectForRequest(Object o) {
        objsForRequest.put(o.getClass(), o);
    }
}

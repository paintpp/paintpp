package com.purkynka.paintpp.event;

import java.util.ArrayList;
import java.util.List;

public class RunnableEvent {
    private List<Runnable> listeners = new ArrayList<>();

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void send() {
        listeners.forEach(Runnable::run);
    }
}

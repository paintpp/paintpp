package com.purkynka.paintpp.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConsumerEvent<T> {
    private List<Consumer<T>> listeners = new ArrayList<>();

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void send(T value) {
        listeners.forEach(l -> l.accept(value));
    }
}

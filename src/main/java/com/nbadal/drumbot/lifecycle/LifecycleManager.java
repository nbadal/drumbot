package com.nbadal.drumbot.lifecycle;

import java.util.ArrayList;
import java.util.List;

public class LifecycleManager implements LifecycleListener {
    private List<LifecycleListener> listeners = new ArrayList<>();

    public void addListener(LifecycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onStart() {
        listeners.forEach(LifecycleListener::onStart);
    }

    @Override
    public void onStop() {
        listeners.forEach(LifecycleListener::onStop);
    }
}

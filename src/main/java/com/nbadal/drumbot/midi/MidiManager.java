package com.nbadal.drumbot.midi;

import com.nbadal.drumbot.lifecycle.LifecycleListener;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MidiManager implements LifecycleListener {

    private MidiDevice selectedDevice;
    private Subject<MidiMessage> midiNotesSubject = PublishSubject.create();

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
        closeSelectedDevice();
    }

    public Observable<MidiDevice> getDevices() {
        return getAllTransmitterDevices();
    }

    public Completable setSelectedDevice(MidiDevice device) {
        return Completable.defer(() -> {
            closeSelectedDevice();
            selectedDevice = device;
            openSelectedDevice();
            return Completable.complete();
        });
    }

    private void openSelectedDevice() throws MidiUnavailableException {
        if (selectedDevice == null || selectedDevice.isOpen()) return;

        selectedDevice.open();
        selectedDevice.getTransmitter().setReceiver(new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                final int command = message.getStatus() & 0xF0;
                if (command == ShortMessage.NOTE_ON) {
                    midiNotesSubject.onNext(message);
                }
            }

            @Override
            public void close() {
            }
        });
    }

    private void closeSelectedDevice() {
        if (selectedDevice == null || !selectedDevice.isOpen()) return;

        selectedDevice.close();
        selectedDevice = null;
    }

    public Observable<? extends MidiMessage> observeMidiNotes() {
        return midiNotesSubject.observeOn(JavaFxScheduler.platform());
    }

    public static Observable<MidiDevice> getAllTransmitterDevices() {
        return Observable.defer(() -> Observable.fromArray(MidiSystem.getMidiDeviceInfo()))
                .map(MidiSystem::getMidiDevice)
                .filter(device -> {
                    try {
                        Transmitter temp = device.getTransmitter();
                        temp.close(); // We know it exists, so close this temporary one
                        return true;
                    } catch (MidiUnavailableException ex) {
                        return false;
                    }
                });
    }

}

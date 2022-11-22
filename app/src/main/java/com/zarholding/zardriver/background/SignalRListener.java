package com.zarholding.zardriver.background;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;


/**
 * Created by m-latifi on 11/11/2022.
 */

public class SignalRListener {

    private static SignalRListener instance;
    private final HubConnection hubConnection;
    private final RemoteSignalREmitter remoteSignalREmitter;
    private Thread thread;

    //---------------------------------------------------------------------------------------------- SignalRListener
    public SignalRListener(RemoteSignalREmitter remoteSignalREmitter, String token) {
        this.remoteSignalREmitter = remoteSignalREmitter;
        hubConnection = HubConnectionBuilder
                .create("http://192.168.50.113:1364/realtimenotification?access_token=" + token)
                .build();
    }
    //---------------------------------------------------------------------------------------------- SignalRListener


    //---------------------------------------------------------------------------------------------- getInstance
    public static SignalRListener getInstance(RemoteSignalREmitter remote, String token) {
        if (instance == null)
            instance = new SignalRListener(remote, token);
        return instance;
    }
    //---------------------------------------------------------------------------------------------- getInstance


    //---------------------------------------------------------------------------------------------- startConnection
    public void startConnection() {
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                        hubConnection
                                .start()
                                .doOnError(throwable -> remoteSignalREmitter.onErrorConnectToSignalR())
                                .doOnComplete(remoteSignalREmitter::onConnectToSignalR)
                                .blockingAwait();

                    hubConnection.onClosed(exception -> {
                        remoteSignalREmitter.onReConnectToSignalR();
                        interruptThread();
                    });
                } catch (Exception ignored) {
                    remoteSignalREmitter.onReConnectToSignalR();
                    interruptThread();
                }
            }
        };
        thread.start();
    }
    //---------------------------------------------------------------------------------------------- startConnection


    //---------------------------------------------------------------------------------------------- stopConnection
    public void stopConnection() {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.stop();
        interruptThread();
    }
    //---------------------------------------------------------------------------------------------- stopConnection


    //---------------------------------------------------------------------------------------------- isConnection
    public boolean isConnection() {
        return hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
    }
    //---------------------------------------------------------------------------------------------- isConnection


    //---------------------------------------------------------------------------------------------- sendToServer
    public void sendToServer(String serviceId, String lat, String lon) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("TrackDriver", serviceId, lat, lon);
    }
    //---------------------------------------------------------------------------------------------- sendToServer


    //---------------------------------------------------------------------------------------------- interruptThread
    public void interruptThread() {
        if (thread != null)
            thread.interrupt();
    }
    //---------------------------------------------------------------------------------------------- interruptThread

    /*        hubConnection.on("ReceiveNewPosition", (receive) -> {
            this.remoteSignalREmitter.onReceiveSignalR("receive : " + receive);
        }, Boolean.class);*/

}

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


    //---------------------------------------------------------------------------------------------- SignalRListener
    public SignalRListener(RemoteSignalREmitter remoteSignalREmitter) {
        this.remoteSignalREmitter = remoteSignalREmitter;
        hubConnection = HubConnectionBuilder.create("http://10.0.2.2:5000/movehub").build();
        hubConnection.on("ReceiveNewPosition", (receive -> {
            this.remoteSignalREmitter.onReceiveSignalR("receive : " + receive);
        }), Boolean.class);
    }
    //---------------------------------------------------------------------------------------------- SignalRListener


    //---------------------------------------------------------------------------------------------- getInstance
    public static SignalRListener getInstance(RemoteSignalREmitter remoteSignalREmitter) {
        if (instance == null)
            instance = new SignalRListener(remoteSignalREmitter);
        return instance;
    }
    //---------------------------------------------------------------------------------------------- getInstance


    //---------------------------------------------------------------------------------------------- startConnection
    public void startConnection() {
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
            hubConnection.start();
    }
    //---------------------------------------------------------------------------------------------- startConnection


    //---------------------------------------------------------------------------------------------- stopConnection
    public void stopConnection() {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.stop();
    }
    //---------------------------------------------------------------------------------------------- stopConnection



    //---------------------------------------------------------------------------------------------- isConnection
    public boolean isConnection() {
        return hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
    }
    //---------------------------------------------------------------------------------------------- isConnection



    //---------------------------------------------------------------------------------------------- sendToServer
    public void sendToServer(float lat, float lon) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("ReceiveNewPosition", lat, lon);
    }
    //---------------------------------------------------------------------------------------------- sendToServer


}

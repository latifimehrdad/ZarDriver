package com.zarholding.zardriver.background;

import android.os.Handler;
import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;


/**
 * Created by m-latifi on 11/11/2022.
 */

public class SignalRListener {

    private static SignalRListener instance;
    private HubConnection hubConnection;
    private final RemoteSignalREmitter remoteSignalREmitter;


    //---------------------------------------------------------------------------------------------- SignalRListener
    public SignalRListener(RemoteSignalREmitter remoteSignalREmitter) {
        this.remoteSignalREmitter = remoteSignalREmitter;
        hubConnection = HubConnectionBuilder.create("http://192.168.50.113:1364/realtimenotification?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIxIiwiVXNlck5hbWUiOiJzdXBlcmFkbWluIiwiUGVyc29ubmVsTnVtYmVyIjoiU3VwZXJBZG1pbiIsIkZ1bGxOYW1lIjoiU3VwZXIgQWRtaW4iLCJSb2xlcyI6IlJlZ2lzdGVyZWRVc2VyIiwibmJmIjoxNjY5MDI0OTM1LCJleHAiOjE2NjkxMTEzMzUsImlhdCI6MTY2OTAyNDkzNX0.Z2Msyx3mpgujVodgcN8-iY-ai3mEq0HLniStUYDHOEU").build();
/*        hubConnection.on("ReceiveNewPosition", (receive) -> {
            this.remoteSignalREmitter.onReceiveSignalR("receive : " + receive);
        }, Boolean.class);*/
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
        Handler handler = new Handler();
        handler.postDelayed(() ->{
            if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                hubConnection
                        .start()
                        .doOnError(throwable -> remoteSignalREmitter.onErrorConnectToSignalR())
                        .doOnComplete(remoteSignalREmitter::onConnectToSignalR)
                        .blockingAwait();
        }, 2000);
        hubConnection.onClosed(exception -> {
            Log.i("meri", "onClosed");
            remoteSignalREmitter.onReConnectToSignalR();
        });
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
    public void sendToServer(String serviceId, String lat, String lon) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("TrackDriver", serviceId, lat, lon);
    }
    //---------------------------------------------------------------------------------------------- sendToServer


}

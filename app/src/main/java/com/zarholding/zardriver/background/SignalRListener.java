package com.zarholding.zardriver.background;

import android.view.View;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

/**
 * Created by m-latifi on 11/11/2022.
 */

public class SignalRListener {

    private static SignalRListener instance;
    private final HubConnection hubConnection;


    //---------------------------------------------------------------------------------------------- SignalRListener
    public SignalRListener() {
        hubConnection = HubConnectionBuilder.create("http://10.0.2.2:5000/movehub").build();
        hubConnection.on("ReceiveNewPosition",(receive -> {
            //receive
        }), Boolean.class);
    }
    //---------------------------------------------------------------------------------------------- SignalRListener



    //---------------------------------------------------------------------------------------------- getInstance
    public static SignalRListener getInstance() {
        if (instance == null)
            instance = new SignalRListener();
        return instance;
    }
    //---------------------------------------------------------------------------------------------- getInstance



    //---------------------------------------------------------------------------------------------- startConnection
    public boolean startConnection(){
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            hubConnection.start();
            return true;
        } else
            return false;
    }
    //---------------------------------------------------------------------------------------------- startConnection



    //---------------------------------------------------------------------------------------------- stopConnection
    public boolean stopConnection(){
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hubConnection.stop();
            return true;
        } else
            return false;
    }
    //---------------------------------------------------------------------------------------------- stopConnection



    //---------------------------------------------------------------------------------------------- sendToServer
    public void sendToServer(float lat, float lon) {
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
            hubConnection.send("ReceiveNewPosition", lat, lon);
    }
    //---------------------------------------------------------------------------------------------- sendToServer


}

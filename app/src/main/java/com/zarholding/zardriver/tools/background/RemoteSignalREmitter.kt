package com.zarholding.zardriver.tools.background

/**
 * Created by m-latifi on 11/21/2022.
 */

interface RemoteSignalREmitter {

    fun onConnectToSignalR()
    fun onErrorConnectToSignalR()
    fun onReConnectToSignalR()

}
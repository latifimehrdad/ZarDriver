package com.zarholding.zardriver.background

/**
 * Created by m-latifi on 11/21/2022.
 */

interface RemoteSignalREmitter {

    fun onReceiveSignalR(message: String)

}
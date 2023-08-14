package com.zarholding.zardriver.view.dialog

import android.app.Dialog
import android.content.Context

/**
 * Created by m-latifi on 7/29/2023.
 */

abstract class ZarDialog(
    context: Context,
    private val onShowDialog: () -> Unit,
    private val onDismissDialog: () -> Unit
): Dialog(context) {
    

    //---------------------------------------------------------------------------------------------- onStart
    override fun onStart() {
        super.onStart()
        onShowDialog.invoke()
    }
    //---------------------------------------------------------------------------------------------- onStart



    //---------------------------------------------------------------------------------------------- onStart
    override fun dismiss() {
        super.dismiss()
        onDismissDialog.invoke()
    }
    //---------------------------------------------------------------------------------------------- onStart


}
package com.zarholding.zardriver.view.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.zarholding.zardriver.R

/**
 * Created by m-latifi on 11/26/2022.
 */

class ConfirmDialog(
    context: Context,
    private val type: ConfirmType,
    private val title: String,
    private val force: Boolean = false,
    private val onYesClick: () -> Unit,
    onShowDialog: () -> Unit,
    onDismissDialog: () -> Unit
) : ZarDialog(context, onShowDialog, onDismissDialog) {


    //---------------------------------------------------------------------------------------------- ConfirmType
    enum class ConfirmType {
        ADD,
        DELETE,
        WARNING
    }
    //---------------------------------------------------------------------------------------------- ConfirmType


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)
        val lp = WindowManager.LayoutParams()
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        this.window?.setGravity(Gravity.CENTER)
        lp.copyFrom(this.window?.attributes)
        lp.horizontalMargin = 50f
        this.window?.attributes = lp
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onStart
    override fun onStart() {
        initDialog()
        super.onStart()
    }
    //---------------------------------------------------------------------------------------------- onStart


    //---------------------------------------------------------------------------------------------- initDialog
    private fun initDialog() {
        val imageViewClose = this.findViewById<ImageView>(R.id.imageViewClose)
        val layoutHeader = this.findViewById<ConstraintLayout>(R.id.constraintLayoutHeader)
        val textViewTitle = this.findViewById<TextView>(R.id.textViewTitle)
        val buttonYes = this.findViewById<MaterialButton>(R.id.buttonYes)
        val buttonNo = this.findViewById<MaterialButton>(R.id.buttonNo)
        this.setCancelable(!force)

        if (force) {
            buttonNo.visibility = View.INVISIBLE
            imageViewClose.visibility = View.INVISIBLE
        } else {
            buttonNo.visibility = View.VISIBLE
            imageViewClose.visibility = View.VISIBLE
        }

        when (type) {
            ConfirmType.ADD -> layoutHeader.setBackgroundResource(R.color.connect_center)
            ConfirmType.DELETE -> layoutHeader.setBackgroundResource(R.color.cardStop)
            ConfirmType.WARNING -> layoutHeader.setBackgroundResource(R.color.buttonLoading)
        }

        textViewTitle.text = title

        buttonYes.setOnClickListener {
            dismiss()
            onYesClick.invoke()
        }

        buttonNo.setOnClickListener { dismiss() }

        imageViewClose.setOnClickListener { dismiss() }
    }
    //---------------------------------------------------------------------------------------------- initDialog

}
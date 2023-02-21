package com.zarholding.zardriver.ext

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.zarholding.zardriver.R

/**
 * Created by m-latifi on 11/9/2022.
 */

//-------------------------------------------------------------------------------------------------- MaterialButton.setShowProgress
@BindingAdapter("showProgress")
fun MaterialButton.showProgress(showProgress: Boolean?) {
    icon = if (showProgress == true) {
        CircularProgressDrawable(context!!).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            setColorSchemeColors(ContextCompat.getColor(context!!, R.color.buttonLoading))
            start()
        }
    } else null
    if (icon != null) { // callback to redraw button icon
        icon.callback = object : Drawable.Callback {
            override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            }

            override fun invalidateDrawable(who: Drawable) {
                this@showProgress.invalidate()
            }

            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            }
        }
    }
}
//-------------------------------------------------------------------------------------------------- MaterialButton.setShowProgress

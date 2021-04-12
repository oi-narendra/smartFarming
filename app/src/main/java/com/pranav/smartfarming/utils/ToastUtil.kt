package com.pranav.smartfarming.utils

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty


fun Context.successToast(message: CharSequence) {
    Toasty.success(this, message).show()
}

fun Context.errorToast(message: CharSequence) {
    Toasty.error(this, message, Toast.LENGTH_LONG).show()
}

fun Context.warningToast(message: CharSequence) {
    Toasty.warning(this, message).show()
}

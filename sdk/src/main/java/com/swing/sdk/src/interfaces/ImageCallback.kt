package com.swing.sdk.src.interfaces

interface ImageCallback {
    fun onSuccess(path: String)

    fun onFailed(msg: String)
}

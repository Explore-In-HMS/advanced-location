
package com.huawei.hms.advancedlocationlibrary.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import com.huawei.hms.advancedlocationlibrary.AdvancedLocation
import com.huawei.hms.advancedlocationlibrary.utils.Constants.LOG_PREFIX

internal class AdvancedLocationInitProvider : ContentProvider() {

    companion object {
        private const val TAG = "${LOG_PREFIX}InitProvider"

        private var context: Context? = null

        @JvmName("setContext1")
        internal fun setContext(context: Context) {
            Companion.context = context
        }

        @JvmName("getContext1")
        internal fun getContext() = context
    }

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate()")
        context?.let {
            AdvancedLocation.init(it)
            setContext(it)
            Log.d(TAG, "onCreate --> LiveLocation initialized.")
        }
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Nothing? = null

    override fun getType(uri: Uri): Nothing? = null

    override fun insert(uri: Uri, values: ContentValues?): Nothing? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
}
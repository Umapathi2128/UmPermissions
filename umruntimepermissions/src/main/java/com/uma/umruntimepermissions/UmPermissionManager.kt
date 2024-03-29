package com.uma.umruntimepermissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Author     : Umapathi
 * Email      : umapathir2@gmail.com
 * Github     :
 * Created on : 26/07/19.
 */
class UmPermissionManager {

    private lateinit var permissionsNeeded: Array<String>
    private lateinit var permissionListener: PermissionListener
    private lateinit var activity: Activity
    private var fragment: Fragment? = null
    private lateinit var context: Context

    private var isFragment: Boolean = false
    private var shouldShowRationaleDialog: Boolean = false
    private var requestCode: Int = 0

    constructor() {
        throw AssertionError("Can't use default constructor. Use PermissionHelper.Builder class to create a new Method of PermissionHelper")
    }

    internal constructor(builder: Builder) {
        this.fragment = builder.fragment
        this.activity = builder.activity!!
        this.requestCode = builder.requestCode
        this.permissionsNeeded = builder.permissionsNeeded
        this.permissionListener = builder.permissionListener!!
        this.context = builder.context!!
        this.isFragment = builder.isFragment
    }

    companion object {

        fun builder(): IWith {
            return Builder()
        }

        fun gotoSettings(activity: Activity) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun request() {
        if (isFragment) {
            fragment?.requestPermissions(permissionsNeeded, requestCode)
        } else {
            ActivityCompat.requestPermissions(activity, permissionsNeeded, requestCode)
        }
    }

    fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(context, permissionsNeeded)) {
                request()
            } else {
                permissionListener.onPermissionsGranted(requestCode, permissionsNeeded.toString())
            }
        } else {
            permissionListener.onPermissionsGranted(requestCode, permissionsNeeded.toString())
        }
    }

    private fun shouldShowRationale(permission: String): Boolean {

        shouldShowRationaleDialog = if (isFragment) {
            fragment?.shouldShowRequestPermissionRationale(permission)!!
        } else {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
        return shouldShowRationaleDialog
    }

    fun getPermissionMessageDialog(find: String): String {
        return PermissionUtil.getPermissionMessageDialog(context, find)
    }

    /**
     * Called by the user when he gets the call in Activity/Fragment
     *
     * @param reqCode      Request Code
     * @param permissions  List of permissions
     * @param grantResults Permission grant result
     */
    fun onRequestPermissionsResult(reqCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == reqCode) {

            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {

                    if (shouldShowRationale(permissions[grantResults.indexOf(grantResult)])) {
                        if (permissionListener.showGrantDialog(permissions[grantResults.indexOf(grantResult)])) {
                            request()
                        }
                    } else {
                        permissionListener.showRationalDialog(
                            requestCode,
                            permissions[grantResults.indexOf(grantResult)]
                        )
                    }
                    return
                }
            }
            permissionListener.onPermissionsGranted(requestCode, permissions.toString())
        }
    }

    /**
     * [Builder] class for [UmPermissionManager].
     * Use only this class to create a new instance of [UmPermissionManager]
     */
    internal class Builder : IWith, IRequestCode, IPermissionListner, INeededPermissions, IBuild {
        internal lateinit var permissionsNeeded: Array<String>
        internal var permissionListener: PermissionListener? = null
        internal var activity: Activity? = null
        internal var fragment: Fragment? = null
        internal var context: Context? = null
        internal var requestCode = -1
        internal var isFragment: Boolean = false

        override fun with(activity: Activity): IRequestCode {
            this.activity = activity
            this.context = activity
            this.fragment = null
            isFragment = false
            return this
        }

        override fun with(fragment: Fragment): IRequestCode {
            this.fragment = fragment
            this.context = fragment.activity
            this.activity = fragment.activity
            isFragment = true
            return this
        }

        override fun requestCode(requestCode: Int): INeededPermissions {
            this.requestCode = requestCode
            return this
        }

        override fun neededPermissions(permissions: Array<String>): IPermissionListner {
            permissionsNeeded = permissions
            return this
        }

        override fun setPermissionListner(permissionListener: PermissionListener): IBuild {
            this.permissionListener = permissionListener
            return this
        }

        override fun build(): UmPermissionManager {
            return when {
                this.permissionListener == null -> throw NullPointerException("Permission listener can not be null")
                this.context == null -> throw NullPointerException("Context can not be null")
                this.permissionsNeeded.isEmpty() -> throw IllegalArgumentException(
                    "Not asking for any permission. At least one permission is expected before calling build()"
                )
                this.requestCode == -1 -> throw IllegalArgumentException("Request code is missing")
                else -> UmPermissionManager(this)
            }
        }
    }

    interface IWith {
        fun with(activity: Activity): IRequestCode

        fun with(fragment: Fragment): IRequestCode
    }

    interface IRequestCode {
        fun requestCode(requestCode: Int): INeededPermissions
    }

    interface INeededPermissions {
        fun neededPermissions(permissions: Array<String>): IPermissionListner
    }

    interface IPermissionListner {
        fun setPermissionListner(permissionListener: PermissionListener): IBuild
    }

    interface IBuild {
        fun build(): UmPermissionManager
    }
}
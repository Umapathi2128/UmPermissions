package com.uma.umruntimepermissions

/**
 * Author     : Umapathi
 * Email      : umapathir2@gmail.com
 * Github     :
 * Created on : 26/07/19.
 */
interface PermissionListener {
    fun onPermissionsGranted(requestCode: Int, acceptedPermission: String)

    //fun onPermissionsDenied(requestCode: Int, deniedPermission: String)

    fun showGrantDialog(grantPermissionTo: String): Boolean = false

    fun showRationalDialog(requestCode: Int, deniedPermission: String): Boolean = true
}
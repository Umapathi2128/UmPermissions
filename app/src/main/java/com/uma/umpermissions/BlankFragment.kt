package com.uma.umpermissions


import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.uma.umruntimepermissions.PermissionListener
import com.uma.umruntimepermissions.UmPermissionManager
import kotlinx.android.synthetic.main.content_main.*

/**
 * Author     : Umapathi
 * Email      : umapathir2@gmail.com
 * Github     :
 * Created on : 26/07/19.
 */

class BlankFragment : Fragment() {

    var permissionManager: UmPermissionManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_single.setOnClickListener { singleCheck() }
        btn_multi.setOnClickListener { multipleCheck() }
    }

    /**
     *  This method for single permission check...
     */
    private fun singleCheck() {
        permissionManager = UmPermissionManager.builder()
            .with(this)
            .requestCode(102)
            .neededPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
            .setPermissionListner(object : PermissionListener {
                override fun onPermissionsGranted(requestCode: Int, acceptedPermission: String) {
                    Toast.makeText(activity, "PERMISSION GRANTED", Toast.LENGTH_LONG).show()
                }

                override fun showGrantDialog(grantPermissionTo: String): Boolean {

                    (activity as FragmentActivity).openAlertDialog(
                        message = permissionManager?.getPermissionMessageDialog(grantPermissionTo).toString(),
                        positiveButtonName = getString(R.string.action_grant),
                        positiveListner = DialogInterface.OnClickListener { _, _ ->
                            permissionManager?.requestPermissions()
                        }
                    )

                    return super.showGrantDialog(grantPermissionTo)
                }

                override fun showRationalDialog(requestCode: Int, deniedPermission: String): Boolean {
                    (activity as FragmentActivity).openAlertDialog(
                        message = permissionManager?.getPermissionMessageDialog(deniedPermission).toString(),
                        positiveButtonName = getString(R.string.action_settings),
                        positiveListner = DialogInterface.OnClickListener { _, _ ->
                            UmPermissionManager.gotoSettings(activity as Activity)
                        }
                    )
                    return super.showRationalDialog(requestCode, deniedPermission)
                }

            })
            .build()

        permissionManager?.requestPermissions()

    }

    /**
     *  This method for multiple permission check...
     */
    private fun multipleCheck() {
        permissionManager = UmPermissionManager.builder()
            .with(this)
            .requestCode(101)
            .neededPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            .setPermissionListner(object : PermissionListener {
                override fun onPermissionsGranted(requestCode: Int, acceptedPermission: String) {
                    Toast.makeText(activity, "REQUEST : GRANTED", Toast.LENGTH_LONG).show()
                }

                override fun showGrantDialog(grantPermissionTo: String): Boolean {

                    (activity as FragmentActivity).openAlertDialog(
                        message = permissionManager?.getPermissionMessageDialog(grantPermissionTo).toString(),
                        positiveButtonName = getString(R.string.action_grant),
                        positiveListner = DialogInterface.OnClickListener { _, _ ->
                            permissionManager?.requestPermissions()
                        }
                    )
                    return super.showGrantDialog(grantPermissionTo)
                }

                override fun showRationalDialog(requestCode: Int, deniedPermission: String): Boolean {
                    (activity as FragmentActivity).openAlertDialog(
                        message = permissionManager?.getPermissionMessageDialog(deniedPermission).toString(),
                        positiveButtonName = getString(R.string.action_settings),
                        positiveListner = DialogInterface.OnClickListener { _, _ ->
                            UmPermissionManager.gotoSettings(activity as Activity)
                        }
                    )
                    return super.showRationalDialog(requestCode, deniedPermission)
                }

            })
            .build()

        permissionManager?.requestPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



}

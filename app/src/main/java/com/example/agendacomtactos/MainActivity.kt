package com.example.agendacomtactos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.drawToBitmap
import java.io.ByteArrayOutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var et_nombre: EditText
    lateinit var et_apellido: EditText
    lateinit var et_phone: EditText
    lateinit var et_mail: EditText

    var MY_PERMISSIONS_REQUEST_CAMARA = 0
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        et_nombre = findViewById<EditText>(R.id.editTextName)
        et_apellido = findViewById<EditText>(R.id.editTextSurname)
        et_phone = findViewById<EditText>(R.id.editTextPhone)
        et_mail = findViewById<EditText>(R.id.editTextMail)

        var datos = ArrayList<ContentValues>()

        var btnFoto = findViewById<Button>(R.id.btnFoto)
        btnFoto.setOnClickListener {
            if (checkPermissions()) {
                takePicture()
                /*val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val image = stream.toByteArray()

                val row = ContentValues().apply {
                    put(ContactsContract.CommonDataKinds.Photo.PHOTO, image)
                    put(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                }
                datos = arrayListOf(row)*/
            } else {
                requestPermissions()
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMARA)
    }

    private fun checkPermissions(): Boolean {
        return (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                imageView.setImageBitmap(bitmap)
            }

        }
    }

    private fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMARA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    takePicture()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }

    private fun getScreenViewBitmap(view: View): Bitmap {
        val specSize = View.MeasureSpec.makeMeasureSpec(
                0 /* any */, View.MeasureSpec.UNSPECIFIED)
        view.measure(specSize, specSize)
        val bitmap = Bitmap.createBitmap(view.measuredWidth,
                view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(canvas)
        return bitmap
    }

    fun guardar(view: View) {
        var nombre = et_nombre.text.toString()
        var apellido = et_apellido.text.toString()
        var phone =  et_phone.text.toString()
        var mail = et_mail.text.toString()

        var intentContacto = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
            type = ContactsContract.Contacts.CONTENT_ITEM_TYPE

            putExtra(ContactsContract.Intents.Insert.NAME, nombre)
            putExtra(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, apellido)
            putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            putExtra(ContactsContract.Intents.Insert.EMAIL, mail)
            putExtra(ContactsContract.CommonDataKinds.Photo.PHOTO, imageView.drawToBitmap())
        }

        startActivity(intentContacto)
    }

}

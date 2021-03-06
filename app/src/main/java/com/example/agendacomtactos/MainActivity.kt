package com.example.agendacomtactos

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


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


        var btnFoto = findViewById<Button>(R.id.btnFoto)
        btnFoto.setOnClickListener {
            if (checkPermissions()) {
                takePicture()
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

    fun guardar(view: View) {
        var nombre = et_nombre.text.toString()
        var apellido = et_apellido.text.toString()
        var phone =  et_phone.text.toString()
        var mail = et_mail.text.toString()

        val bitmap = (imageView.drawable as BitmapDrawable).bitmap


        var intentContacto = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
            type = ContactsContract.Contacts.CONTENT_ITEM_TYPE

            putExtra(ContactsContract.Intents.Insert.NAME, "$nombre $apellido")
            putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            putExtra(ContactsContract.Intents.Insert.EMAIL, mail)
            putExtra(ContactsContract.CommonDataKinds.Photo.PHOTO, bitmap)
        }

        startActivity(intentContacto)
    }

}

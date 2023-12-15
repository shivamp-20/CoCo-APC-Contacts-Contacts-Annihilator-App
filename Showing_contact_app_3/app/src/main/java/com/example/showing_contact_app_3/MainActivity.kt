package com.example.showing_contact_app_3
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(){
    private companion object{
        const val READ_CONTACTS_PERMISSION_REQUEST = 1
        const val COCO_SEARCH_REQUEST = 2
    }
    private lateinit var btnShowContacts: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShowContacts = findViewById(R.id.btnShowCoCoContacts)
        btnShowContacts.setOnClickListener {
            requestContactsPermission()
        }
    }
    private fun requestContactsPermission() {
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_PERMISSION_REQUEST
            )
        }
        else{
            showCoCoContacts()
        }
    }
    private fun showCoCoContacts(){
        val intent = Intent(this, CoCoContactsActivity::class.java)
        startActivityForResult(intent, COCO_SEARCH_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        if(requestCode == READ_CONTACTS_PERMISSION_REQUEST) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showCoCoContacts()
            }
            else{
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
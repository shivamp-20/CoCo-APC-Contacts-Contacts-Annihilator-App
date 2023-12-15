package com.example.showing_contact_app_3
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class CoCoContactsActivity : AppCompatActivity(){
    private lateinit var listViewContacts: ListView
    private lateinit var textViewContactsCount: TextView
    private lateinit var btnDeleteContacts: Button
    private val WRITE_CONTACTS_PERMISSION_REQUEST = 3
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coco_contacts)
        listViewContacts = findViewById(R.id.listViewCoCoContacts)
        textViewContactsCount = findViewById(R.id.textViewCoCoCount)
        btnDeleteContacts = findViewById(R.id.btnDeleteCoCoContacts)
        showCoCoContacts()
        setupDeleteButton()
    }
    private fun setupDeleteButton() {
        btnDeleteContacts.setOnClickListener{
            requestWriteContactsPermission()
        }
    }
    private fun requestWriteContactsPermission(){
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_CONTACTS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
        {
            deleteCoCoContacts()
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_CONTACTS),
                WRITE_CONTACTS_PERMISSION_REQUEST
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == WRITE_CONTACTS_PERMISSION_REQUEST){
            if(grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED){
                deleteCoCoContacts()
            }
        }
    }
    private fun deleteCoCoContacts(){
        val contactsList = mutableListOf<String>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ? OR ${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?",
            arrayOf("%CoCo'23%", "%APC'23%"),
            null
        )
        if(cursor != null && cursor.count > 0){
            while (cursor.moveToNext()){
                val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val deleteUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(
                    ContactsContract.CALLER_IS_SYNCADAPTER,
                    "true"
                ).build()
                contentResolver.delete(
                    deleteUri,
                    "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                    arrayOf(contactId)
                )
            }
            cursor.close()
            showDeletionConfirmationDialog()
        }
    }
    private fun showDeletionConfirmationDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Contacts Deleted")
        builder.setMessage("All displayed contacts have been deleted.")
        builder.setPositiveButton("OK"){ _, _ ->
            finish()
        }
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.custom_dialog_layout, null)
        builder.setView(dialogLayout)
        val dialog = builder.create()
        dialog.show()
    }
    private fun showCoCoContacts(){
        val contactsList = mutableListOf<String>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ? OR ${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?",
            arrayOf("%CoCo'23%", "%APC'23%"),
            null
        )
        if(cursor != null && cursor.count > 0){
            while (cursor.moveToNext()){
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                contactsList.add(contactName)
                val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
                )
                if(phoneCursor != null && phoneCursor.moveToNext()){
                    val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    contactsList.add("Phone: $phoneNumber")
                    contactsList.add("")
                    phoneCursor.close()
                }
            }
            cursor.close()
            textViewContactsCount.text = "The number of contacts with the words CoCo'23 or APC'23 in them are: ${contactsList.size / 3}"
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                contactsList
            )
            listViewContacts.adapter = adapter
        }
    }
}
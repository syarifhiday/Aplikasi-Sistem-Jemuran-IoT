package com.example.sistemjemuran

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.backButton
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        firebaseDatabase = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = firebaseDatabase?.getReference("kontrol")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val getsuhu = snapshot.child("suhu").value.toString()
                val getcahaya = snapshot.child("cahaya").value.toString()

                editSuhu.setText(getsuhu)
                editCahaya.setText(getcahaya)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "onCancelled: ${error.toException()}")
            }
        })



        saveButton.setOnClickListener{
            val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/suhu")
            if(editSuhu.text.length > 0 && editCahaya.text.length > 0){
                var suhu:Int = Integer.parseInt(editSuhu.text.toString())
                database.setValue(suhu).addOnSuccessListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }

                val database2 = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/cahaya")
                var cahaya:Int = Integer.parseInt(editCahaya.text.toString())
                database2.setValue(cahaya).addOnSuccessListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "data is empty", Toast.LENGTH_SHORT).show()

            }

        }

        backButton.setOnClickListener{
            finish()
        }
    }
}
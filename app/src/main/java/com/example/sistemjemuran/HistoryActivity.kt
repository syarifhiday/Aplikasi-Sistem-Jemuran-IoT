package com.example.sistemjemuran

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*
import org.json.JSONArray


class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyList: ArrayList<HistoryModel>
    private lateinit var dbRef: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        deleteButton.setOnClickListener{
            val builder = AlertDialog.Builder(this@HistoryActivity, R.style.AlertDialogCustom)
            builder.setMessage("Anda yakin ingin menghapus semua catatan?")
                .setCancelable(false)
                .setPositiveButton("Ya") { dialog, id ->
                    // Delete selected note from database
                    AndroidNetworking.delete("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app/log_data.json")
                        .addPathParameter("pageNumber", "0")
                        .addQueryParameter("limit", "3")
                        .addHeaders("token", "1234")
                        .setTag("test")
                        .setPriority(Priority.LOW)
                        .build()
                        .getAsJSONArray(object : JSONArrayRequestListener {
                            override fun onResponse(response: JSONArray) {
                                // do anything with response
                            }

                            override fun onError(error: ANError) {
                                // handle error
                            }
                        })
                    finish();
                    startActivity(getIntent());
                }
                .setNegativeButton("Tidak") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }

        historyRecyclerView = findViewById(R.id.listHistory)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.setHasFixedSize(true)

        historyList = arrayListOf<HistoryModel>()

        getLogData()

        backButton.setOnClickListener{
            finish()
        }
    }

    private fun getLogData(){
        dbRef = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("log_data")
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                if(snapshot.exists()){
                    noDataText.isVisible = false
                    deleteButton.isVisible = true
                    for(logSnap in snapshot.children) {
                        val logData = logSnap.getValue(HistoryModel::class.java)
                        historyList.add(logData!!)
                    }

                    historyRecyclerView.adapter = HistoryAdapter(historyList)
                }
                else{
                    noDataText.isVisible = true
                    deleteButton.isVisible = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
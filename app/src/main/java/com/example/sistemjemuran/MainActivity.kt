package com.example.sistemjemuran

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference : DatabaseReference
    private var mode:String? = null
    private var cuacaAPI:String? = null
    private var device_status:String? = null


    val CITY: String = "sleman,id"
    val API: String = "da08f63b6effc7110408b70f7663d543" // Use API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        historyButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }

        settingsButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = firebaseDatabase?.getReference("Sensor")
        weatherTask().execute()
        getData()

        findViewById<Switch>(R.id.modeManual).setOnCheckedChangeListener{
            buttonView, isChecked ->
            if(isChecked){
                val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/mode")
                var Kontrol = "tutup atap"

                database.setValue(Kontrol).addOnSuccessListener {
                    Toast.makeText(this, "Mode Manual", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }

            }else{
                val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/mode")
                var Kontrol = "otomatis"

                database.setValue(Kontrol).addOnSuccessListener {
                    Toast.makeText(this, "Mode Otomatis", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.buka).setOnClickListener {
            val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/mode")
            var Kontrol = "buka atap"

            database.setValue(Kontrol).addOnSuccessListener {
                Toast.makeText(this, "Buka Atap", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
            findViewById<Button>(R.id.buka).setBackgroundResource(R.drawable.rounded_left)
            findViewById<Button>(R.id.buka).setTextColor(getColor(R.color.white))
            findViewById<Button>(R.id.tutup).setBackgroundResource(R.drawable.rounded_stroke_right)
            findViewById<Button>(R.id.tutup).setTextColor(getColor(R.color.colorPrimary))


        }

        findViewById<Button>(R.id.tutup).setOnClickListener {
            val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/mode")
            var Kontrol = "tutup atap"

            database.setValue(Kontrol).addOnSuccessListener {
                Toast.makeText(this, "Tutup Atap", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
            findViewById<Button>(R.id.buka).setBackgroundResource(R.drawable.rounded_stroke_left)
            findViewById<Button>(R.id.buka).setTextColor(getColor(R.color.colorPrimary))

            findViewById<Button>(R.id.tutup).setBackgroundResource(R.drawable.rounded_right)
            findViewById<Button>(R.id.tutup).setTextColor(getColor(R.color.white))


        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.w(TAG, token)

            Log.w(TAG, FirebaseMessaging.getInstance().getToken().toString())
        })

    }

    private fun getData() {
        //update status
        firebaseDatabase = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = firebaseDatabase?.getReference("status")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val atap = snapshot.child("atap").value.toString()

                Handler().postDelayed({
                    if(device_status != snapshot.child("active").value.toString()){
                        findViewById<TextView>(R.id.online_status).text = "Online"
                        findViewById<ImageView>(R.id.online_icon).setImageResource(R.drawable.green_circle)
                    }else{
                        findViewById<TextView>(R.id.online_status).text = "Offline"
                        findViewById<ImageView>(R.id.online_icon).setImageResource(R.drawable.red_circle)

                    }
                }, 8000)


                device_status = snapshot.child("active").value.toString()
                if(atap.equals("terbuka")){
                    findViewById<TextView>(R.id.atap).text = "Atap terbuka"
                    findViewById<ImageView>(R.id.atapImage).setImageResource(R.drawable.bukaatap)
                }else if(atap.equals("tertutup")){
                    findViewById<TextView>(R.id.atap).text = "Atap tertutup"
                    findViewById<ImageView>(R.id.atapImage).setImageResource(R.drawable.tutupatap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "onCancelled: ${error.toException()}")
            }
        })

        //update kontrol atap
        databaseReference = firebaseDatabase?.getReference("kontrol")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kontrol = snapshot.child("/mode").value.toString()
                findViewById<TextView>(R.id.modeKontrol).text = "Kontrol: " + kontrol
                if(kontrol.equals("otomatis")) {
                    mode = "buka atap"
                    findViewById<Switch>(R.id.modeManual).isChecked = false
                    findViewById<Button>(R.id.buka).isVisible = false
                    findViewById<Button>(R.id.tutup).isVisible = false
                }else{
                    findViewById<Switch>(R.id.modeManual).isChecked = true
                    findViewById<Button>(R.id.buka).isVisible = true
                    findViewById<Button>(R.id.tutup).isVisible = true

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "onCancelled: ${error.toException()}")
            }

        })

        //update sensor
            databaseReference = firebaseDatabase?.getReference("Sensor")
            databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val suhu = snapshot.child("/data/suhu").value.toString()
                val humidity = snapshot.child("/data/kelembapan").value.toString()
                val textCuaca = snapshot.child("data/cuaca").value.toString()

                findViewById<TextView>(R.id.temp).text = suhu+"°C"
                findViewById<TextView>(R.id.humidity).text = humidity


                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("HH:mm")
                val current = formatter.format(time)
                Log.d("Cek jam", current)
//                findViewById<TextView>(R.id.updated_at).text =  "Updated at: "+ SimpleDateFormat("yyyy-MM-dd HH:mm").format(time).toString()
                if(current.substring(0,2).toInt() >= 18 || current.substring(0,2).toInt() < 6){
                    Log.d("Sekarang", "Malam")
                    findViewById<TextView>(R.id.status).text = cuacaAPI
                    findViewById<LinearLayout>(R.id.bg_dasar).setBackgroundResource(R.drawable.gradient_bg_night)
                    if (findViewById<TextView>(R.id.status).text.contains("Cloud")) {
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.night_half_moon_partial_cloud)
                    } else if (findViewById<TextView>(R.id.status).text.contains("Clear")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.night_half_moon_clear)
                    } else if (findViewById<TextView>(R.id.status).text.contains("Rain")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.night_half_moon_rain)
                    }
                }else{
                    findViewById<TextView>(R.id.status).text = textCuaca
                    findViewById<LinearLayout>(R.id.bg_dasar).setBackgroundResource(R.drawable.gradient_bg)
                    if (findViewById<TextView>(R.id.status).text.equals("Berawan") || findViewById<TextView>(R.id.status).text.equals("Mendung")) {
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_partial_cloud)
                    } else if (findViewById<TextView>(R.id.status).text.equals("Cerah")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_clear)
                    } else if (findViewById<TextView>(R.id.status).text.equals("Hujan")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_rain)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "onCancelled: ${error.toException()}")
            }
        })

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                cuacaAPI = weather.getString("main")


                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}

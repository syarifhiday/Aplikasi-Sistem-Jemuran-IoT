package com.example.weatherapp

import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
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

    val CITY: String = "sleman,id"
    val API: String = "da08f63b6effc7110408b70f7663d543" // Use API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseDatabase = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseReference = firebaseDatabase?.getReference("Sensor")
        weatherTask().execute()
        getData()

//        findViewById<Button>(R.id.kipas).setOnClickListener {
//            val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/kipas")
//            var Kontrol = kontrolKipas
//
//            database.setValue(Kontrol).addOnSuccessListener {
//                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener {
//                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//            }
//        }

        findViewById<Button>(R.id.kontrol).setOnClickListener {
            val database = FirebaseDatabase.getInstance("https://aplikasi-jemuran-iot-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("kontrol/mode")
            var Kontrol = findViewById<Button>(R.id.kontrol).text.toString()

            database.setValue(Kontrol).addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.w(TAG, token)

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
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
                val kipas = snapshot.child("kipas").value.toString()
                if(atap.equals("terbuka")){
                    findViewById<TextView>(R.id.atap).text = "Atap terbuka"
                    findViewById<ImageView>(R.id.atapImage).setImageResource(R.drawable.bukaatap)
                }else if(atap.equals("tertutup")){
                    findViewById<TextView>(R.id.atap).text = "Atap tertutup"
                    findViewById<ImageView>(R.id.atapImage).setImageResource(R.drawable.tutupatap)
                }
//                if (kipas.equals("Off")){
//                    findViewById<TextView>(R.id.kipasStatus).text = "Off"
//                    kontrolKipas = "Turn on the fan"
//
//
//                }else if(kipas.equals("On")){
//                    findViewById<TextView>(R.id.kipasStatus).text = "On"
//                    kontrolKipas = "Turn off the fan"
//                }
//                findViewById<Button>(R.id.kipas).text = kontrolKipas
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
                if(kontrol.equals("otomatis")){
                    mode = "buka atap"
                    findViewById<Button>(R.id.kontrol).text = "buka atap"
                }else if(kontrol.equals("buka atap")){
                    mode = "tutup atap"
                    findViewById<Button>(R.id.kontrol).text = "tutup atap"
                }else if(kontrol.equals("tutup atap")){
                    mode = "otomatis"
                    findViewById<Button>(R.id.kontrol).text = "otomatis"
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
                findViewById<TextView>(R.id.status).text = textCuaca
                if(textCuaca == "Hujan"){
                    findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_rain)
                }else if (textCuaca == "Berawan" || textCuaca == "Mendung"){
                    findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_partial_cloud)
                }else{
                    findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_clear)
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
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.windText).text = wind.getString("speed")
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))

                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("HH:mm")
                val current = formatter.format(time)
                findViewById<TextView>(R.id.updated_at).text =  "Updated at: "+ SimpleDateFormat("yyyy-MM-dd HH:mm").format(time).toString()
                if(current.substring(0,2).toInt() >= 18 || current.substring(0,2).toInt() < 6){
                    if (weatherDescription.capitalize().equals("Berawan") || weatherDescription.capitalize().equals("Mendung")) {
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.night_half_moon_partial_cloud)
                    } else if (weatherDescription.capitalize().equals("Cerah")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.night_half_moon_clear)
                    } else if (weatherDescription.capitalize().equals("Hujan")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.night_half_moon_rain)
                    }
                }else{
                    if (weatherDescription.capitalize().equals("Berawan") || weatherDescription.capitalize().equals("Mendung")) {
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_partial_cloud)
                    } else if (weatherDescription.capitalize().equals("Cerah")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_clear)
                    } else if (weatherDescription.capitalize().equals("Hujan")){
                        findViewById<ImageView>(R.id.weather_image).setImageResource(R.drawable.day_rain)
                }
                }

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}

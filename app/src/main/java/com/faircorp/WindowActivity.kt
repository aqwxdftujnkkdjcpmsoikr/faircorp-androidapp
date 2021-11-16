package com.faircorp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.faircorp.model.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WindowActivity : BasicActivity() {

    var windowId = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val param = intent.getStringExtra(WINDOW_NAME_PARAM)
        val windowName = findViewById<TextView>(R.id.txt_window_name)
        windowName.text = param

        val id = intent.getLongExtra(WINDOW_NAME_PARAM, 0)
        windowId = id

        //GET WINDOWS
        lifecycleScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices().windowsApiService.findById(id).execute(); }
                .onSuccess {
                    withContext(context = Dispatchers.Main) {

                        val window = it.body();

                        if (window != null) {
                            findViewById<TextView>(R.id.txt_window_name).text = window.name

                            findViewById<TextView>(R.id.txt_window_status).text =
                                window.windowStatus.toString()

                            //GET ROOM from ID
                            lifecycleScope.launch(context = Dispatchers.IO) {

                                runCatching {
                                    ApiServices().roomsApiService.findById(window.roomId).execute()
                                } // (2)
                                    .onSuccess {
                                        withContext(context = Dispatchers.Main) {

                                            val room = it.body()
                                            if (room != null) {
                                                findViewById<TextView>(R.id.txt_window_room).text =
                                                    room.name

                                                findViewById<TextView>(R.id.txt_room_current_temp).text =
                                                    room.currentTemperature?.toString()
                                                findViewById<TextView>(R.id.txt_room_target_temp).text =
                                                    room.targetTemperature?.toString()
                                            }
                                        }
                                    }
                                    .onFailure {
                                        withContext(context = Dispatchers.Main) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Error on windows loading $it",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                        }
                    }


                }
                .onFailure {
                    withContext(context = Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "Error on windows loading $it",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }


    }
    }

package com.example.alonm.todeolho

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.alonm.todeolho.utils.Constant
import kotlinx.android.synthetic.main.activity_add_disorder.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ActivityAddDisorder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_disorder)
        val edit = add_disorder_descricao

        add_disorder_button_save.setOnClickListener {

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val user = prefs.getString("user", "")
            val lat = intent.getDoubleExtra("latitude", 0.0)
            val long = intent.getDoubleExtra("longitude", 0.0)
            val currentTime = Calendar.getInstance().time

            try {
                val queue = Volley.newRequestQueue(this)
                val url = "${Constant().API_URL}denuncia/inserir"

                val body = JSONObject()
                body.put("usuario", user)
                body.put("den_status", "Com Problemas")
                body.put("den_descricao",edit.text.toString())
                body.put("den_anonimato",1)
                body.put("desordem","Prédio, casa ou galpão abandonado")
                body.put("den_datahora_registro","2018-05-21T02:05:06.000Z")
                body.put("den_datahora_ocorreu","2018-05-21T02:05:06.000Z")
                body.put("den_nivel_confiabilidade",3)
                body.put("den_local_latitude",lat.toString())
                body.put("den_local_longitude",long.toString())

                val requestBody = body.toString()

                val requestQueue = Volley.newRequestQueue(this)
                val jsonObj = JsonObjectRequest(Request.Method.POST, url, body,
                        Response.Listener<JSONObject> { response ->
                            Log.d("RESPONSE", response.toString())
                            if("true" == response.getString("sucesso")) {
                                Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, ActivityMap::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Desculpe, ocorreu um erro!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, ActivityMap::class.java)
                                startActivity(intent)
                            }
                        },
                        Response.ErrorListener { error ->
                            Log.d("RESPONSE", error.toString())
                        }
                )
                requestQueue.add(jsonObj)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    fun showTimePickerDialog(v: View) {
        //TimePickerFragment().show(fragmentManager, "timePicker")
    }

    fun showDatePickerDialog(v: View) {
        //DatePickerFragment().show(fragmentManager, "datePicker")
    }

}

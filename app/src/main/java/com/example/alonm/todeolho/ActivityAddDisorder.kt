package com.example.alonm.todeolho

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alonm.todeolho.model.Desordem
import com.example.alonm.todeolho.utils.Constant
import com.koushikdutta.async.future.Future
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_add_disorder.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ActivityAddDisorder : AppCompatActivity() {
    var denunciaDesordeTipo = ""
    var dataOcorreu = Calendar.getInstance()
    val IMAGE_SELECTION_CODE = 1

    var imageUri: Uri? = null
    var path: String? = null
    var fileExtension: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_disorder)
        carregaTiposDesordem()
    }

    fun choseImageToUpload(v: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, IMAGE_SELECTION_CODE)
    }

    fun uploadImage(v: View) {
        val file = File(path)
        val url = "${Constant().API_URL}denuncia/upload/imagem"
        var uploading: Future<com.koushikdutta.ion.Response<String>> = Ion.with(this)
                .load(url)
                .setMultipartFile("image", file)
                .asString()
                .withResponse()
                .setCallback { e, result ->
                    try {
                        var jobj = JSONObject(result.result);
                        Toast.makeText(this, jobj.getString("response"), Toast.LENGTH_SHORT).show();

                    } catch (e: JSONException) {
                        e.printStackTrace();
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null)
            return
        when (requestCode) {
            IMAGE_SELECTION_CODE -> if (resultCode == Activity.RESULT_OK) {
                imageUri = data.data
                path = getPathFromURI(data.data)
                fileExtension = path.toString().substring(path.toString().lastIndexOf(".") + 1)
                Log.d("alonmota", this.path)
                add_disorder_image_input.setImageURI(data.data)
                try {
                    if (fileExtension.equals("img") || fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("gif") || fileExtension.equals("png")) {
                        //FINE
                    } else {
                        throw FileNotFoundException()
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun showTimePickerDialog(v: View) {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minute = c.get(Calendar.MINUTE)


        val dpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            dataOcorreu.set(Calendar.HOUR, hour)
            dataOcorreu.set(Calendar.MINUTE, minute)
            Log.d("alonmota", "${dataOcorreu}")
        }, hour,minute, true)
        dpd.show()
    }

    fun showDatePickerDialog(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dataOcorreu.set(year, month, day)
            Log.d("alonmota", "$year , $monthOfYear , $day")
        }, year, month, day)
        dpd.show()
    }

    fun Boolean.toInt() = if (this) 1 else 0

    private fun carregaTiposDesordem() {
        var arrayDesordens = ArrayList<Desordem>()
        val queue = Volley.newRequestQueue(this)
        val url = "${Constant().API_URL}desordens"

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val result = JSONObject(response.toString())
                    val desordens = result.getJSONArray("desordens")
//                    Toast.makeText(context, denuncias.toString(), Toast.LENGTH_LONG).show()
                    for (i in 0..(desordens.length() - 1)) {
                        val _desordem = desordens.getJSONObject(i)
                        val desordem = Desordem(
                                _desordem.getInt("des_iddesordem"),
                                _desordem.getInt("des_tipo"),
                                _desordem.getString("des_descricao"),
                                _desordem.getInt("org_idorgao")
                        )
                        arrayDesordens.add(desordem)

                    }
                    var adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, arrayDesordens )
                    add_disorder_tipo_desordem_select.adapter = adapter
                    add_disorder_tipo_desordem_select.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            Log.d("alonmota", "nda")
                        }
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            denunciaDesordeTipo = arrayDesordens[p2].descricao
                            Log.d("alonmota", "${arrayDesordens[p2].descricao}")
                        }
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this, "Algo saiu errado, verifique as permissooes e tente novamente!", Toast.LENGTH_SHORT).show()
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun salvarDenuncia(v: View) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val user = prefs.getString("user", "")
        val lat = intent.getDoubleExtra("latitude", 0.0)
        val long = intent.getDoubleExtra("longitude", 0.0)
        val currentTime = Calendar.getInstance().time

        try {
            val queue = Volley.newRequestQueue(this)
            val url = "${Constant().API_URL}denuncia/inserir"
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
            val tz = TimeZone.getTimeZone("UTC")
            df.timeZone = tz;

            val body = JSONObject()
            body.put("usuario", user)
            body.put("den_status", "Com Problemas")
            body.put("den_descricao", add_disorder_descricao.text.toString())
            body.put("den_anonimato", add_disorder_anonima.isChecked.toInt())
            body.put("desordem",denunciaDesordeTipo)
            body.put("den_datahora_registro", df.format(Date()))
            body.put("den_datahora_ocorreu",df.format(dataOcorreu.time))
            Log.d("alonmota", df.format(dataOcorreu.time))
            body.put("den_nivel_confiabilidade",3)
            body.put("den_local_latitude",lat.toString())
            body.put("den_local_longitude",long.toString())

            val requestBody = body.toString()
            Log.d("alonmota", requestBody)

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

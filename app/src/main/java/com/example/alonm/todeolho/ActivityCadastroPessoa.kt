package com.example.alonm.todeolho

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.preference.PreferenceManager.*
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.alonm.todeolho.utils.Constant
import com.koushikdutta.ion.Ion
import kotlinx.android.synthetic.main.activity_add_disorder.*
import kotlinx.android.synthetic.main.activity_cadastro_pessoa.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class ActivityCadastroPessoa : AppCompatActivity() {

    var nascimento = Calendar.getInstance()


    val IMAGE_SELECTION_CODE = 2
    var imageUri: Uri? = null
    var path: String? = ""
    var image: String? = ""
    var fileExtension: String? = null
    var requestSave = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_pessoa)
    }

    fun showDatePickerDialogPessoa(v: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            nascimento.set(year, month, day)
            Log.d("alonmota", "$year , $monthOfYear , $day")
        }, year, month, day)
        dpd.show()
    }

    fun choseImageToUploadPessoa(v: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, IMAGE_SELECTION_CODE)
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
                cadastro_pessoa_foto.setImageURI(data.data)
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


    fun salvarP(v: View) {

        Log.d("request_alon", "ta passando pra salvar1")
/*
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            email.error = getString(R.string.error_invalid_email)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {

            val queue = Volley.newRequestQueue(this)
            val url = "${Constant().API_URL}usuarios"


        }
*/
        if (  path!!.isNotEmpty() ) {
            try {
                val file: File = File(path)
                val url = "${Constant().API_URL}usuario/upload/imagem"
                Ion.with(this)
                        .load("POST", url)
                        .setMultipartParameter("platform", "android")
                        .setMultipartFile("image", "image/jpeg", file)
                        .asJsonObject()
                        .withResponse()
                        .setCallback { e, result ->
                            try {
                                if( result != null ) {
                                    var jobj = result.result
                                    image = jobj["filename"].asString
                                    salvarPessoa(v)
                                }

                            } catch (e: JSONException) {
                                e.printStackTrace();
                            }
                        }
            } catch (e: NullPointerException) {
                salvarPessoa(v)
            }
        } else {
            salvarPessoa(v)
        }

    }

    fun salvarPessoa(v: View) {
        if (!requestSave){
            Log.d("request_alon", "ta passando pra salvar2")
            requestSave = true
            cadastro_pessoa_salvar.isEnabled = false
            val currentTime = Calendar.getInstance().time

            try {
                val queue = Volley.newRequestQueue(this)
                val url = "${Constant().API_URL}usuarios"
                val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
                val tz = TimeZone.getTimeZone("UTC")
                df.timeZone = tz;

                val body = JSONObject()
                body.put("login", cadastro_pessoa_login.text.toString())
                body.put("senha", cadastro_pessoa_senha.text.toString().md5() )
                body.put("email", cadastro_pessoa_email.text.toString())
                body.put("nascimento", df.format(nascimento.time))
                body.put("cpf",cadastro_pessoa_cpf.text.toString())
                body.put("nome", cadastro_pessoa_nome.text.toString())
                body.put("confia",0)
                body.put("tipo",2)
                body.put("telefone",cadastro_pessoa_telefone.text.toString())

                if (image!!.isNotEmpty()) {
                    body.put("img_usuario_id", image)
                }

                val requestQueue = Volley.newRequestQueue(this)
                val jsonObj = JsonObjectRequest(Request.Method.POST, url, body,
                        Response.Listener<JSONObject> { response ->
                            Log.d("RESPONSE_request", response.toString())
                            if(response.getBoolean("sucesso")) {
                                Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Desculpe, ocorreu um erro!", Toast.LENGTH_SHORT).show()
                                finish()
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

    fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digested = md.digest(toByteArray())
        return digested.joinToString("") {
            String.format("%02x", it)
        }
    }
}

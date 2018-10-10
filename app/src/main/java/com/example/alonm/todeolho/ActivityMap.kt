package com.example.alonm.todeolho

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alonm.todeolho.utils.Constant
import kotlinx.android.synthetic.main.activity_map.*
import org.json.JSONObject
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class ActivityMap : AppCompatActivity() {
    companion object {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        checkAndRequestPermissions()
        setUpMap()

    }

    public fun addDenuncia(v: View) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val user = prefs.getString("user", "")
        if (user != null && !user.isEmpty() && !"-".equals(user)) {
            val intent = Intent(this, ActivityAddDisorder::class.java)
            val position = map.mapCenter as GeoPoint
            intent.putExtra("latitude", position.latitude)
            intent.putExtra("longitude", position.longitude)
            startActivity(intent)
        } else {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }
    }

    public fun goBack(v: View) {
        val intent = Intent(this, ActivityToDeOlho::class.java)
        startActivity(intent)
    }



     // Verifica e solicita permissoes necessarias para abri o mapa
    private fun checkAndRequestPermissions(): Boolean {

        val listPermissionsNeeded = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Internet não permitida, o aplicativo pode nao funcionar como esperado", Toast.LENGTH_LONG).show()
                }

                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Acesso a armazenamento não permitido" +
                            ", o aplicativo pode nao funcionar como esperado", Toast.LENGTH_LONG).show()
                }

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "GPS não permitido, o aplicativo pode nao funcionar como esperado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun setUpMap() {
       // Aqui definimos qual vai ser o mapa usado. No caso MAPNIK
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Aqui sao colocados os botoes de zoom
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        // Aqui e definido o ponto central do mapa usando o controler
        val mapController = map.controller
        mapController.setZoom(15.5)
        val startPoint = GeoPoint(-15.7801,  -47.9292)
        mapController.setCenter(startPoint)
        map.invalidate()

        carregaDenuncias()
    }


    private fun carregaDenuncias() {
        val queue = Volley.newRequestQueue(this)
        val url = "${Constant().API_URL}denuncias/coords"

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val result = JSONObject(response.toString())
                    val denuncias = result.getJSONArray("denuncia")
                    for (i in 0..(denuncias.length() - 1)) {
                        val denuncia = denuncias.getJSONObject(i)
                        val startMarker = Marker(map)
                        startMarker.position = GeoPoint(denuncia.getDouble("st_x"), denuncia.getDouble("st_y"))
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        map.overlays.add(startMarker)

                    }
                    map.invalidate()

                },
                Response.ErrorListener {
                    Toast.makeText(this, "Algo saiu errado, verifique as permissooes e tente novamente!", Toast.LENGTH_SHORT).show()
                })

        queue.add(stringRequest)
    }
}

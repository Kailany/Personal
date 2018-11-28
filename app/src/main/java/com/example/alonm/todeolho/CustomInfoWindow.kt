package com.example.alonm.todeolho

import android.view.View
import com.example.alonm.todeolho.model.Denuncia
import com.example.alonm.todeolho.utils.Constant
import com.squareup.picasso.Picasso
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import kotlinx.android.synthetic.main.custom_info_window.*

class CustomInfoWindow(mapView: MapView, private val den: Denuncia) : MarkerInfoWindow(R.layout.custom_info_window, mapView) {
    override fun onOpen(item: Any?) {
        super.onOpen(item)
    }
}
package com.example.alonm.todeolho


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.alonm.todeolho.model.Denuncia
import com.example.alonm.todeolho.utils.Constant
import kotlinx.android.synthetic.main.fragment_lista_desordens.*
import org.json.JSONObject


class FragmentListaDenuncias : Fragment() {

    var rv: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_lista_desordens, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = disorder_recicler_view
        rv?.layoutManager = LinearLayoutManager(view.context)
        rv?.adapter = AdapterListaDenuncias(listOf(), view.context)

        recuperaListaDesordens(view)

    }

    fun recuperaListaDesordens(view: View) {
        var arrayDisordem: ArrayList<Denuncia> = ArrayList()
        val queue = Volley.newRequestQueue(view.context)
        val url = "${Constant().API_URL}denuncias"

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val result = JSONObject(response.toString())
                    Log.d("Alon", result.toString())
                    val denuncias = result.getJSONArray("denuncias")
                    //Toast.makeText(context, denuncias.toString(), Toast.LENGTH_LONG).show()
                    for (i in 0..(denuncias.length() - 1)) {
                        val denuncia = denuncias.getJSONObject(i)
                        var denunciaTO = Denuncia(
                                descricao = denuncia.getString("den_descricao")?: "nao consta",
                                anonima = denuncia.getInt("den_anonimato")?: 1,
                                status = denuncia.getString("den_status")?: "nao consta",
                                desordem = "Prédio, casa ou galpão abandonado",
                                local = "local"
                        )
                        arrayDisordem.add(denunciaTO)

                    }
                    rv?.adapter = AdapterListaDenuncias(arrayDisordem, view.context)
                },
                Response.ErrorListener {
                    Toast.makeText(view.context, "Algo saiu errado, verifique as permissooes e tente novamente!", Toast.LENGTH_SHORT).show()
                })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }



}

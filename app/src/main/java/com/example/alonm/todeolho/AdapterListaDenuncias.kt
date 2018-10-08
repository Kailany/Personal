package com.example.alonm.todeolho

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alonm.todeolho.model.Denuncia
import kotlinx.android.synthetic.main.item_disorder.view.*

class AdapterListaDenuncias(
        private val desordens: List<Denuncia>,
        private val context: Context
): RecyclerView.Adapter<AdapterListaDenuncias.Holder>() {

    //Holder para cada item
    class Holder(item: View): RecyclerView.ViewHolder(item) {
        val desordem = item.rv_disorder_desordem
        val descricao = item.rv_disorder_descricao
//        val status = item.rv_disorder_status
//        val local = item.rv_disorder_local

        fun setValues(_den: Denuncia) {
            this.desordem.text = _den.desordem
            this.descricao.text = _den.descricao
//            this.status.text = _den.status
//            this.local.text = _den.local
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_disorder, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return desordens.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val desordem = desordens[position]
        holder.setValues(desordem)
    }
}
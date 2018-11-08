package com.example.alonm.todeolho

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alonm.todeolho.model.Denuncia
import com.example.alonm.todeolho.utils.Constant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_disorder.view.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity



class AdapterListaDenuncias(
        private val desordens: List<Denuncia>,
        private val context: Context
): RecyclerView.Adapter<AdapterListaDenuncias.Holder>() {

    //Holder para cada item
    inner class Holder(item: View): RecyclerView.ViewHolder(item) {
        val desordem = item.rv_disorder_desordem
        val descricao = item.rv_disorder_descricao
        val imagem = item.rv_disorder_image
        val btnLike = item.rv_disorder_validar
        val btnDslike = item.rv_disorder_invalidar

        init {
            item.setOnClickListener {
                var pos = adapterPosition
                if (pos !== RecyclerView.NO_POSITION) {
                    val clickedItem = desordens[pos]
                    val intent = Intent(it.context, ActivityDetalheDenuncia::class.java)
                    intent.putExtra("denuncia", clickedItem)
                    this@AdapterListaDenuncias.context.startActivity(intent)
                }
            }
            btnLike.setOnClickListener {
                this@AdapterListaDenuncias.likeDenuncia()
            }
            btnDslike.setOnClickListener{
                this@AdapterListaDenuncias.dslikeDenuncia()
            }
        }
//        val status = item.rv_disorder_status
//        val local = item.rv_disorder_local

        fun setValues(_den: Denuncia) {
            this.desordem.text = _den.des_descricao
            this.descricao.text = _den.den_descricao

                Log.d("alonmota", "${Constant().API_URL}denuncia/uploads/${_den.img_idarquivo.toString()}")
                val url = "${Constant().API_URL}denuncia/uploads/${_den.img_idarquivo.toString()}"
                Picasso.get()
                        .load(url)
                        .resize(150, 150)
                        .centerCrop()
                        .placeholder(R.drawable.ic_local_see_black_24dp)
                        .error(R.drawable.ic_local_see_black_24dp)
                        .into(imagem)
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

    fun likeDenuncia( ) {

    }

    fun dslikeDenuncia( ) {

    }

}
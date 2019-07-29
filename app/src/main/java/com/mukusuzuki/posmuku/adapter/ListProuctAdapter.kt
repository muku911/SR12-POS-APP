package com.mukusuzuki.posmuku.adapter

import android.content.Context
import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException
import com.mukusuzuki.posmuku.R
import com.mukusuzuki.posmuku.data.*
import kotlinx.android.synthetic.main.adapter_list_barcode.view.*
import kotlinx.android.synthetic.main.adapter_list_item_transaction.view.*
import kotlinx.android.synthetic.main.adapter_list_pelanggan.view.*
import kotlinx.android.synthetic.main.adapter_list_product.view.*
import kotlinx.android.synthetic.main.adapter_list_transaction.view.*


class ListProductAdapter(private val context: Context, private val items: MutableList<_ListProduct>, private val listener: (_ListProduct) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ListProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list_product, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {


        fun bindItem(items: _ListProduct, listener: (_ListProduct) -> Unit) {
            itemView.tvItemNumber.text = items.id.toString()
            itemView.tvItemCount.text = items.item_count.toString()
            itemView.tvItemName.text = items.item_name
            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}

class ListCodeAdapter(private val context: Context, private val items: MutableList<_Product>, private val listener: (_Product) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ListCodeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list_barcode, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {


        fun bindItem(items: _Product, listener: (_Product) -> Unit) {
            val qrgEncoder = QRGEncoder(items.item_code, null, QRGContents.Type.TEXT, 60)
            val bitmap: Bitmap

            itemView.tvNameItem.text = items.item_name

            try {
                bitmap = qrgEncoder.encodeAsBitmap()
                itemView.imgview_item.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                println(e.toString())
            }


            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}

class ListPelangganAdapter(private val context: Context, private val items: MutableList<_Pelanggan>, private val listener: (_Pelanggan) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ListPelangganAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list_pelanggan, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {


        fun bindItem(items: _Pelanggan, listener: (_Pelanggan) -> Unit) {

            itemView.tvNumber.text   = items.no.toString()
            itemView.tvName.text = items.name

            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}

class ListTransactionAdapter(private val context: Context, private val items: MutableList<_ListTransaction>, private val listener: (_ListTransaction) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ListTransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list_transaction, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {


        fun bindItem(items: _ListTransaction, listener: (_ListTransaction) -> Unit) {

            itemView.tvNumber1.text   = items.no.toString()
            itemView.tvName1.text     = items.nama_pelanggan
            itemView.tvDate1.text     = items.tanggal

            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}

class ListItemAdapter(private val context: Context, private val items: MutableList<_DetailTransaction>, private val listener: (_DetailTransaction) -> Unit)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_list_item_transaction, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {


        fun bindItem(items: _DetailTransaction, listener: (_DetailTransaction) -> Unit) {

            itemView.tvDTItemNumber.text   = items.no.toString()
            itemView.tvDTItemName.text     = items.name_item
            itemView.tvDTItemCount.text    = items.sum_item.toString()

            itemView.setOnClickListener {
                listener(items)
            }
        }
    }
}

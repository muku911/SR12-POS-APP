package com.mukusuzuki.posmuku.fg_pelanggan

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mukusuzuki.posmuku.InputPelanggan
import com.mukusuzuki.posmuku.R
import com.mukusuzuki.posmuku.adapter.ListPelangganAdapter
import com.mukusuzuki.posmuku.data._Pelanggan
import com.mukusuzuki.posmuku.database.Pelanggan
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.fragment_list_pelanggan.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh

class ListPelanggan : Fragment() {

    private var pelangganList: MutableList<_Pelanggan> = mutableListOf()
    private lateinit var adapter: ListPelangganAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        pelangganList.clear()


        adapter = ListPelangganAdapter(activity!!.baseContext, pelangganList){
            ctx.startActivity<InputPelanggan>("id" to it.id)
        }

        rvPelanggan.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(activity!!.baseContext)
        rvPelanggan.adapter = adapter

        showPelangganList()
        refresh.onRefresh {
            pelangganList.clear()
            showPelangganList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_pelanggan, container, false)
    }

    override fun onResume() {
        super.onResume()
        pelangganList.clear()
        showPelangganList()
    }

    private fun showPelangganList(){
        context?.database?.use {
            val result  = select(Pelanggan.TABEL_NAME)
            val getdata = result.parseList(classParser<Pelanggan>())
            var a = 0
            while (a < getdata.count()){
                val data = _Pelanggan(a+1, getdata[a].id, getdata[a].name, getdata[a].provinsi,
                    getdata[a].kota, getdata[a].kecamatan, getdata[a].address, getdata[a].phone)
                pelangganList.add(data)
                a++
            }

        }
        adapter.notifyDataSetChanged()
        if (pelangganList.count() == 0){
            rvPelanggan.visibility = View.GONE
            textViewNoData.visibility = View.VISIBLE
        }else{
            textViewNoData.visibility = View.GONE
            rvPelanggan.visibility = View.VISIBLE
        }
        refresh.isRefreshing = false
    }
}

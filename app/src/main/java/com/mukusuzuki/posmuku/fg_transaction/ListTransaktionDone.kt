package com.mukusuzuki.posmuku.fg_transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mukusuzuki.posmuku.R
import com.mukusuzuki.posmuku.adapter.ListTransactionAdapter
import com.mukusuzuki.posmuku.data._ListTransaction
import com.mukusuzuki.posmuku.database.Pelanggan
import com.mukusuzuki.posmuku.database.Transaction
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.fragment_list_transaction.*
import kotlinx.android.synthetic.main.fragment_list_transaktion_done.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh

class ListTransaktionDone : Fragment() {
    private var transactionList: MutableList<_ListTransaction> = mutableListOf()
    private lateinit var adapter: ListTransactionAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        transactionList.clear()


        adapter = ListTransactionAdapter(activity!!.baseContext, transactionList){
            ctx.startActivity<com.mukusuzuki.posmuku.Transaction>("id" to it.id, "id_pelanggan" to it.id_pelanggan,
                "date" to it.tanggal)
        }

        rvTransaksiDone.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(activity!!.baseContext)
        rvTransaksiDone.adapter = adapter

        showPelangganList()
        refresh2.onRefresh {
            transactionList.clear()
            showPelangganList()
        }
    }

    private fun showPelangganList() {
        context?.database?.use {
            val result  = select(Transaction.TABEL_NAME).whereArgs("(${Transaction.STATUS} = {status})", "status" to 1)
            val getdata = result.parseList(classParser<Transaction>())
            val result2  = select(Pelanggan.TABEL_NAME)
            val getdata2 = result2.parseList(classParser<Pelanggan>())

            var a = 0
            var b = 0
            var name: String
            while (a < getdata.count()){
                while (b < getdata2.count()){
                    if(getdata[a].id_pelanggan == getdata2[b].id){
                        name = getdata2[b].name.toString()
                        val data = _ListTransaction(a+1, getdata[a].id, getdata[a].id_pelanggan, name, getdata[a].tanggal)
                        transactionList.add(data)
                    }
                    b++
                }
                a++
                b = 0
            }

        }
        adapter.notifyDataSetChanged()
        if (transactionList.count() == 0){
            rvTransaksiDone.visibility = View.GONE
            textViewNoData2.visibility = View.VISIBLE
        }else{
            textViewNoData2.visibility = View.GONE
            rvTransaksiDone.visibility = View.VISIBLE
        }
        refresh2.isRefreshing = false


    }

    override fun onResume() {
        super.onResume()
        transactionList.clear()
        showPelangganList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_transaktion_done, container, false)
    }
}

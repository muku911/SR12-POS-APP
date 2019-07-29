package com.mukusuzuki.posmuku.fg_barcode

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mukusuzuki.posmuku.R
import com.mukusuzuki.posmuku.adapter.ListCodeAdapter
import com.mukusuzuki.posmuku.data._Product
import com.mukusuzuki.posmuku.database.Product
import com.mukusuzuki.posmuku.database.database
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class ListQRCode : Fragment(), AnkoComponent<Context> {
    private var barcodeList: MutableList<_Product> = mutableListOf()
    private lateinit var adapter: ListCodeAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var listBarcode: androidx.recyclerview.widget.RecyclerView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        barcodeList.clear()

        adapter = ListCodeAdapter(activity!!.baseContext, barcodeList){

        }

        listBarcode.adapter = adapter
        showList()
        swipeRefresh.onRefresh {
            barcodeList.clear()
            showList()
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeList.clear()
        showList()
    }

    private fun showList() {
        context?.database?.use {
            val result  = select(Product.TABEL_NAME)
            val getData = result.parseList(classParser<_Product>())

            barcodeList.addAll(getData)
            adapter.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createView(AnkoContext.create(ctx))
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        verticalLayout {
            lparams (width = matchParent, height = wrapContent)
            topPadding = dip(2)
            leftPadding = dip(2)
            rightPadding = dip(2)

            swipeRefresh = swipeRefreshLayout {
                setColorSchemeResources(
                    R.color.colorTextSplash,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light)

                verticalLayout {
                    listBarcode = recyclerView {
                        lparams (width = matchParent, height = wrapContent)
                        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
                    }
                }
            }
        }
    }







}
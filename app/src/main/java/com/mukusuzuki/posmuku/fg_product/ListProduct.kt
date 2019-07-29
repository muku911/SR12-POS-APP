package com.mukusuzuki.posmuku.fg_product

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mukusuzuki.posmuku.R
import com.mukusuzuki.posmuku.adapter.ListProductAdapter
import com.mukusuzuki.posmuku.data._ListProduct
import com.mukusuzuki.posmuku.database.Gudang
import com.mukusuzuki.posmuku.database.Product
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.fragment_list_product.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select

class ListProduct : Fragment() {

    private var productList: MutableList<_ListProduct> = mutableListOf()
    private lateinit var adapter: ListProductAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        productList.clear()


        adapter = ListProductAdapter(activity!!.baseContext, productList){

        }

        rvProduct.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(activity!!.baseContext)
        rvProduct.adapter = adapter
        showProductList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_list_product, container, false)
    }

    override fun onResume() {
        super.onResume()
        productList.clear()
        showProductList()
    }

    private fun showProductList(){
        context?.database?.use {
            val result  = select(Product.TABEL_NAME)
            val getdata = result.parseList(classParser<Product>())
            val result2  = select(Gudang.TABEL_NAME)
            val getdata2 = result2.parseList(classParser<Gudang>())
            var itung = 0
            while(itung < getdata.size){
                if (getdata[itung].code_item.equals(getdata2[itung].code_item)){
                    val data = _ListProduct(itung+1, getdata[itung].code_item, getdata[itung].name_item, getdata[itung].price_item, getdata2[itung].count_item)
                    productList.add(data)
                }
                itung++
            }

            adapter.notifyDataSetChanged()
        }
    }


}

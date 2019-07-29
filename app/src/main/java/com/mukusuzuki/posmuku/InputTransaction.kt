package com.mukusuzuki.posmuku

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mukusuzuki.posmuku.database.Pelanggan
import com.mukusuzuki.posmuku.database.Transaction
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.activity_input_transaction.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class InputTransaction : AppCompatActivity() {
    private var dropdownItem: MutableList<String> = mutableListOf()
    private lateinit var spinnerMap: HashMap<Int,String>
    private val fristString = "----Pilih Pelanggan----"
    private val lastString = "Pelanggan Baru..."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_transaction)
        setSupportActionBar(toolbar2)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        dropdownCreate()
        refresh.onRefresh {
            dropdownCreate()
        }

        spinnerPelanggan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val getName = dropdownItem[position]
                if(getName == lastString){
                    startActivity<InputPelanggan>()
                }
            }

        }

        btnNext.setOnClickListener {

            createTransaction()

        }

    }

    override fun onResume() {
        super.onResume()
        dropdownCreate()
    }

    private fun createTransaction() {
        if (spinnerPelanggan.selectedItem != fristString && spinnerPelanggan.selectedItem != lastString){
            var idTrancation: Int
            val idPelanggan  = spinnerMap[spinnerPelanggan.selectedItemPosition]?.toInt()
            val date         = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(System.currentTimeMillis())
            val sumItem     = 0
            val subTotal     = 0
            val spinnerGet   = spinnerDiskon.selectedItem.toString()
            var diskon       = 0
            when(spinnerGet){
                "10%" ->{
                    diskon = 10
                }
                "25%" ->{
                    diskon = 25
                }
                "35%" ->{
                    diskon = 35
                }
            }
            val total        = 0
            val status       = 0

            database.use {
                val result = select(Transaction.TABEL_NAME)
                val getdata = result.parseList(classParser<Transaction>())
                idTrancation = getdata.count()
                idTrancation += 1

                insert(Transaction.TABEL_NAME, Transaction.ID to idTrancation,
                    Transaction.ID_PELANGGAN to idPelanggan, Transaction.DATE to date, Transaction.TOTAL_ITEM to sumItem,
                    Transaction.SUB_TOTAL to subTotal, Transaction.DISKON to diskon, Transaction.TOTAL to total,
                    Transaction.STATUS to status)

                println("IDTRANSAKSI: $idTrancation, IDPELANGGAN: $idPelanggan")
                finish()
                ctx.startActivity<com.mukusuzuki.posmuku.Transaction>("id" to idTrancation, "id_pelanggan" to idPelanggan,
                    "date" to date)
            }

        }else{
            toast("PILIH PELANGGAN")
        }
    }

    private fun dropdownCreate() {
        val diskon = arrayOf("10%", "25%", "35%")
        val diskonAdpter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, diskon)
        spinnerDiskon.adapter = diskonAdpter

        dropdownItem.clear()
        dropdownItem.add(fristString)
        database.use {
            val result  = select(Pelanggan.TABEL_NAME)
            val getdata = result.parseList(classParser<Pelanggan>())
            var a = 0
            spinnerMap = HashMap()
            spinnerMap[a] = "$a"
            while (a < getdata.count()){
                val data = getdata[a].name.toString()
                spinnerMap[a+1] = getdata[a].id.toString()
                dropdownItem.add(data)
                a++
            }
            dropdownItem.add(lastString)
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dropdownItem)
        spinnerPelanggan.adapter = adapter
        refresh.isRefreshing = false
    }
}

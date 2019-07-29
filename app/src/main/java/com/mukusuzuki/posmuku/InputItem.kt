package com.mukusuzuki.posmuku

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.google.zxing.Result
import com.mukusuzuki.posmuku.database.DetailTransaction
import com.mukusuzuki.posmuku.database.Product
import com.mukusuzuki.posmuku.database.Transaction
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.activity_input_item.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.jetbrains.anko.db.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class InputItem : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var dropdownItem: MutableList<String> = mutableListOf()
    private lateinit var spinnerMap: HashMap<Int,String>
    private val fristString = "----Pilih Barang----"

    private var CODE: String? = ""
    private var codeItem:String = ""
    private var state:Int = 0
    private var id:Int = 0
    private var idPelanggan:Int = 0
    private var idItem:Int = 0
    private var sumItem:Int = 0
    private var diskon:Int = 0
    private var idDetail:Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_item)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        dropdownCreate()
        scanner?.setResultHandler(this)
        scanner?.startCamera()

        etSumItem.setText("1")

        val intent = intent
        state = intent.getIntExtra("state",0)
        if(state == 0){
            btnDelete.visibility = View.GONE
            id = intent.getIntExtra("id",0)
            idPelanggan = intent.getIntExtra("idPelanggan",0)
            diskon = intent.getIntExtra("diskon",0)
        }else if (state == 1){
            btnDelete.visibility = View.VISIBLE
            btnSave.text = "Simpan Perubahan"
            id = intent.getIntExtra("id",0)
            idPelanggan = intent.getIntExtra("idPelanggan",0)
            idItem = intent.getIntExtra("idItem",0)
            codeItem = intent.getStringExtra("codeItem")
            sumItem = intent.getIntExtra("sumItem",0)
            diskon = intent.getIntExtra("diskon",0)
            idDetail = intent.getIntExtra("idDetail",0)
            setFromBundle()
        }

    }

    private fun setFromBundle() {
        var a = 0
        println("count: ${spinnerMap.count()}")
        while (a < spinnerMap.count()){
            if(codeItem == spinnerMap[a]) {
                spinnerItem.setSelection(a)
            }
            a++
        }
        etSumItem.setText(sumItem.toString())
    }

    override fun handleResult(p0: Result?) {
        CODE= p0?.text
        val vibrator: Vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)

        doAsync {
            val selection = getSelection()
            runOnUiThread {
                spinnerItem.setSelection(selection)
            }
        }

        scanner?.setResultHandler(this)
        scanner?.startCamera()

    }

    private fun getSelection():Int{
        var selection = 0
        try {
            var a = 0
            println("count: ${spinnerMap.count()}")
            while (a < spinnerMap.count()){
                if(CODE == spinnerMap[a]) {
                    selection = a
                }
                a++
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
        return selection
    }

    private fun dropdownCreate() {
        dropdownItem.clear()
        dropdownItem.add(fristString)
        database.use {
            val result  = select(Product.TABEL_NAME)
            val getdata = result.parseList(classParser<Product>())

            var a = 0
            spinnerMap = HashMap()
            spinnerMap[a] = "$a"
            while(a < getdata.count()){
                val data = getdata[a].name_item.toString()
                spinnerMap[a+1] = getdata[a].code_item.toString()
                dropdownItem.add(data)
                a++
            }
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dropdownItem)
        spinnerItem.adapter = adapter
    }

    fun deleteAction(view : View) {
        println("OK")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
        builder.setPositiveButton("Iya"){dialog, which ->
            toast("Deleted")
            database.use {
                delete(DetailTransaction.TABEL_NAME, "${DetailTransaction.ID} = $idItem")
            }
            finish()
        }
        builder.setNegativeButton("Tidak"){dialog, which ->
            toast("Menghapus Dibatalkan")
        }
        builder.setMessage("Ingin Mengahapus Item Ini Dari Keranjang?")
        val alert = builder.create()
        alert.show()
    }

    fun save(view : View){
        if(spinnerItem.selectedItemPosition != 0) {
            when (state) {
                0 -> {
                    val count: Int = etSumItem.text.toString().toInt()
                    CODE = spinnerMap[spinnerItem.selectedItemPosition].toString()
                    database.use {
                        insert(
                            DetailTransaction.TABEL_NAME,
                            DetailTransaction.ID_TRANSACTION to id,
                            DetailTransaction.ID_PELANGGAN to idPelanggan,
                            DetailTransaction.CODE_ITEM to CODE,
                            DetailTransaction.SUM_ITEM to count
                        )
                        finish()
                    }
                }
                1 -> {
                    val count: Int = etSumItem.text.toString().toInt()
                    CODE = spinnerMap[spinnerItem.selectedItemPosition].toString()
                    database.use {
                        update(
                            DetailTransaction.TABEL_NAME,
                            DetailTransaction.ID_TRANSACTION to id,
                            DetailTransaction.ID_PELANGGAN to idPelanggan,
                            DetailTransaction.CODE_ITEM to CODE,
                            DetailTransaction.SUM_ITEM to count
                        )
                            .whereArgs("(${DetailTransaction.ID} = {id})", "id" to "$idItem")
                            .exec()
                        finish()
                    }
                }
            }
        }else{
            toast("PILIH/SCAN BARANG")
        }
    }

    fun cancel(view : View){
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        scanner?.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner?.stopCamera()
    }

    override fun onStop() {
        super.onStop()
        scanner?.stopCamera()
    }
}

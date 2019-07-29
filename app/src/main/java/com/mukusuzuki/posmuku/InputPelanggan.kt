package com.mukusuzuki.posmuku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.mukusuzuki.posmuku.database.Pelanggan
import com.mukusuzuki.posmuku.database.database
import kotlinx.android.synthetic.main.activity_input_pelanggan.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast

class InputPelanggan : AppCompatActivity() {

    private var nama: String = ""
    private var nomorHp: String = ""
    private var provinsi: String = ""
    private var kota: String = ""
    private var kecamatan: String = ""
    private var alamat: String = ""

    private var id: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_pelanggan)
        setSupportActionBar(toolbar2)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val intent = intent
        id = intent.getIntExtra("id",0)

        if(id != 0){
            setTextET()
        }

        buttonSave.setOnClickListener {

            nama = etNama.text.toString()
            nomorHp = etPhone.text.toString()
            provinsi = etProvinsi.text.toString()
            kota = etKota.text.toString()
            kecamatan = etKecamatan.text.toString()
            alamat = etAlammat.text.toString()

            if(id == 0) {
                if (checkNullFill()) {
                    database.use {
                        insert(
                            Pelanggan.TABEL_NAME, Pelanggan.NAME to nama,
                            Pelanggan.PHONE to nomorHp, Pelanggan.PROVINSI to provinsi,
                            Pelanggan.KOTA to kota, Pelanggan.KECAMATAN to kecamatan, Pelanggan.ADDRESS to alamat
                        )

                        finish()
                    }
                } else {
                    toast("ISI SEMUA DATA!!!")
                }
            }else{
                if (checkNullFill()) {
                    database.use {
                        update(Pelanggan.TABEL_NAME, Pelanggan.NAME to nama,
                            Pelanggan.PHONE to nomorHp, Pelanggan.PROVINSI to provinsi,
                            Pelanggan.KOTA to kota, Pelanggan.KECAMATAN to kecamatan, Pelanggan.ADDRESS to alamat)
                            .whereArgs("(${Pelanggan.ID} = {id})", "id" to "$id")
                            .exec()

                        finish()
                    }
                } else {
                    toast("ISI SEMUA DATA!!!")
                }
            }
        }

        buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun setTextET(){
        database.use {
            val result = select(Pelanggan.TABEL_NAME).whereArgs("(${Pelanggan.ID} = {id})", "id" to "$id")
            val getdata = result.parseList(classParser<Pelanggan>())

            etNama.setText(getdata[0].name)
            etPhone.setText(getdata[0].phone)
            etProvinsi.setText(getdata[0].provinsi)
            etKota.setText(getdata[0].kota)
            etKecamatan.setText(getdata[0].kecamatan)
            etAlammat.setText(getdata[0].address)
        }

    }

    private fun checkNullFill():Boolean{
        var checking = false

        if(nama != "" && nomorHp != "" && provinsi != "" && kota != "" && kecamatan != "" && alamat != "" ){
            checking = true
        }

        return checking
    }
}

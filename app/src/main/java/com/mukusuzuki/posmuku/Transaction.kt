package com.mukusuzuki.posmuku

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.mukusuzuki.posmuku.adapter.ListItemAdapter
import com.mukusuzuki.posmuku.data._DetailTransaction
import com.mukusuzuki.posmuku.database.*
import com.mukusuzuki.posmuku.database.Transaction
import com.mukusuzuki.posmuku.plugins.SolidBorder
import kotlinx.android.synthetic.main.activity_transaction.*
import org.jetbrains.anko.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.support.v4.onRefresh
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Transaction : AppCompatActivity() {
    private var itemList: MutableList<_DetailTransaction> = mutableListOf()
    private lateinit var adapter: ListItemAdapter

    private val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD, BaseColor.BLACK)
    private val nameFont  = Font(Font.FontFamily.TIMES_ROMAN, 11f, Font.BOLD, BaseColor.BLACK)
    private val normalFont  = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL, BaseColor.BLACK)

    private var id:Int          = 0
    private var idPelanggan:Int = 0
    private var diskon:Int      = 0
    private var date:String            = ""
    private var namaPelanggan:String   = ""
    private var alamatPelanggan:String = ""
    private var noHpPelanggan:String   = ""
    private var jenisJNE:String        = ""
    private var subTotal = 0
    private var disTotal = 0
    private var total = 0
    private var sumItemTotal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        setSupportActionBar(toolbar3)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val intent = intent
        id = intent.getIntExtra("id",0)
        idPelanggan = intent.getIntExtra("id_pelanggan",0)
        date = intent.getStringExtra("date")
        println("Transaksi -> ID: $id, ID_PELANGGAN: $idPelanggan")

        itemList.clear()
        adapter = ListItemAdapter(this, itemList){
            startActivity<InputItem>("state" to 1, "id" to id, "idPelanggan" to idPelanggan, "idItem" to it.id,
                "codeItem" to it.code_item, "sumItem" to it.sum_item, "diskon" to diskon, "idDetail" to it.id)
        }

        rvDTrans.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(this)
        rvDTrans.adapter = adapter

        setToolbarTitle()

        fabAdd.setOnClickListener {
            startActivity<InputItem>("state" to 0, "id" to id, "idPelanggan" to idPelanggan, "diskon" to diskon)
        }

        fabPrint.setOnClickListener {
            alert{
                titleResource   = R.string.title_jne
                messageResource = R.string.pick_jne
                positiveButton("Reguler") {
                    jenisJNE = "Reguler"
                    createFaktur(idPelanggan, namaPelanggan)
                }
                negativeButton("YES"){
                    jenisJNE = "YES"
                    createFaktur(idPelanggan, namaPelanggan)
                }
            }.show()
        }

        refreshDT.onRefresh {
            itemList.clear()
            sumItemTotal = 0
            showItemList()
        }
    }

    override fun onResume() {
        super.onResume()
        sumItemTotal = 0
        itemList.clear()
        showItemList()
    }

    @SuppressLint("SetTextI18n")
    private fun showItemList() {
        total    = 0
        subTotal = 0
        disTotal = 0

        database.use{
            val result  = select(DetailTransaction.TABEL_NAME)
                .whereArgs("(${DetailTransaction.ID_TRANSACTION} = {ID}) and (${DetailTransaction.ID_PELANGGAN} = {ID_PELANGGAN})",
                "ID" to id, "ID_PELANGGAN" to idPelanggan)
            val getdata = result.parseList(classParser<DetailTransaction>())

            val result2  = select(Product.TABEL_NAME)
            val getdata2 = result2.parseList(classParser<Product>())

            val result3  = select(Transaction.TABEL_NAME).whereArgs("(${Transaction.ID} = {ID}) and " +
                    "(${Transaction.ID_PELANGGAN} = {ID_PELANGGAN}) and (${Transaction.DATE} = {DATE})",
                "ID" to id, "ID_PELANGGAN" to idPelanggan, "DATE" to date)
            val getdata3 = result3.parseList(classParser<Transaction>())

            val result4  = select(Pelanggan.TABEL_NAME)
                .whereArgs("(${Pelanggan.ID} = {ID_PELANGGAN})",
                    "ID_PELANGGAN" to idPelanggan)
            val getdata4 = result4.parseList(classParser<Pelanggan>())

            alamatPelanggan = "${getdata4[0].address}, ${getdata4[0].kecamatan}, ${getdata4[0].kota} - ${getdata4[0].provinsi}"
            noHpPelanggan   = getdata4[0].phone.toString()

            var a = 0
            var b = 0
            while(a < getdata.count()){
                while (b < getdata2.count()){
                    if(getdata[a].code_item == getdata2[b].code_item){
                        val data = _DetailTransaction(a+1, getdata[a].id, getdata[a].id_transaksi, getdata[a].id_pelanggan,
                            getdata[a].code_item, getdata2[b].name_item, getdata[a].sum_item, getdata2[b].price_item!!.toInt())
                        itemList.add(data)
                        sumItemTotal += getdata[a].sum_item!!.toInt()
                        subTotal += (getdata2[b].price_item!!.toInt() * getdata[a].sum_item!!.toInt())
                    }
                    b++
                }
                a++
                b = 0
            }

            diskon     = getdata3[0].diskon!!.toInt()
            disTotal   = (subTotal*diskon)/100
            total  = subTotal - disTotal

            update(Transaction.TABEL_NAME, Transaction.SUB_TOTAL to subTotal, Transaction.TOTAL to total, Transaction.TOTAL_ITEM to sumItemTotal)
                .whereArgs("(${Transaction.ID} = $id)")
                .exec()

            tvDTdate.text     = "Transaksi Pada Tanggal $date"
            tvSubTotal1.text  = toRupiah(subTotal)
            tvDiskon1.text    = "$diskon%"
            tvTotal1.text     = toRupiah(total)
            tvItemTotal.text  = "$sumItemTotal"
        }

        if (itemList.count() == 0){
            toast("-No Items-")
            rvDTrans.visibility = View.GONE
            tvDTNoItem.visibility = View.VISIBLE
            llTop.visibility = View.GONE
            llBotom.visibility = View.GONE
        }else{
            rvDTrans.visibility = View.VISIBLE
            tvDTNoItem.visibility = View.GONE
            llTop.visibility = View.VISIBLE
            llBotom.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        refreshDT.isRefreshing = false
    }

    private fun toRupiah(rp: Int):String{
        val locale = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(locale)
        return format.format(rp)
    }

    private fun setToolbarTitle() {
        database.use{
            val result  = select(Pelanggan.TABEL_NAME)
            val getdata = result.parseList(classParser<Pelanggan>())
            var a = 0
            while (a < getdata.count()){
                if(idPelanggan == getdata[a].id){
                    namaPelanggan = getdata[a].name.toString()
                    supportActionBar!!.title = namaPelanggan
                }
                a++
            }
        }
    }

    private fun createFaktur(id: Int, pelanggan: String){
        val folder = "Faktur/$id" + "_$pelanggan"
        createFolder("MyPDF/Faktur/$id" + "_$pelanggan")
        val doc = Document(PageSize.A4)
        val docName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + "_$pelanggan"
        val docFilePath = Environment.getExternalStorageDirectory().toString() + "/MyPDF/$folder/" + docName + ".pdf"

        try {
            PdfWriter.getInstance(doc, FileOutputStream(docFilePath))
            doc.open()
            doc.addAuthor("Muku Suzuki")
            doc.addTitle("Faktur $id" + "_$pelanggan")

            doc.add(setTopTable())
            doc.add(Paragraph("\n"))
            doc.add(setItemTable())
            doc.add(Paragraph("\n"))
            doc.add(setBottomTable())

            val table = PdfPTable(6)
            table.widthPercentage = 100F
            val columnWidth = floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f)
            table.setWidths(columnWidth)
            table.addCell(getLastFill("Hormat Kami",1))
            table.addCell(getLastFill("Penerima",1))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill("\n\n(......................)",1))
            table.addCell(getLastFill("\n\n(......................)",1))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill(" ",0))
            table.addCell(getLastFill(" ",0))
            doc.add(table)

            doc.close()
            Toast.makeText(this, "$docName.pdf berhasil dibuat\n$docFilePath", Toast.LENGTH_LONG).show()

        }catch (e:Exception){
            println(e.printStackTrace())
        }
    }

    private fun createFolder(folderName: String) {
        val myFolder = Environment.getExternalStorageDirectory().toString() + "/" + folderName
        val file = File(myFolder)
        file.mkdir()
    }

    private fun getNormalCell(text: String, state: Int, font: Font): PdfPCell {
        val cell = PdfPCell(Paragraph(text, font))
        cell.border = Rectangle.NO_BORDER
        when(state){
            0 ->{
                cell.horizontalAlignment = PdfPCell.ALIGN_LEFT
            }
            1 ->{
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            }
            2 ->{
                cell.horizontalAlignment = PdfPCell.ALIGN_LEFT
                cell.rowspan = 3
            }
        }

        return cell
    }

    private fun getFillCell(text: String, font: Font, state: Int): PdfPCell {
        val phrase = Phrase(text, font)
        val cell = PdfPCell(phrase)
        cell.border = PdfPCell.NO_BORDER
        when(state){
            1 ->{//garis atas bawah
                cell.cellEvent = SolidBorder(PdfPCell.TOP)
                cell.cellEvent = SolidBorder(PdfPCell.BOTTOM)
            }
            2 ->{//garis atas bawah text di tengah
                cell.cellEvent = SolidBorder(PdfPCell.TOP)
                cell.cellEvent = SolidBorder(PdfPCell.BOTTOM)
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            }
            3 ->{//item di tengah
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            }
            4 ->{//item di kanan
                cell.horizontalAlignment = PdfPCell.ALIGN_RIGHT
            }
        }

        return cell
    }

    private fun getLastCell(text: String, state: Int): PdfPCell {
        val phrase = Phrase(text, normalFont)
        val cell = PdfPCell(phrase)
        cell.border = PdfPCell.NO_BORDER
        cell.cellEvent = SolidBorder(PdfPCell.BOTTOM)
        when(state){
            1 ->{//item di tengah
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            }
            2 ->{//item di kanan
                cell.horizontalAlignment = PdfPCell.ALIGN_RIGHT
            }
        }

        return cell
    }

    private fun getLastFill(text: String, state: Int): PdfPCell {
        val phrase = Phrase(text, normalFont)
        val cell = PdfPCell(phrase)
        cell.border = PdfPCell.NO_BORDER
        when(state){
            1 ->{//isian paling bawah
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            }
            2 ->{//isian paling bawah
                cell.horizontalAlignment = PdfPCell.ALIGN_RIGHT
            }
            3 ->{
                cell.rowspan = 2
            }
        }

        return cell
    }

    private fun setBottomTable() : PdfPTable {
        val table = PdfPTable(10)
        table.widthPercentage = 100F
        val columnWidth = floatArrayOf(15f, 3f, 47f, 20f, 3f, 8f, 15f, 20f, 3f, 15f)
        table.setWidths(columnWidth)

        table.addCell(getLastFill("Keterangan",0))
        table.addCell(getLastFill(":",1))
        val keterangan = "Dikirim Memakai JNE-$jenisJNE"
        table.addCell(getLastFill(keterangan,3))
        table.addCell(getLastFill("Jumlah Barang",0))
        table.addCell(getLastFill(":",1))
        table.addCell(getLastFill(" ",0))
        val jumlahTotalBarang = "$sumItemTotal"
        table.addCell(getLastFill(jumlahTotalBarang,2))
        table.addCell(getLastFill("Sub Total",2))
        table.addCell(getLastFill(":",1))
        table.addCell(getLastFill(setFormatNumber(subTotal),2))
        table.addCell(getLastFill(" ",0))
        table.addCell(getLastFill(" ",0))
        table.addCell(getLastFill("Diskon",0))
        table.addCell(getLastFill(":",1))
        table.addCell(getLastFill("$diskon%",1))
        table.addCell(getLastFill(setFormatNumber(disTotal),2))
        table.addCell(getLastFill("Total Akhir",2))
        table.addCell(getLastFill(":",1))
        table.addCell(getLastFill(setFormatNumber(total),2))

        return table
    }

    private fun setFormatNumber(totSemua: Int): String {
        val formater = DecimalFormat("##,###,###")
        return formater.format(totSemua)
    }

    private fun setItemTable() : PdfPTable {
        val table = PdfPTable(6)
        table.widthPercentage = 100F
        val columnWidth = floatArrayOf(8f, 18f, 47f, 22f, 20f, 15f)
        table.setWidths(columnWidth)
        //1,1,1,2,2,2
        table.addCell(getFillCell("No", nameFont,1))
        table.addCell(getFillCell("Kode Barang", nameFont,1))
        table.addCell(getFillCell("Nama Barang", nameFont,1))
        table.addCell(getFillCell("Jumlah Satuan", nameFont,2))
        table.addCell(getFillCell("Harga Satuan", nameFont,2))
        table.addCell(getFillCell("Total", nameFont,2))

        var abc = 0
        while (abc < itemList.count()){
            val no:String          = itemList[abc].no.toString()
            val code:String        = itemList[abc].code_item.toString()
            val nama:String        = itemList[abc].name_item.toString()
            val jumlah:String      = itemList[abc].sum_item.toString()
            val hargaSatuan:String = setFormatNumber(itemList[abc].price_item!!)
            val totSemua:Int       = itemList[abc].price_item!! * itemList[abc].sum_item!!
            val totalBarang:String = setFormatNumber(totSemua)

            if(itemList.count()-1 == abc){
                //last item 0,0,0,1,2,2
                table.addCell(getLastCell(no,0))
                table.addCell(getLastCell(code, 0))
                table.addCell(getLastCell(nama, 0))
                table.addCell(getLastCell("$jumlah PCS", 1))
                table.addCell(getLastCell(hargaSatuan, 2))
                table.addCell(getLastCell(totalBarang, 2))
            }else{
                //0,0,0,3,4,4
                table.addCell(getFillCell(no,normalFont,0))
                table.addCell(getFillCell(code, normalFont,0))
                table.addCell(getFillCell(nama, normalFont,0))
                table.addCell(getFillCell("$jumlah PCS", normalFont,3))
                table.addCell(getFillCell(hargaSatuan, normalFont,4))
                table.addCell(getFillCell(totalBarang, normalFont,4))
            }
            abc++
        }

        return table
    }

    private fun setTopTable(): PdfPTable {

        @Suppress("DEPRECATION")
        val myLogo = this.resources.getDrawable(R.drawable.icon_sr12)
        val bitmap = (myLogo as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bitMapData = stream.toByteArray()
        val logoSR = Image.getInstance(bitMapData)

        val mainTable = PdfPTable(5)
        mainTable.widthPercentage = 100F
        val columnWidth = floatArrayOf(20f, 30f, 20f, 5f, 40f)
        mainTable.setWidths(columnWidth)

        val cellImg = PdfPCell()

        cellImg.addElement(logoSR)
        cellImg.rowspan = 7
        cellImg.border = Rectangle.NO_BORDER

        mainTable.addCell(cellImg)
        mainTable.addCell(getNormalCell("FAKTUR PENJUALAN", 0, titleFont))
        //Nomor Transaksi
        mainTable.addCell(getNormalCell("No. Transaksi", 0, normalFont))
        mainTable.addCell(getNormalCell(":", 1, normalFont))
        val noTransaksi = "RFP-$id"
        mainTable.addCell(getNormalCell(noTransaksi, 0, normalFont))
        mainTable.addCell(getNormalCell("AGEN RITA ROSMIATI", 0, nameFont))
        //Tanggal Transaksi
        mainTable.addCell(getNormalCell("Tanggal", 0, normalFont))
        mainTable.addCell(getNormalCell(":", 1, normalFont))
        val tanggal = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        mainTable.addCell(getNormalCell(tanggal, 0, normalFont))
        mainTable.addCell(getNormalCell("Bogor, Indonesia", 0, normalFont))
        //Nama Pembeli
        mainTable.addCell(getNormalCell("Nama Pembeli", 0, normalFont))
        mainTable.addCell(getNormalCell(":", 1, normalFont))
        mainTable.addCell(getNormalCell(namaPelanggan, 0, normalFont))
        mainTable.addCell(getNormalCell("082311412500", 0, normalFont))
        //Alamat Pembeli
        mainTable.addCell(getNormalCell("Alamat", 0, normalFont))
        mainTable.addCell(getNormalCell(":", 1, normalFont))
        mainTable.addCell(getNormalCell(alamatPelanggan, 2, normalFont))
        mainTable.addCell(getNormalCell("www.sr12hebal.com", 0, normalFont))
        mainTable.addCell(getNormalCell(" ", 0, normalFont))
        mainTable.addCell(getNormalCell(" ", 0, normalFont))
        mainTable.addCell(getNormalCell(" ", 0, normalFont))
        mainTable.addCell(getNormalCell(" ", 0, normalFont))
        mainTable.addCell(getNormalCell(" ", 0, normalFont))
        mainTable.addCell(getNormalCell(" ", 0, normalFont))
        //No Telepon
        mainTable.addCell(getNormalCell("No. Telp.", 0, normalFont))
        mainTable.addCell(getNormalCell(":", 1, normalFont))
        mainTable.addCell(getNormalCell(noHpPelanggan, 0, normalFont))

        return mainTable
    }
}

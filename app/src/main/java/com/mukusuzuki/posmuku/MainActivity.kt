package com.mukusuzuki.posmuku

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.mukusuzuki.posmuku.database.Product
import com.mukusuzuki.posmuku.database.database
import com.mukusuzuki.posmuku.fg_barcode.ListQRCode
import com.mukusuzuki.posmuku.fg_pelanggan.ListPelanggan
import com.mukusuzuki.posmuku.fg_product.ListProduct
import com.mukusuzuki.posmuku.fg_transaction.ListTransaction
import com.mukusuzuki.posmuku.fg_transaction.ListTransaktionDone
import com.mukusuzuki.posmuku.plugins.SolidBorder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("IMPLICIT_CAST_TO_ANY", "DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var productList: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        createFolder("MyPDF")
        createFolder("MyPDF/Faktur")
        createFolder("MyPDF/QRCode")

        database.use {
            val result  = select(Product.TABEL_NAME)
            val getdata = result.parseList(classParser<Product>())
            productList.addAll(getdata)

        }

        fab.setOnClickListener { view ->
            if(toolbar.title == "Daftar Produk"){
                Snackbar.make(view, "Menambahkan Barang", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else if(toolbar.title == "Daftar Transaksi"){
                val intent = Intent(applicationContext, InputTransaction::class.java)
                startActivity(intent)
            }else if(toolbar.title == "Transaksi Selesai"){
                val intent = Intent(applicationContext, InputTransaction::class.java)
                startActivity(intent)
            }else if(toolbar.title == "Daftar Pelanggan"){
                val intent = Intent(applicationContext, InputPelanggan::class.java)
                startActivity(intent)
            }
        }

        fabPrint.setOnClickListener {
            createPDF()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun displayScreen (id: Int){
        val fragment = when (id){
            R.id.nav_produk -> {
                toolbar.title = "Daftar Produk"
                textView.visibility = View.GONE
                textView4.visibility = View.GONE
                fab.visibility = View.VISIBLE
                fabPrint.visibility = View.GONE
                ListProduct()
            }R.id.nav_transaksi -> {
                toolbar.title = "Daftar Transaksi"
                textView.visibility = View.GONE
                textView4.visibility = View.GONE
                fab.visibility = View.VISIBLE
                fabPrint.visibility = View.GONE
                ListTransaction()
            }R.id.nav_barcode -> {
                toolbar.title = "Daftar Barcode"
                textView.visibility = View.GONE
                textView4.visibility = View.GONE
                fab.visibility = View.GONE
                fabPrint.visibility = View.VISIBLE
                ListQRCode()
            }
            R.id.nav_transaksi_done ->{
                toolbar.title = "Transaksi Selesai"
                textView.visibility = View.GONE
                textView4.visibility = View.GONE
                fab.visibility = View.VISIBLE
                fabPrint.visibility = View.GONE
                ListTransaktionDone()
            }
            R.id.nav_pelanggan -> {
                toolbar.title = "Daftar Pelanggan"
                textView.visibility = View.GONE
                textView4.visibility = View.GONE
                fab.visibility = View.VISIBLE
                fabPrint.visibility = View.GONE
                ListPelanggan()
            }
            else -> {

            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.relativeLayout, fragment as Fragment)
            .commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        displayScreen(item.itemId)


        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("SimpleDateFormat")
    private fun createPDF(){
        val doc = Document(PageSize.A4)
        val docName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + "_QRCODE"
        val docFilePath = Environment.getExternalStorageDirectory().toString() + "/MyPDF/QRCode/" + docName + ".pdf"

        try {
            PdfWriter.getInstance(doc, FileOutputStream(docFilePath))
            doc.open()
            doc.addAuthor("Muku Suzuki")
            doc.addTitle("QRCode Print")

            var item = 0
            val sumItem = productList.count()
            while (item < sumItem){
                val par = Paragraph(productList[item].name_item)
                par.alignment = Paragraph.ALIGN_CENTER
                doc.add(par)
                doc.add(Paragraph("\n"))

                val table = PdfPTable(8)
                table.widthPercentage = 100F
                val columnWidth = floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)
                table.setWidths(columnWidth)

                val cell = PdfPCell()
                cell.border = Rectangle.NO_BORDER

                val qrgEncoder = QRGEncoder(productList[item].code_item, null, QRGContents.Type.TEXT, 100)
                val bitmap = qrgEncoder.encodeAsBitmap()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bitMapData = stream.toByteArray()
                val qrCode = Image.getInstance(bitMapData)

                cell.addElement(qrCode)
                table.addCell(cell)

                var sumQrcode = 0
                while (sumQrcode <= 10){
                    table.addCell(cell)
                    table.addCell(cell)
                    table.addCell(cell)
                    table.addCell(cell)
                    table.addCell(cell)
                    table.addCell(cell)
                    table.addCell(cell)
                    table.addCell(cell)
                    sumQrcode++
                }

                doc.add(table)
                item++
            }

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

}

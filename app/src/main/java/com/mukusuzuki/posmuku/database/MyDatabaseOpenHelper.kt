package com.mukusuzuki.posmuku.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "db_myPOS_ver03.db", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance as MyDatabaseOpenHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(Product.TABEL_NAME, true,
            Product.ITEM_CODE to TEXT + PRIMARY_KEY,
            Product.ITEM_NAME to TEXT,
            Product.ITEM_PRICE to INTEGER)

        db.createTable(Gudang.TABEL_NAME, true,
            Gudang.ITEM_CODE to TEXT + PRIMARY_KEY,
            Gudang.ITEM_COUNT to INTEGER)

        db.createTable(Pelanggan.TABEL_NAME, true,
            Pelanggan.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Pelanggan.NAME to TEXT,
            Pelanggan.PROVINSI to TEXT,
            Pelanggan.KOTA to TEXT,
            Pelanggan.KECAMATAN to TEXT,
            Pelanggan.ADDRESS to TEXT,
            Pelanggan.PHONE to TEXT)

        db.createTable(Transaction.TABEL_NAME, true,
            Transaction.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            Transaction.ID_PELANGGAN to INTEGER,
            Transaction.DATE to TEXT,
            Transaction.TOTAL_ITEM to INTEGER,
            Transaction.SUB_TOTAL to INTEGER,
            Transaction.DISKON to INTEGER,
            Transaction.TOTAL to INTEGER,
            Transaction.STATUS to INTEGER)

        db.createTable(DetailTransaction.TABEL_NAME, true,
            DetailTransaction.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
            DetailTransaction.ID_TRANSACTION to INTEGER,
            DetailTransaction.ID_PELANGGAN to INTEGER,
            DetailTransaction.CODE_ITEM to TEXT,
            DetailTransaction.SUM_ITEM to INTEGER)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(Product.TABEL_NAME, true)
        db.dropTable(Gudang.TABEL_NAME, true)
        db.dropTable(Pelanggan.TABEL_NAME, true)
        db.dropTable(Transaction.TABEL_NAME, true)
        db.dropTable(DetailTransaction.TABEL_NAME, true)
    }
}


// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)
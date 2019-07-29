package com.mukusuzuki.posmuku.database

data class Product(val code_item: String?, val name_item: String?, val price_item: Int?) {

    companion object {
        const val TABEL_NAME: String = "TABLE_PRODUCT"
        const val ITEM_CODE: String  = "ITEM_CODE"
        const val ITEM_NAME: String  = "ITEM_NAME"
        const val ITEM_PRICE: String = "ITEM_PRICE"
    }
}

data class Gudang(val code_item: String?, val count_item: Int?) {

    companion object {
        const val TABEL_NAME: String = "TABLE_GUDANG"
        const val ITEM_CODE: String  = "ITEM_CODE"
        const val ITEM_COUNT: String = "ITEM_COUNT"
    }
}

data class Pelanggan(val id: Int?, val name: String?, val provinsi: String?, val kota: String?, val kecamatan: String?, val address: String?, val phone: String?) {

    companion object {
        const val TABEL_NAME: String = "TABLE_PELANGGAN"
        const val ID: String = "ID_"
        const val NAME: String  = "NAME"
        const val PROVINSI: String  = "PROVINSI"
        const val KOTA: String  = "KOTA"
        const val KECAMATAN: String  = "KECAMATAN"
        const val ADDRESS: String = "ADDRESS"
        const val PHONE: String = "PHONE"
    }
}

data class Transaction(val id: Int?, val id_pelanggan: Int?, val tanggal: String?, val total_item: Int?, val sub_total: Int?, val diskon: Int?, val total: Int?, val status: Int?) {

    companion object {
        const val TABEL_NAME: String = "TABLE_TRANSACTION"
        const val ID: String = "ID_"
        const val ID_PELANGGAN: String  = "ID_PELANGGAN"
        const val DATE: String  = "DATE"
        const val TOTAL_ITEM: String = "TOTAL_ITEM"
        const val SUB_TOTAL: String = "SUB_TOTAL"
        const val DISKON: String = "DISKON"
        const val TOTAL: String = "TOTAL"
        const val STATUS: String = "STATUS"
    }
}

data class DetailTransaction(val id: Int?, val id_transaksi: Int?, val id_pelanggan: Int?, val code_item: String?, val sum_item: Int?) {

    companion object {
        const val TABEL_NAME: String = "TABLE_DETAIL_TRANSACTION"
        const val ID: String = "ID_"
        const val ID_TRANSACTION: String  = "ID_TRANSACTION"
        const val ID_PELANGGAN: String = "ID_PELANGGAN"
        const val CODE_ITEM: String = "CODE_ITEM"
        const val SUM_ITEM: String = "SUM_ITEM"
    }
}
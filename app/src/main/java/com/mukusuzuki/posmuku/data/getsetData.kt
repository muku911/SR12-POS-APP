package com.mukusuzuki.posmuku.data

class _Product(val item_code: String?, val item_name: String?, val item_price: Int?)

class _ListProduct(val id:Int?, val item_code:String?, val item_name:String?, val item_price: Int?, val item_count: Int?)

class _Pelanggan(val no: Int?, val id: Int?, val name: String?, val provinsi: String?, val kota: String?, val kecamatan: String?, val address: String?, val phone: String?)

class _ListTransaction(val no: Int?,val id: Int?, val id_pelanggan:Int?, val nama_pelanggan: String?, val tanggal: String?)

class _DetailTransaction(val no: Int?, val id: Int?, val id_transaksi: Int?, val id_pelanggan: Int?, val code_item: String?, val name_item: String?, val sum_item: Int?, val price_item: Int?)
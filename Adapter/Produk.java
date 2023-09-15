package com.taufiqskripsiapp.masbro.Adapter;

import java.util.HashMap;

public class Produk {
    public String nama;
    public String deskripsi;
    public String barcode;
    public String gambar;
    public String kategori;
    public String kemasan;
    public HashMap<String, Integer> harga;
    public HashMap<String, Boolean> ketersediaan;

    public String jumlahProduk;
    public int jumlah;
    public String key;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKemasan() {
        return kemasan;
    }

    public void setKemasan(String kemasan) {
        this.kemasan = kemasan;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getHargaFromToko(String pilihanToko) {
        if (harga != null && harga.containsKey(pilihanToko)) {
            return harga.get(pilihanToko);
        }
        return 0; // atau nilai default lainnya jika toko tidak ditemukan
    }

    public String getJumlahProduk() {
        return jumlahProduk;
    }

    public void setJumlahProduk(String jumlahProduk) {
        this.jumlahProduk = jumlahProduk;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}



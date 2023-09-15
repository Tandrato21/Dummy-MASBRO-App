package com.taufiqskripsiapp.masbro.Adapter;

import static androidx.fragment.app.FragmentManager.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.taufiqskripsiapp.masbro.Adapter.Manager.ShoppingCartManager;
import com.taufiqskripsiapp.masbro.MainActivity;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.Utils.ConVar;
import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingViewHolder> {

    private List<Produk> produkList;
    private ShoppingCartManager cartManager;
    AppSharedViewModel appSharedViewModel;
    private  LifecycleOwner lifecycleOwner;


    public ShoppingCartAdapter(List<Produk> produkList, ShoppingCartManager cartManager, LifecycleOwner lifecycleOwner, AppSharedViewModel appSharedViewModel){
        this.produkList = produkList;
        this.cartManager = cartManager;
        this.lifecycleOwner = lifecycleOwner;
        this.appSharedViewModel = appSharedViewModel;
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder{
        public TextView textNamaProduk;
        public TextView textBarcodeProduk;
        public TextView textHargaProduk;
        public TextView textJumlahProduk;
        public ImageView produkIV;
        public MaterialButton decButton;
        public MaterialButton incButton;

        public String pilihanToko;
        public int hargaPerUnit;


        public ShoppingViewHolder(View view){
            super(view);
            textNamaProduk = view.findViewById(R.id.TextNamaProduk);
            textBarcodeProduk = view.findViewById(R.id.TextBarcodeProduk);
            textHargaProduk = view.findViewById(R.id.TextHargaProduk);
            textJumlahProduk = view.findViewById(R.id.TextJumlahProduk);
            produkIV = view.findViewById(R.id.ImageViewProduk);
            decButton = view.findViewById(R.id.ButtonKurang);
            incButton = view.findViewById(R.id.ButtonTambah);
        }
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_produk, parent, false);
        return new ShoppingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
        Produk produk = produkList.get(position);
        String namaProduk = produk.nama;
        String barcodeProduk = produk.barcode;
        String imageProduk = produk.gambar;
        String hargaProduk;
        holder.textNamaProduk.setText(namaProduk);
        holder.textBarcodeProduk.setText(barcodeProduk);

        Observer<String> tokoObserver = new Observer<String>() {
            @Override
            public void onChanged(String tokoPilihan) {
                holder.pilihanToko = tokoPilihan;
                String tokoDatabaseKey = "harga" + holder.pilihanToko.split(" ")[0]; // asumsi "Noari Indah" menjadi "hargaNoari"
                Log.d("DEBUG", "onChanged on ShoppingCartAdapter: Toko adalah " + tokoPilihan);
                Log.d("DEBUG", "onChanged on ShoppingCartAdapter: database key adalah " + tokoDatabaseKey);
                Log.d("DEBUG", "Isi HashMap harga: " + produk.harga.toString());

                if (produk.harga.containsKey(tokoDatabaseKey)) {
                    int hargaProduk = produk.harga.get(tokoDatabaseKey);
                    holder.hargaPerUnit = hargaProduk;

                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                    String hargaFormated = numberFormat.format(hargaProduk).replace("Rp", "Rp. ");

                    holder.textHargaProduk.setText(String.valueOf(hargaFormated));
                    final int position = holder.getAdapterPosition();
                    Log.d("DEBUG", "Adapter Position: " + holder.getAdapterPosition());

                    appSharedViewModel.getHargaTotalLiveData(position).observe(lifecycleOwner, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer hargaTotal) {
                            Log.d("DEBUG", "onChanged called for position: " + position + " with value: " + hargaTotal);
                            String hargaFormated = numberFormat.format(hargaTotal).replace("Rp", "Rp. ");
                            holder.textHargaProduk.setText(String.valueOf(hargaFormated));
                        }
                    });

                } else {
                    Log.d("DEBUG", "Harga tidak tersedia untuk toko: " + holder.pilihanToko);
                    holder.textHargaProduk.setText("Harga tidak tersedia");
                }

//                Log.d("DEBUG", "onChanged on ShoppingCartAdapter: Toko adalah " + tokoPilihan);
//                if (produk.harga.containsKey(tokoPilihan)) {
//                    int hargaProduk = produk.harga.get(tokoPilihan);
//                    holder.textHargaProduk.setText(String.valueOf(hargaProduk));
//                } else {
//                    holder.textHargaProduk.setText("Harga tidak tersedia");
//                }

                appSharedViewModel.getPilihanTokoLiveData().removeObserver(this);
            }
        };
        appSharedViewModel.getPilihanTokoLiveData().observe(lifecycleOwner, tokoObserver);

        Glide.with(holder.itemView.getContext())
                .load(imageProduk)
                .into(holder.produkIV);

        if(produk.jumlah> 0){
            holder.textJumlahProduk.setText(String.valueOf(produk.jumlah));
        }

        holder.incButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartManager.handleIncrement(holder.getAdapterPosition());
            }
        });

        holder.decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartManager.handleDecrement(holder.getAdapterPosition());
            }
        });

        HashMap<String, Integer> jumlahProdukMap = appSharedViewModel.getJumlahProdukMap().getValue();
        if (jumlahProdukMap != null && jumlahProdukMap.containsKey(produk.getKey())) {
            int jumlah = jumlahProdukMap.get(produk.getKey());
            holder.textJumlahProduk.setText(String.valueOf(jumlah));
        } else {
            holder.textJumlahProduk.setText("1");
        }
    }

    public void updateJumlahProduk(HashMap<String, Integer> jumlahProdukMap) {
        // Loop melalui semua item di Adapter dan perbarui jumlah produk
        for (Produk produk : produkList) {
            if (jumlahProdukMap.containsKey(produk.barcode)) {
                int jumlah = jumlahProdukMap.get(produk.barcode);
                // Simpan jumlah ini ke dalam objek Produk
                produk.jumlah = jumlah;
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return produkList.size();
    }
}

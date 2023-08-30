package com.taufiqskripsiapp.masbro.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taufiqskripsiapp.masbro.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProdukViewHolder> {

    private List<Produk> productList;

    public ProductAdapter(List<Produk> productList){
        this.productList = productList;
    }

    public class ProdukViewHolder extends RecyclerView.ViewHolder{
        public TextView txtProductName;

        public ProdukViewHolder(View view){
            super(view);
            txtProductName = view.findViewById(R.id.productName);
        }
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list, parent, false);
        return new ProdukViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = productList.get(position);
        String namaProduk = produk.nama;
        holder.txtProductName.setText(namaProduk);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

package com.taufiqskripsiapp.masbro.Adapter.Manager;

import com.taufiqskripsiapp.masbro.Adapter.Produk;

public class ShoppingCartManager {

    public interface OnItemButtonClickListener{
        void onIncrementClick(int position);
        void onDecrementClick(int position);
    }


    private OnItemButtonClickListener listener;

    public void setOnItemButtonClickListener(OnItemButtonClickListener listener){
        this.listener = listener;
    }

    public void handleIncrement(int position) {
        if (listener != null) {
            listener.onIncrementClick(position);
        }
    }

    public void handleDecrement(int position) {
        if (listener != null) {
            listener.onDecrementClick(position);
        }
    }

}

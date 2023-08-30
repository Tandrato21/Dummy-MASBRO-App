package com.taufiqskripsiapp.masbro.Utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MasbroUtils {

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}

//    Jika ada masalah, ini kode asli sembunyikan keyboard
//    private void hideKeyboard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        View focusedView = getCurrentFocus();
//        if (focusedView != null) {
//            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
//        }
//    }

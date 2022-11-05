package com.upsun.quizz.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.upsun.quizz.R;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 06,April,2020
 */
public class ToastMsg {

    Context context;
  LayoutInflater layoutInflater;
    public ToastMsg(Context context) {
        this.context = context;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void toastIconError(String s)
    {
        Toast toast=new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        View view=layoutInflater.inflate(R.layout.toast_icon_text,null);
        ((TextView)view.findViewById(R.id.message)).setText(s);
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) view.findViewById(R.id.parent_view)).setCardBackgroundColor(context.getResources().getColor(R.color.red_600));
        toast.setView(view);
        toast.show();

    }

    public void toastIconSuccess(String s) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = layoutInflater.inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(s);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(context.getResources().getColor(R.color.green_500));

        toast.setView(custom_view);
        toast.show();
    }

}

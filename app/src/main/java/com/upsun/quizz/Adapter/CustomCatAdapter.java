package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.R;

import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 02,September,2020
 */
public class CustomCatAdapter extends ArrayAdapter<AddQuizCategoryModel> {
   List<AddQuizCategoryModel> list;
    Activity activity;
    Context context;

    public CustomCatAdapter(@NonNull Context context,  List<AddQuizCategoryModel> list) {
        super(context,android.R.layout.simple_list_item_1);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
            convertView = LayoutInflater.from(activity).inflate(R.layout.row_spinner, parent, false);
        AddQuizCategoryModel model=list.get(position);
                TextView txtTitle = (TextView) convertView.findViewById(R.id.tv_name);
//        txtTitle.setText(model.getQuizCategory());
        return convertView;
    }

    @Nullable
    @Override
    public AddQuizCategoryModel getItem(int position) {
        return getItem(position);
    }
}

package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 04,September,2020
 */
public class SpinnerAdapter extends BaseAdapter {
    Context c;
    ArrayList<AddQuizCategoryModel> objects;

    public SpinnerAdapter(Context context, ArrayList<AddQuizCategoryModel> objects) {
        super();
        this.c = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        AddQuizCategoryModel cur_obj = objects.get(position);
        LayoutInflater inflater = ((Activity) c).getLayoutInflater();
        View row = inflater.inflate(R.layout.row_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.tv_name);
        label.setText(cur_obj.getP_name());

        return row;
    }
}

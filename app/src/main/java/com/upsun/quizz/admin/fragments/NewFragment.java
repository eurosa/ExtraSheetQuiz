package com.upsun.quizz.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.upsun.quizz.ManualRewardsActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AddInfoPage;
import com.upsun.quizz.admin.activities.AddInitialWallet;
import com.upsun.quizz.admin.activities.AddReferAmount;
import com.upsun.quizz.admin.activities.AdminAllQuizActivity;
import com.upsun.quizz.admin.activities.AllInsructionActivity;
import com.upsun.quizz.admin.activities.AllPointsActivity;
import com.upsun.quizz.admin.activities.AllProductActivity;
import com.upsun.quizz.admin.activities.AllQuizCategoryActivity;
import com.upsun.quizz.admin.activities.EnqueriesActivity;
import com.upsun.quizz.admin.activities.PointsAdminActivity;
import com.upsun.quizz.admin.activities.Product_requests;
import com.upsun.quizz.admin.activities.WithdrawRequests;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFragment extends Fragment implements View.OnClickListener {
    CardView card_add_quiz ,card_add_info ,card_users,card_questions,card_ins,card_refer_amount,card_wallet,card_withdraw ,card_add_points,card_enq
            ,card_manual_rew,card_manual_points,card_add_category,card_add_product,product_requests;
    ImageView iv_back ;
    TextView txt_date ;

    public NewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new, container, false);

        card_questions = view.findViewById(R.id.card_questions);
        card_users = view.findViewById(R.id.card_users);
        card_ins = view.findViewById(R.id.card_ins);
        card_add_quiz= view.findViewById(R.id.card_add_quiz);
        card_add_info= view.findViewById(R.id.card_add_info);
        card_wallet= view.findViewById(R.id.card_wallet);
        card_withdraw= view.findViewById(R.id.card_withdraw);
        card_add_points=view.findViewById(R.id.card_add_points);
        card_manual_rew=view.findViewById(R.id.card_manual_rew);
        card_manual_points=view.findViewById(R.id.card_manual_points);
        card_add_category=view.findViewById(R.id.card_add_category);
        card_add_product=view.findViewById(R.id.card_add_product);
        product_requests=view.findViewById(R.id.product_request);
        card_enq=view.findViewById(R.id.card_enq);
        txt_date = view.findViewById(R.id.txt_date);
        iv_back = view.findViewById(R.id.iv_back);
        card_refer_amount = view.findViewById(R.id.card_refer_amount);
        card_refer_amount.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        card_add_info.setOnClickListener(this);
        card_add_quiz.setOnClickListener(this);
        card_users.setOnClickListener(this);
        card_questions.setOnClickListener(this);
        card_ins.setOnClickListener(this);
        card_wallet.setOnClickListener(this);
        card_withdraw.setOnClickListener(this);
        card_add_points.setOnClickListener(this);
        card_enq.setOnClickListener(this);
        card_manual_rew.setOnClickListener(this);
        card_manual_points.setOnClickListener(this);
        card_add_category.setOnClickListener(this);
        card_add_product.setOnClickListener(this);
        product_requests.setOnClickListener(this);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        txt_date.setText(format.format(date));
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Fragment fm = null;
        if (id == R.id.card_add_info)
        {

            Intent intent = new Intent(getActivity(), AddInfoPage.class);
            startActivity(intent);
        }
        else if (id == R.id.card_add_quiz)
        {
//            fm = new AddQuizFragment();
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//
//            fragmentManager.beginTransaction()
//                    .replace( R.id.container_admin,fm)
//                    .addToBackStack(null)
//                    .commit();
            Intent intent = new Intent(getActivity(), AdminAllQuizActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.card_users)
        {
            fm = new AllUsersFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace( R.id.container_admin,fm)
                    .addToBackStack(null)
                    .commit();

        }
        else if (id == R.id.card_questions)
        {
//
            fm = new AllCategoriesFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace( R.id.container_admin,fm)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.card_ins)
        {
            Intent intent = new Intent(getActivity(), AllInsructionActivity.class);
            startActivity(intent);
        } else if (id == R.id.card_wallet)
        {
            Intent intent = new Intent(getActivity(), AddInitialWallet.class);
            startActivity(intent);

        }
        else if (id == R.id.card_refer_amount)
        {
            Intent intent = new Intent(getActivity(), AddReferAmount.class);
            startActivity(intent);
        }
        else if (id == R.id.card_withdraw)
        {
            Intent intent = new Intent(getActivity(), WithdrawRequests.class);
            startActivity(intent);
        }
        else if (id == R.id.iv_back)
        {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
        else if (id == R.id.card_add_points)
        {
            Intent intent = new Intent(getActivity(), AllPointsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.card_enq)
        {
            Intent intent = new Intent(getActivity(), EnqueriesActivity.class);
            startActivity(intent);

        }  else if(id == R.id.card_manual_rew)
        {
            Intent intent = new Intent(getActivity(), ManualRewardsActivity.class);
            startActivity(intent);

        }  else if(id == R.id.card_manual_points)
        {
            Intent intent = new Intent(getActivity(), PointsAdminActivity.class);
            startActivity(intent);

        }else if(id == R.id.card_add_category){
            Intent intent = new Intent(getActivity(), AllQuizCategoryActivity.class);
            startActivity(intent);
        }else if(id == R.id.card_add_product){
            Intent intent = new Intent(getActivity(), AllProductActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.product_request){
            Intent intent = new Intent(getActivity(), Product_requests.class);
            startActivity(intent);
        }
    }
}

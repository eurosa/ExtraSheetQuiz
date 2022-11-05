package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.UserAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {

    ImageView iv_back;
    TextView tv_title;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    RelativeLayout rel_no_items;
    RecyclerView rv_users;
    DatabaseReference user_ref;
    ArrayList<UserModel> list;
    UserAdapter userAdapter;
    Module module;

    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v) {
        iv_back = (ImageView) v.findViewById(R.id.iv_back);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        rel_no_items = (RelativeLayout) v.findViewById(R.id.rel_no_items);
        rv_users = (RecyclerView) v.findViewById(R.id.rv_users);
        toastMsg = new ToastMsg(getActivity());
        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        list = new ArrayList<>();
        module = new Module(getActivity());
        user_ref = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (NetworkConnection.connectionChecking(getActivity())) {
            getAllUsers();
        } else {
            module.noConnectionActivity();
        }

    }

    private void getAllUsers() {
        loadingBar.show();
        list.clear();
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel model = snapshot.getValue(UserModel.class);

                        if (model.getName() == null || model.getName().equals("")) {

                        } else
                            list.add(model);
                    }
                    rv_users.setVisibility(View.VISIBLE);
                    rel_no_items.setVisibility(View.GONE);
                    rv_users.setLayoutManager(new LinearLayoutManager(getActivity()));
                    userAdapter = new UserAdapter(getActivity(), list);
                    rv_users.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                } else {

                    rv_users.setVisibility(View.GONE);
                    rel_no_items.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

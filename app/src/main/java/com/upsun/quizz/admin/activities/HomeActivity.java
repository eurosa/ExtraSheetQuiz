package com.upsun.quizz.admin.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.fragments.AdminDashboradFragment;
import com.upsun.quizz.admin.fragments.AdminProfileFragment;
import com.upsun.quizz.admin.fragments.NewFragment;

public class HomeActivity extends AppCompatActivity {

    Activity ctx=HomeActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.icons8_registration_128px));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.icons8_add_100px));
        spaceNavigationView.setCentreButtonSelectable(true);
        spaceNavigationView.setCentreButtonSelected();
        AdminDashboradFragment fm=new AdminDashboradFragment();
        final Bundle bundle=new Bundle();
        addFragment(fm,bundle);




        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                AdminDashboradFragment fm=new AdminDashboradFragment();
                Bundle bundle1=new Bundle();
                loadFragment(fm,bundle);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

                Fragment fm=null;
                if(itemIndex == 0)
                {
                    fm=new AdminProfileFragment();
                }
                else if(itemIndex==1)
                {
                    fm=new NewFragment();
                }
                Bundle bundle=new Bundle();
                loadFragment(fm,bundle);

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
//                AdminDashboradFragment fm=new AdminDashboradFragment();
//                Bundle bundle1=new Bundle();
//                loadFragment(fm,bundle);

            }
        });
    }

    public void addFragment(Fragment fm, Bundle args)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fm.setArguments(args);
        fragmentManager.beginTransaction()
                .add( R.id.container_admin,fm)
                .addToBackStack(null)
                .commit();
    }
    public void loadFragment(Fragment fm,Bundle args)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fm.setArguments(args);
        fragmentManager.beginTransaction()
                .replace( R.id.container_admin,fm)
                .addToBackStack(null)
                .commit();
    }


}

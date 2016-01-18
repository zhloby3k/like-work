package com.example.gbyakov.likework;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.gbyakov.likework.data.LikeWorkContract;
import com.example.gbyakov.likework.fragments.CallItemFragment;
import com.example.gbyakov.likework.fragments.CallsListFragment;
import com.example.gbyakov.likework.fragments.OrderItemFragment;
import com.example.gbyakov.likework.fragments.OrdersListFragment;
import com.example.gbyakov.likework.fragments.RecordItemFragment;
import com.example.gbyakov.likework.fragments.RecordsListFragment;
import com.example.gbyakov.likework.fragments.RecordsTabFragment;
import com.example.gbyakov.likework.sync.LikeWorkSyncAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OrdersListFragment.OnItemSelectedListener,
        RecordsListFragment.OnItemSelectedListener,
        CallsListFragment.OnItemSelectedListener{

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportActionBar() != null) {
                    int bseCount = getSupportFragmentManager().getBackStackEntryCount();
                    if (bseCount>0) {
                        mDrawerToggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getSupportFragmentManager().popBackStack();
                            }
                        });
                    } else {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        mDrawerToggle.setDrawerIndicatorEnabled(true);
                    }
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        int bseCount = mFragmentManager.getBackStackEntryCount();

        if (id == R.id.nav_kpi) {
            LikeWorkSyncAdapter.syncImmediately(this);
        } else if (id == R.id.nav_records) {
            if (bseCount>0) mFragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new RecordsTabFragment()).commit();
        } else if (id == R.id.nav_orders) {
            if (bseCount>0) mFragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new OrdersListFragment()).commit();
        } else if (id == R.id.nav_calls) {
            if (bseCount>0) mFragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new CallsListFragment()).commit();
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnItemSelected(Uri itemUri) {
        String uriType = this.getContentResolver().getType(itemUri);
        if (uriType.equals(LikeWorkContract.OrderEntry.CONTENT_ITEM_TYPE)) {
            Bundle args = new Bundle();
            args.putParcelable(OrderItemFragment.ORDER_URI, itemUri);

            OrderItemFragment fOrderItem = new OrderItemFragment();
            fOrderItem.setArguments(args);

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, fOrderItem)
                    .addToBackStack(null).commit();
        } else if (uriType.equals(LikeWorkContract.RecordEntry.CONTENT_ITEM_TYPE)) {
            Bundle args = new Bundle();
            args.putParcelable(RecordItemFragment.RECORD_URI, itemUri);

            RecordItemFragment fRecordItem = new RecordItemFragment();
            fRecordItem.setArguments(args);

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, fRecordItem)
                    .addToBackStack(null).commit();
        } else if (uriType.equals(LikeWorkContract.CallEntry.CONTENT_ITEM_TYPE)) {
            Bundle args = new Bundle();
            args.putParcelable(CallItemFragment.CALL_URI, itemUri);

            CallItemFragment fCallItem = new CallItemFragment();
            fCallItem.setArguments(args);

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, fCallItem)
                    .addToBackStack(null).commit();
        }
    }
}

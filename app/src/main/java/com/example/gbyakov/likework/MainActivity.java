package com.example.gbyakov.likework;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.gbyakov.likework.data.LikeWorkContract;
import com.example.gbyakov.likework.fragments.CallItemFragment;
import com.example.gbyakov.likework.fragments.CallsListFragment;
import com.example.gbyakov.likework.fragments.KpiGridFragment;
import com.example.gbyakov.likework.fragments.OrderItemFragment;
import com.example.gbyakov.likework.fragments.OrdersListFragment;
import com.example.gbyakov.likework.fragments.PreferencesFragment;
import com.example.gbyakov.likework.fragments.RecordItemFragment;
import com.example.gbyakov.likework.fragments.RecordsListFragment;
import com.example.gbyakov.likework.fragments.RecordsTabFragment;
import com.example.gbyakov.likework.gcm.RegistrationIntentService;
import com.example.gbyakov.likework.sync.LikeWorkSyncAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OrdersListFragment.OnItemSelectedListener,
        RecordsListFragment.OnItemSelectedListener,
        CallsListFragment.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener{

    private final String LOG_TAG = LoginActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    FragmentManager mFragmentManager;
    ActionBarDrawerToggle mDrawerToggle;
    public FloatingActionMenu fabMenu;
    public SwipeRefreshLayout mSwipeRefresh;

    private BroadcastReceiver syncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSwipeRefresh.setRefreshing(false);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle params = intent.getExtras();
        String id_1c = params.getString("id_1c");

        int bseCount = mFragmentManager.getBackStackEntryCount();
        if (bseCount>0) mFragmentManager.popBackStack();

        Bundle args = new Bundle();
        args.putParcelable(OrderItemFragment.ORDER_URI, LikeWorkContract.OrderEntry.buildOrderID1C(id_1c));

        OrderItemFragment fOrderItem = new OrderItemFragment();
        fOrderItem.setArguments(args);

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new OrdersListFragment()).commit();

        fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, fOrderItem)
                .addToBackStack(null).commit();

        fabMenu.showMenuButton(true);
    }

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
                    if (bseCount > 0) {
                        mDrawerToggle.setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!fabMenu.isMenuButtonHidden()) fabMenu.hideMenuButton(true);
                                getSupportFragmentManager().popBackStack();
                            }
                        });
                    } else {
                        if (!fabMenu.isMenuButtonHidden()) fabMenu.hideMenuButton(true);
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
        View headerView = navigationView.inflateHeaderView(R.layout.left_drawer_header);

        Intent intent = getIntent();

        String userName = intent.getStringExtra("username");
        String userunit = intent.getStringExtra("userunit");

        TextView vUserName = (TextView) headerView.findViewById(R.id.user_name);
        vUserName.setText(userName);

        TextView vUserUnit = (TextView) headerView.findViewById(R.id.user_unit);
        vUserUnit.setText(userunit);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new KpiGridFragment()).commit();

        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fabMenu.hideMenuButton(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View toolbarShadow = findViewById(R.id.toolbar_shadow);
            toolbarShadow.setVisibility(View.GONE);
        }
        createCustomAnimation();

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(this);

        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (checkPlayServices()) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            boolean sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
            if (!sentToken) {
                intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

        String id_1c = intent.getStringExtra("id_1c");
        if (id_1c != null) onNewIntent(intent);

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
            if (bseCount>0) mFragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new KpiGridFragment()).commit();
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
            if (bseCount>0) mFragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new PreferencesFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(syncFinishedReceiver, new IntentFilter(LikeWorkSyncAdapter.SYNC_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncFinishedReceiver);
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

            fabMenu.showMenuButton(true);

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

            fabMenu.showMenuButton(true);

        }
    }

    private void createCustomAnimation() {

        final FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.fab_menu);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menu.getMenuIconView().setImageResource(menu.isOpened()
                        ? R.drawable.ic_call : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menu.setIconToggleAnimatorSet(set);
    }

    @Override
    public void onRefresh() {
        LikeWorkSyncAdapter.syncImmediately(this);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}

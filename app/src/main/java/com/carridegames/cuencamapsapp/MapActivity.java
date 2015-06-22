package com.carridegames.cuencamapsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity
      implements
      ConnectionCallbacks,
      OnConnectionFailedListener,
      LocationListener,
      OnMyLocationButtonClickListener,
      OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private Resources resources;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
          .setInterval(5000)         // 5 seconds
          .setFastestInterval(16)    // 16ms = 60fps
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    String[] menutitles;
    // nav drawer title
    private ActionBarDrawerToggle mDrawerToggle;
    private UiSettings mUiSettings;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView expListView;
    public static MapHandler mapHandler;
    private static HashMap<Integer, Integer> routeImages;
    static {
        routeImages = new HashMap<Integer, Integer>();
        routeImages.put(3, R.drawable.route3);
        routeImages.put(5, R.drawable.route5);
        routeImages.put(7, R.drawable.route7);
        routeImages.put(8, R.drawable.route8);
        routeImages.put(15, R.drawable.route15);
        routeImages.put(20, R.drawable.route20);
        routeImages.put(50, R.drawable.route50);
    }
    private static ArrayList<boolean[]> checkedChildren;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private static SparseArray<boolean[]> shownRoutes;
    private static boolean isVerMakers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLanguage();

        setContentView(R.layout.map_activity);

        checkedChildren = new ArrayList<boolean[]>() {{
            for (int i = 0; i < routeImages.size(); i++) {
                add(new boolean[2]);
            }
        }};

        initialize();

    }

    public void initialize() {
        resources = getResources();

        // Load Ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
              getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_layout, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
              .addApi(LocationServices.API)
              .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
              .build();

        // Initialize ExpandableListView
        menutitles = getResources().getStringArray(R.array.titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        List<RowItem> rowItems = new ArrayList<RowItem>();
        final List<String> groupList = new ArrayList<String>();
        for (String menutitle : menutitles) {
            RowItem items = new RowItem(menutitle);
            rowItems.add(items);
            groupList.add(menutitle);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        expListView = (ExpandableListView) findViewById(R.id.expandable_list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(this, groupList);
        expListAdapter.checkedChildren = this.checkedChildren;
        expListView.setAdapter(expListAdapter);
        for (int i = 0; i < checkedChildren.size(); i++) {
            if(checkedChildren.get(i)[0] || checkedChildren.get(i)[1]) {
                expListView.expandGroup(i);
            }
        }
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return true;
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(expListView.getWidth() - 80, expListView.getWidth() - 10);
        } else {
            expListView.setIndicatorBoundsRelative(expListView.getWidth() - 80, expListView.getWidth() - 10);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);

        mUiSettings = map.getUiSettings();

        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-2.901270, -79.003542), 13));
        map.setInfoWindowAdapter(new MyInfoWindowAdapter());
        boolean doesMapHandlerExist = mapHandler != null;
        mapHandler = new MapHandler(map, this);
        if (doesMapHandlerExist && shownRoutes != null) {
            mapHandler.reDrawShownRoutes(shownRoutes);
            mapHandler.toggleMarkers(isVerMakers);
        }
        mMap = map;
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
              mGoogleApiClient,
              REQUEST,
              this);  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider
            String provider = manager.getBestProvider(criteria, true);

            Location location = manager.getLastKnownLocation(provider);
            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15);
                mMap.animateCamera(cameraUpdate);
            } else {
                String toastText = resources.getString(R.string.searching_location);
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(resources.getString(R.string.gps_prompt))
              .setCancelable(false)
              .setPositiveButton(resources.getString(R.string.prompt_yes), new DialogInterface.OnClickListener() {
                  public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                      startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                  }
              })
              .setNegativeButton(resources.getString(R.string.prompt_no), new DialogInterface.OnClickListener() {
                  public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                      dialog.cancel();
                  }
              });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(expListView);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during   * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        shownRoutes = mapHandler.shownRoutes;
        isVerMakers = mapHandler.verMarkers;
        final SparseArray<boolean[]> shownRoutes = mapHandler.shownRoutes;
        checkedChildren = new ArrayList<boolean[]>() {{
            for (int i = 0; i < shownRoutes.size(); i++) {
                add(shownRoutes.get(shownRoutes.keyAt(i)));
            }
        }};

        setLanguage();
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.map_activity_horizontal);
        } else {
            setContentView(R.layout.map_activity);
        }

        initialize();

        ((CheckBox)findViewById(R.id.show_routes)).setChecked(isVerMakers);

        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    class MyInfoWindowAdapter implements InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int imageSize = Math.min(size.x, size.y) / 20;

            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            LinearLayout list = (LinearLayout) myContentsView.findViewById(R.id.image_list);
            list.removeAllViews();
            Integer[] routeList = mapHandler.mapResources.findRoutesFromStopId(Integer.parseInt(marker.getSnippet()));
            for (int i = 0; i < routeList.length; i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageResource(routeImages.get(routeList[i]));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageSize, imageSize);
                imageView.setLayoutParams(lp);
                list.addView(imageView);
            }

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    public void onShowStops(View view) {
        CheckBox cb = (CheckBox) view;
        mapHandler.toggleMarkers(cb.isChecked());
        getSharedPreferences(MainActivity.PREFERENCES, 0).edit().putBoolean("shouldShowRoutes", cb.isChecked()).apply();
    }

    private void setLanguage() {
        // Set language
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFERENCES, 0);
        Locale locale = new Locale(preferences.getString("language", "en"), preferences.getString("locale", "US"));
        Locale.setDefault(locale);
        Log.i("MAP ACTIVITY", "Language" + Locale.getDefault().getLanguage() + ", locale" + Locale.getDefault().getCountry());
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}



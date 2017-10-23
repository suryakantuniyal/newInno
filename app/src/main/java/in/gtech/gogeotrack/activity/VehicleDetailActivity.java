package in.gtech.gogeotrack.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import in.gtech.gogeotrack.R;
import in.gtech.gogeotrack.api.APIServices;
import in.gtech.gogeotrack.network.DetailResponseCallback;
import in.gtech.gogeotrack.utils.URLContstant;

/**
 * Created by silence12 on 21/6/17.
 */

public class   VehicleDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = VehicleDetailActivity.class.getSimpleName();

    public static VehicleDetailActivity vehicleDetailActivity;
    GoogleMap googleMap;
    private TextView name_tv, positionId_tv, uniqueId_tv, status_tv, lastUpdate_tv, category_tv, contact_tv,
            speed_tv, distance_tv, timedated;
    private ImageView projImageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressDialog progressDialog;
    private String nameString, positionIdString, uniqueIdString, lastUpdatetString, categoryString, statusString,
            contactString, diffString,address;
    private int id, positionId,deviceId;
    private Button homeButton;
    private MarkerOptions markerOptions;
    Double speed,latitute,longitute,distance_trav;
    private CameraPosition cameraPosition;
    SharedPreferences mSharedPreferences;
    //8189 12.2.13
    //7162 12.5.11

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vehicle);

       vehicleDetailActivity = this;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("DeviceDetail");
        initViews();
        mSharedPreferences = getSharedPreferences(URLContstant.PREFERENCE_NAME, Context.MODE_PRIVATE);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Wait a moment...");
        progressDialog.show();
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state.name().equals(State.COLLAPSED.name())) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else if (state.name().equals(State.IDLE.name())) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });
        callDetailRequest();
        collapsingToolbarLayout.setTitle(nameString);
    }

    public void callDetailRequest() {

        Intent getIntent = getIntent();

        id = getIntent.getIntExtra("id", -1);
        nameString = getIntent.getStringExtra("name");
        uniqueIdString = getIntent.getStringExtra("uid");

      //  deviceId = Integer.parseInt(uniqueIdString);

        Log.d("uniq",String.valueOf(deviceId));

        positionId = getIntent.getIntExtra("pid", -1);
      //  deviceId = getIntent.getIntExtra("deviceId",-1);
        Log.d("DEVICEID",String.valueOf(deviceId));
        //007835051035

        //positionId = 9648368 getId =?
        //get id by positionId

        lastUpdatetString = getIntent.getStringExtra("lastupdate");
        statusString = getIntent.getStringExtra("status");
        categoryString = getIntent.getStringExtra("category");
        contactString = getIntent.getStringExtra("contact");
        diffString = getIntent.getStringExtra("diff");
        address = getIntent.getStringExtra("address");
        latitute = getIntent.getDoubleExtra("lat",0.0);
        longitute = getIntent.getDoubleExtra("long",0.0);
        speed = getIntent.getDoubleExtra("speed",0.0);
        distance_trav = getIntent.getDoubleExtra("distance",0.0);
        Log.d("Distance", String.valueOf(distance_trav));

        progressDialog.dismiss();
        name_tv.setText(nameString);
        if (address.equals("null")) {
            address = "Loading...";
        }
        positionId_tv.setText(address);

        SupportMapFragment supportMapFragment;
      //  if (Build.VERSION.SDK_INT < 21)
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map25)).getMap();

       /* if (Build.VERSION.SDK_INT >= 22){
         Toast.makeText(getApplicationContext(), "APK greater than 22", Toast.LENGTH_LONG).show();

     }*/
      //  googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2)).getMap();

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(10,10,10,20);

//                CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629));
//                googleMap.moveCamera(point);
        markerOptions = new MarkerOptions().position(new LatLng(latitute,longitute)).title(address);
        cameraPosition = new CameraPosition.Builder().target(new LatLng(latitute,longitute)).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if(categoryString.equals("person")){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_punch_person));
        }
        if (uniqueIdString.equals("007835051035")){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.motobikes));
        }
       else {
            if (statusString.equals("online")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.greentruck));
            } else if (statusString.equals("offline")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redtruck));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_truck_med));
            }
        }
        googleMap.addMarker(markerOptions);
        uniqueId_tv.setText(uniqueIdString);
       // lastUpdate_tv.setText();
        lastUpdate_tv.setText(lastUpdatetString);
        status_tv.setText(statusString);
        category_tv.setText(categoryString);
        speed_tv.setText(String.valueOf(speed));
        timedated.setText(diffString);
        distance_tv.setText(String.valueOf(distance_trav));

    }

    private void initViews() {
        name_tv = (TextView) findViewById(R.id.project_name_tv);
        positionId_tv = (TextView) findViewById(R.id.positionId_tv);
        uniqueId_tv = (TextView) findViewById(R.id.uniqueid_tv);
        lastUpdate_tv = (TextView) findViewById(R.id.date_tv);
        status_tv = (TextView) findViewById(R.id.status_tv);
        category_tv = (TextView) findViewById(R.id.category_tv);
        speed_tv = (TextView) findViewById(R.id.speed_tv);
        distance_tv = (TextView) findViewById(R.id.distancecover_tv);
        timedated = (TextView) findViewById(R.id.diff_tv);
        homeButton = (Button) findViewById(R.id.gohome_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void uploadIndividualData(){
        APIServices.GetVehicleDetailById(VehicleDetailActivity.this, deviceId, new DetailResponseCallback() {
            @Override
            public void OnResponse(JSONArray result) {
              Log.d("NEW_RESPONSE",String.valueOf(result));
                try {
                    lastUpdate_tv.setText(result.getJSONObject(0).getString("deviceTime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               /* try {
                    int id = result.getJSONObject(0).getInt("deviceId");
                    Log.d("helloId",String.valueOf(id));
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
               // SessionHandler.updateSnessionHandler(getBaseContext(), result, mSharedPreferences);


            }
        });
        //initViews();
       // callDetailRequest();
    }

    public void updateWithNewId(){
       // APIServices.GetVehicleDetailById();

          }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callDetailRequest();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


    }

    public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

        public enum State {
            EXPANDED,
            COLLAPSED,
            IDLE
        }
    }

}

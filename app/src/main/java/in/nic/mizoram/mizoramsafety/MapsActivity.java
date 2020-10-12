package in.nic.mizoram.mizoramsafety;
//pp
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Vibrator;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap,mMap2;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String firebaseUser;
    TextView fullLocationText;
    public String contact1;
    public  String contact2;
    CoordinatorLayout mylayout;

   //Location tan
    LocationManager locationManager;
   private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;

    private double currentLatitude;
    private double currentLongitude;
    Button btnNearestSMS,btnSOS,btnReserve,btnexitList,btnUpdate;
    ListView listView;
    String whenReport;
    List<String> list = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmainmenu);
       // mylayout=findViewById(R.id.mylayout);
        checkSMSPermission();

       // float        heightscr= Resources.getSystem().getDisplayMetrics().ydpi;

       // Toast.makeText(getApplicationContext(),"Height: "+ heightscr,Toast.LENGTH_LONG).show();

        btnUpdate=findViewById(R.id.btnUpdate);
        //setSupportActionBar(toolbar);

        GPSTracker gpsEnabled=new GPSTracker(this);

       /* if(gpsEnabled.isGPSEnabled==false || gpsEnabled.isNetworkEnabled==false )
        {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

        }*/

        if (ConnectivityCheck.isConnectedToNetwork(this)) {
            //Show the connected screen
        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Scanning Network Connectivity")
                    .setCancelable(false)
                    .setIcon(R.drawable.iconic)
                    .setMessage("Please Enable data connectivity for Better Performance !")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                           // finish();
                        }
                    })

                    .create()
                    .show();

        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        firebaseFirestore=FirebaseFirestore.getInstance();
       firebaseAuth= FirebaseAuth.getInstance();

      firebaseUser=firebaseAuth.getCurrentUser().getPhoneNumber().toString();
       // checkEmergencyContact();
        fullLocationText=findViewById(R.id.fullLocation);
        fullLocationText.setVisibility(View.INVISIBLE);
       // btnNearestSMS=findViewById(R.id.btnSMS);
        btnSOS=findViewById(R.id.btnSOS);
        btnReserve=findViewById(R.id.btnRes);
       // btnexitList=findViewById(R.id.btnCloseList);
        listView=findViewById(R.id.listView);
        listView.setVisibility(View.INVISIBLE);
        //btnexitList.setVisibility(View.INVISIBLE);
       /* btnexitList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.INVISIBLE);
                btnexitList.setVisibility(View.INVISIBLE);
            }
        });*/
// btnNearestSMS.setVisibility(View.INVISIBLE);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Vibrator vibrator = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);
                startActivity( new Intent(MapsActivity.this,UpdateContact.class));
                finish();
            }
        });

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  listView.setVisibility(View.VISIBLE);
             //   btnexitList.setVisibility(View.VISIBLE);

                final Vibrator vibrator = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);
                final ProgressDialog progressDialog= new ProgressDialog(MapsActivity.this);
                progressDialog.setTitle("Finding your Nearest Police Station");
                progressDialog.setIcon(R.drawable.iconic);
                progressDialog.setMessage("Please Wait ....");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                final GPSTracker gps=new GPSTracker(MapsActivity.this);
                final DistanceCalculate distanceCalculate = new DistanceCalculate();
                final ArrayList<String> fullLocation = new ArrayList<>();


                CollectionReference cr=firebaseFirestore.collection("InitData");
                cr.orderBy("District");
                cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            String ss="";
                            for(QueryDocumentSnapshot qs : task.getResult())
                            {
                                double dist =distanceCalculate.myDistanceinKM(gps.getLatitude(),gps.getLongitude(),qs.getDouble("Latitude"),qs.getDouble("Longitude"));
                                ss=String.valueOf(dist);
                                fullLocation.add("["+ ss +"]"+qs.getDouble("Latitude")+"^"+qs.getDouble("Longitude")+"{"+ qs.get("Contact")+"}" +"(0" + qs.get("Landline")+"#"+ qs.get("PAName")+"$"+qs.get("OCName")+"!");
                                // Toast.makeText(getApplicationContext(),"QQ :" + fullLocation.toString(),Toast.LENGTH_LONG).show();
                            }

                            Collections.sort(fullLocation,Collections.reverseOrder(new Comparator<String>() {
                                @Override
                                public int compare(String s1, String s2) {

                                    String left=s1.toString();
                                    String rights=s2.toString();
                                    int index1=left.lastIndexOf("[");
                                    int index2=left.lastIndexOf("]");
                                    String firstDistance=left.substring(index1+1,index2);
                                    String secondDistance=rights.substring(index1+1,index2);

                                    return firstDistance.compareTo(secondDistance);

                                }
                            }));

                            for(String i:fullLocation)
                            {


                                fullLocationText.setText(i);

                                //Toast.makeText(getApplicationContext(),fullLocationText.getText().toString(),Toast.LENGTH_LONG).show();
                            }

                            progressDialog.dismiss();
                            final String locationText=fullLocationText.getText().toString();


                            final String latitudePS=locationText.substring(locationText.lastIndexOf("]")+1,locationText.lastIndexOf("^"));

                            final String longitudePS=locationText.substring(locationText.lastIndexOf("^")+1,locationText.lastIndexOf("{"));

                            final String policeStation=locationText.substring(locationText.lastIndexOf("#")+1,locationText.lastIndexOf("$"));

                            final String ocName=locationText.substring(locationText.lastIndexOf("$")+1,locationText.lastIndexOf("!"));


                            final String contactPS=locationText.substring(locationText.lastIndexOf("{")+1,locationText.lastIndexOf("}"));

                            final String landlinePS=locationText.substring(locationText.lastIndexOf("(")+1,locationText.lastIndexOf("#"));
                            new androidx.appcompat.app.AlertDialog.Builder( MapsActivity.this)
                                    .setTitle("Nearest Police Station from your Current Location is : ")
                                    .setMessage("  "+policeStation+" , and Your O/C is : "+ocName)

                                    .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Intent call = new Intent(Intent.ACTION_DIAL);
                                            call.setData(Uri.parse("tel:"+landlinePS));
                                            startActivity(call);

                                        }
                                    })
                                    .setNegativeButton("Send-SMS", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            GPSTracker gps=new GPSTracker(MapsActivity.this);
                                            Intent sosIntent = new Intent(Intent.ACTION_VIEW);
                                            sosIntent.setData(Uri.parse("smsto:"));
                                            sosIntent.putExtra("address","+91"+contactPS);
                                            sosIntent.putExtra("sms_body","I NEED HELP. My Location is : https://maps.google.com/?q="+gps.getLatitude()+","+gps.getLongitude());
                                            sosIntent.setType("vnd.android-dir/mms-sms");
                                            if (sosIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(sosIntent);
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Error ",Toast.LENGTH_LONG).
                                                        show();
                                            }

                                        }
                                    })
                                    .setNeutralButton("Get-Direction", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            GPSTracker gps=new GPSTracker(MapsActivity.this);

                                            String mynewlocation="google.navigation:q="+ latitudePS +","+longitudePS;
                                            // Toast.makeText(getApplicationContext(),"Lati :" +latitudePS ,Toast.LENGTH_LONG).show();

                                            Uri gmmIntentUri = Uri.parse(mynewlocation);
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                                                mapIntent.setPackage("com.google.android.apps.maps");
                                            }
                                            startActivity(mapIntent);


                                        }
                                    })
                                    .create()
                                    .show();


                        }

                    }
                });



            }
        });




        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Send SOS to all Police Station and Relatives being registered as Emergency Contact

               // String OCName,String OCContact,String SmsContent,String Action,String Status,
                  //      String SenderNos,String SendersEmergencyNo,String DateTime,String PSName,double Latitude,double Longitude)

                final Vibrator vibrator = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);
                GPSTracker gps=new GPSTracker(MapsActivity.this);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy (HHmm");
                Date date = new Date();
                whenReport=dateFormat.format(date);
                IncomingHelpModel incomingHelpModel = new IncomingHelpModel("Lucy","aL"
                ,"google.com","A","1m", firebaseUser,
                        "L",whenReport + "  Hrs)","",gps.getLatitude(),gps.getLongitude());
                 firebaseFirestore.collection("IncomingHelp").add(incomingHelpModel);

                CollectionReference cr= firebaseFirestore.collection("Users").document(firebaseUser).collection("EmergencyContact");
                cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                       GPSTracker gps = new GPSTracker(MapsActivity.this);
                                                       if (task.isSuccessful()) {

                                                           for (QueryDocumentSnapshot qs : task.getResult()) {
                                                               contact1 = qs.get("onecontact").toString();
                                                               contact2 = qs.get("twocontact").toString();
                                                              // Toast.makeText(getApplicationContext(),"Conact"+contact1+contact2,Toast.LENGTH_LONG).show();
                                                           }

                                                           Intent sosIntent = new Intent(Intent.ACTION_VIEW);
                                                           sosIntent.setData(Uri.parse("smsto:"));
                                                           sosIntent.putExtra("address",contact1+";"+contact2);// can add ERSS Mobiles Nos also for their online monitoring
                                                           sosIntent.putExtra("sms_body","I NEED HELP! My Location is : https://maps.google.com/?q="+gps.getLatitude()+","+gps.getLongitude());
                                                           sosIntent.setType("vnd.android-dir/mms-sms");
                                                           if (sosIntent.resolveActivity(getPackageManager()) != null) {
                                                               startActivity(sosIntent);
                                                           }
                                                           else
                                                           {
                                                               Toast.makeText(getApplicationContext(),"Error ",Toast.LENGTH_LONG).
                                                                       show();
                                                           }
                                                           Intent call = new Intent(Intent.ACTION_DIAL);
                                                           call.setData(Uri.parse("tel:112"));
                                                           startActivity(call);
                                                       }
                                                   }
                                               });



            }
        });

       /* btnNearestSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




            }
        });*/



        GPSTracker gps=new GPSTracker(this);
       // Toast.makeText(getApplicationContext(),"Lat :" + gps.getLatitude() + " Long : "+ gps.getLongitude() ,Toast.LENGTH_LONG).show();

        // getting Location

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
      //  fragView =  findViewById(R.id.fragment);
      //  fragView.setVisibility(View.INVISIBLE);
        mapFragment.getMapAsync(this);

    }

       /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final ArrayList<String> fullLocation = new ArrayList<>();
        mMap = googleMap;
        mMap2=googleMap;
        final GPSTracker gps=new GPSTracker(this);
        final DistanceCalculate distanceCalculate = new DistanceCalculate();

        // Add a marker in Sydney and move the camera
        // Adding Dynamic Locations

        CollectionReference cr = firebaseFirestore.collection("InitData");
        cr.orderBy("District");
        cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_2,android.R.id.text2,list);
                list.clear();

                if (task.isSuccessful())
                {
                  for(QueryDocumentSnapshot qs:task.getResult())
                  {
                      LatLng dynamicLocation= new LatLng(qs.getDouble("Latitude"),qs.getDouble("Longitude"));

                     double dist =distanceCalculate.myDistanceinKM(gps.getLatitude(),gps.getLongitude(),qs.getDouble("Latitude"),qs.getDouble("Longitude"));

                     // Hetah hian location details kan add ang
                     // final String testLocation="[12.001]23.899911^92.8788811{8794501007}(03892324712)";
                     fullLocation.add("["+ dist +"]"+qs.getDouble("Latitude")+"^"+qs.getDouble("Longitude")+"{8794501007}(03892324712)");

                      list.add(qs.get("PAName").toString()+ " --  " +dist);
                     // list.add
                      listView.setAdapter(arrayAdapter);
                      CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(dynamicLocation,14);
                      Marker psmarker = mMap.addMarker(new MarkerOptions().title(qs.get("PAName").toString()).position(dynamicLocation).snippet("O/C: " + qs.get("OCName")+" :"+ qs.get("Contact")));
                     // psmarker.showInfoWindow();
                      mMap.moveCamera(CameraUpdateFactory.newLatLng(dynamicLocation));
                      mMap.animateCamera(cameraUpdate1);

                  }
                }

            }
        });

        LatLng aizawl = new LatLng(gps.getLatitude(), gps.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(aizawl,14);
        Marker AizawlPS= mMap2.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(aizawl).title("You Are HERE").snippet("Tap the Red Markers for info on Police Station"));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.closebutton)));
        AizawlPS.showInfoWindow();
        mMap2.moveCamera(CameraUpdateFactory.newLatLng(aizawl));
        mMap2.animateCamera(cameraUpdate);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // fragView.setVisibility(View.INVISIBLE);

                return false;
            }
        });



        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
               // marker.hideInfoWindow();
              //  fragView.setVisibility(View.VISIBLE);
              /*  Fragment fragment = new FragmentThar();
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction transaction= fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment,fragment);
                transaction.commit();
                */
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;



    public boolean checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Request")
                        .setIcon(R.drawable.iconic)
                        .setMessage("Please Enable Location for this App..")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.SEND_SMS},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {


            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 400, 1, this);
                       // GPSTracker gpsTracker= new GPSTracker(this);
                       // gpsTracker.getLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    @Override
    public void onBackPressed() {


        new AlertDialog.Builder(this)
                .setTitle("Exiting...")
                .setIcon(R.drawable.iconic)
                .setMessage("Do you Want to EXIT ?")
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       MapsActivity.super.onBackPressed();
                       finish();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Emergency Contact kan Update Duh .. backPressed palh a ni e
                    }
                })
                .create()
                .show();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.newmenu,menu);
        //getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_logout) {

            firebaseAuth.signOut();
            startActivity(new Intent(this,NewRegistration.class));
            finish();
        }


        if (id == R.id.menu_update) {


            startActivity(new Intent(this,EmergencyContact.class));
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
   /* public void checkEmergencyContact()
    {

       firebaseUser=firebaseAuth.getCurrentUser().getPhoneNumber();
        CollectionReference cr=firebaseFirestore.collection("Users").document(firebaseUser).collection("EmergencyContact");
        cr.whereLessThan("Status",1);

    }*/

    }

package in.nic.mizoram.mizoramsafety;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CheckContact extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String fireBaseUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        fireBaseUser=firebaseAuth.getCurrentUser().getPhoneNumber().toString();

        if (ConnectivityCheck.isConnectedToNetwork(this)) {
            //Show the connected screen
        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Scanning Network Connectivity")
                    .setCancelable(false)
                    .setIcon(R.drawable.iconic)
                    .setMessage("Please enable data connectivity and try again !")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();
                        }
                    })

                    .create()
                    .show();



        }
        checkEmergencyContact();

           }


   public void checkEmergencyContact()
   {
      // Toast.makeText(getApplicationContext(),"Checking Emergency Contact",Toast.LENGTH_LONG).show();
       progressDialog= new ProgressDialog(this);
       progressDialog.setTitle("Checking Emergency Contact");
       progressDialog.setMessage("Please Wait...");
       progressDialog.setIcon(R.drawable.iconic);
       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
       progressDialog.show();

       CollectionReference cr=firebaseFirestore.collection("Users").document(fireBaseUser)
               .collection("EmergencyContact");
       cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful())
               {
                   progressDialog.dismiss();

                  QuerySnapshot doc = task.getResult();
                  if(doc.isEmpty())
                  {
                      //Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
                      startActivity(new Intent(CheckContact.this,EmergencyContact.class));
                      finish();
                  }
                  else
                  {
                      //Toast.makeText(getApplicationContext(),"Not Empty",Toast.LENGTH_LONG).show();
                      startActivity(new Intent(CheckContact.this,MapsActivity.class));
                      finish();
                  }

               }

           }
       });





       /*cr.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {

               if(task.isSuccessful())
               {
                   for(DocumentSnapshot dc:task.getResult())
                   {
                       if (dc.exists())
                       {
                           startActivity(new Intent(CheckContact.this,MapsActivity.class));
                           finish();
                       }
                       if(!dc.exists())
                       {
                           startActivity(new Intent(CheckContact.this,EmergencyContact.class));
                           finish();
                       }


                   }
               }

           }
       });*/

   }
    public static final int MY_LUCYFIC_REQUEST_CONTACT=21;

    public  boolean checkContactPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Contact Read Permission Request")
                        .setMessage("Please Allow for this App..")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CheckContact.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        MY_LUCYFIC_REQUEST_CONTACT);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_LUCYFIC_REQUEST_CONTACT);
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

            case MY_LUCYFIC_REQUEST_CONTACT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }


                    }
    }

}

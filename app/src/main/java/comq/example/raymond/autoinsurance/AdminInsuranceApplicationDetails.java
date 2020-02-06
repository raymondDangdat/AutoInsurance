package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import comq.example.raymond.autoinsurance.Model.RegisterCarModel;

public class AdminInsuranceApplicationDetails extends AppCompatActivity {
    private Toolbar insuranceApplicationToolBar;

    private DatabaseReference cars;

    private ProgressDialog dialog;
    private TextView txt_car_owner, txt_receipt, txt_pics, txt_car_make, txt_policy_type, txt_car_model, txt_use, txt_car_cost;
    private Button  btn_approve, btn_decline;
    private ImageView car_picture, img_papers;

    private String carId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_insurance_application_details);

        dialog = new ProgressDialog(this);

        cars = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("cars");

        //initialize toolBar
        insuranceApplicationToolBar = findViewById(R.id.insurance_applications_detalis_toolbar);
        setSupportActionBar(insuranceApplicationToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Car Detalis");

        txt_car_owner = findViewById(R.id.txt_car_owner);
        txt_car_make = findViewById(R.id.txt_car_make);
        txt_car_model = findViewById(R.id.txt_car_model);
        txt_policy_type = findViewById(R.id.txt_policy_type);
        txt_use = findViewById(R.id.txt_car_use);
        txt_car_cost = findViewById(R.id.txt_car_value);
        btn_approve = findViewById(R.id.btn_approve);
        btn_decline = findViewById(R.id.btn_decline);
        car_picture = findViewById(R.id.img_car_image);
        img_papers = findViewById(R.id.img_car_papers);
        txt_pics = findViewById(R.id.txt_papers);
        txt_receipt = findViewById(R.id.txt_papers);


        //get crime id from Intent
        if (getIntent() != null){
            carId = getIntent().getStringExtra("carId");

            if (!carId.isEmpty()){
                getCarDetail(carId);
            }

        }

        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineApplication();
            }
        });


    }

    private void declineApplication() {
        dialog.setMessage("Declining...");
        dialog.show();
        cars.child(carId).child("status").setValue("Declined").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                Toast.makeText(AdminInsuranceApplicationDetails.this, "Application declined!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AdminInsuranceApplicationDetails.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCarDetail(String carId) {
        cars.child(carId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RegisterCarModel registerCarModel = dataSnapshot.getValue(RegisterCarModel.class);
                txt_car_cost.setText("Value of car in Naira: " + registerCarModel.getValue());
                txt_car_make.setText("Car Make: " + registerCarModel.getMake());
                txt_car_model.setText("Car Model: " + registerCarModel.getModel());
                txt_car_owner.setText("Car Owner: " + registerCarModel.getfName() + " " + registerCarModel.getlName());
                txt_use.setText("Car Use: " + registerCarModel.getUse());
                txt_policy_type.setText("Policy Type: " + registerCarModel.getPolicyType());

                Picasso.get().load(registerCarModel.getCar_pic()).placeholder(R.drawable.benz).into(car_picture);

                final String papers_url = registerCarModel.getPapers().toString();


                if (papers_url.equals("NULL")){
                    txt_receipt.setText("No receipt uploaded yet");
                    img_papers.setVisibility(View.GONE);
                    btn_approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(AdminInsuranceApplicationDetails.this, "Sorry, you can not approve insureance without papers been uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                   img_papers.setVisibility(View.VISIBLE);
                   Picasso.get().load(registerCarModel.getPapers()).into(img_papers);
                   btn_approve.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           approveApplication();
                       }
                   });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void approveApplication() {
        dialog.setMessage("Approving...");
        dialog.show();
        cars.child(carId).child("status").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                Toast.makeText(AdminInsuranceApplicationDetails.this, "Application Approved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminInsuranceApplicationDetails.this, InsuranceApplications.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AdminInsuranceApplicationDetails.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

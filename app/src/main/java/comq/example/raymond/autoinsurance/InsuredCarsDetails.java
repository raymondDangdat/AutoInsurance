package comq.example.raymond.autoinsurance;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import comq.example.raymond.autoinsurance.Model.RegisterCarModel;

public class InsuredCarsDetails extends AppCompatActivity {
    private Toolbar insuredCarsToolBar;

    private DatabaseReference cars;

    private TextView txt_car_owner, txt_car_make, txt_policy_type, txt_car_model, txt_use, txt_car_cost;
    private Button btn_upload_papers, btn_status, btn_make_payment;
    private ImageView car_picture;

    private String carId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insured_cars_details);


        cars = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("cars");
        //initialize toolBar
        insuredCarsToolBar = findViewById(R.id.insured_cars_detalis_toolbar);
        setSupportActionBar(insuredCarsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Car Detalis");

        txt_car_owner = findViewById(R.id.txt_car_owner);
        txt_car_make = findViewById(R.id.txt_car_make);
        txt_car_model = findViewById(R.id.txt_car_model);
        txt_policy_type = findViewById(R.id.txt_policy_type);
        txt_use = findViewById(R.id.txt_car_use);
        txt_car_cost = findViewById(R.id.txt_car_value);
        btn_upload_papers = findViewById(R.id.btn_upload_papers);
        btn_make_payment = findViewById(R.id.btn_make_payment);
        btn_status = findViewById(R.id.btn_status);
        car_picture = findViewById(R.id.img_car_image);



        //get crime id from Intent
        if (getIntent() != null){
            carId = getIntent().getStringExtra("carId");

            if (!carId.isEmpty()){
                getCarDetail(carId);
            }

        }



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

                final String papers_url = registerCarModel.getPapers().toString();
                btn_status.setText("Status: " + registerCarModel.getStatus());

                if (papers_url.equals("NULL")){

                    btn_make_payment.setVisibility(View.GONE);
                    btn_upload_papers.setVisibility(View.VISIBLE);
                }else {
                    btn_upload_papers.setVisibility(View.GONE);
                    btn_make_payment.setVisibility(View.VISIBLE);
                }

                Picasso.get().load(registerCarModel.getCar_pic()).placeholder(R.drawable.benz).into(car_picture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}

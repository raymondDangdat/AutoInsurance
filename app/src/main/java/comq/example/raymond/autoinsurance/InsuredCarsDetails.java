package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import comq.example.raymond.autoinsurance.Model.RegisterCarModel;

public class InsuredCarsDetails extends AppCompatActivity {
    private Toolbar insuredCarsToolBar;

    private static final int GALLERY_REQUEST_CODE = 1;

    private Uri mImageUri = null;

    private ProgressDialog progressDialog;

    private StorageReference mStorageImage;

    private DatabaseReference cars;

    private TextView txt_car_owner, txt_car_pic, txt_papers, txt_car_make, txt_policy_type, txt_car_model, txt_use, txt_car_cost;
    private Button btn_upload_papers, btn_status, btn_make_payment;
    private ImageView car_picture, img_papers;

    private String carId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insured_cars_details);

        progressDialog = new ProgressDialog(this);

        mStorageImage = FirebaseStorage.getInstance().getReference().child("autoInsurance").child("cars");
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
        img_papers = findViewById(R.id.img_papers);
        txt_car_pic = findViewById(R.id.txt_pic);
        txt_papers = findViewById(R.id.txt_papers);
        btn_upload_papers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPapers();
            }
        });



        //get crime id from Intent
        if (getIntent() != null){
            carId = getIntent().getStringExtra("carId");

            if (!carId.isEmpty()){
                getCarDetail(carId);
            }

        }



    }

    private void uploadPapers() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //to set it to square
                    .setAspectRatio(4,4)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                img_papers.setVisibility(View.VISIBLE);
                img_papers.setImageURI(mImageUri);
                btn_upload_papers.setText("Submit");

                btn_upload_papers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadPapersToFirebase();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadPapersToFirebase() {
        progressDialog.setMessage("Uploading papers");
        progressDialog.show();
        StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String image_url = taskSnapshot.getDownloadUrl().toString();

                cars.child(carId).child("papers").setValue(image_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(InsuredCarsDetails.this, "Papers uploaded successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InsuredCarsDetails.this, UInsuredCars.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(InsuredCarsDetails.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InsuredCarsDetails.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                final String papers_url = registerCarModel.getPapers().toString();
                final String status = registerCarModel.getStatus().toString();
                btn_status.setText("Status: " + registerCarModel.getStatus());

                if (papers_url.equals("NULL")){

                    btn_make_payment.setVisibility(View.GONE);
                    btn_upload_papers.setVisibility(View.VISIBLE);
                    txt_papers.setText("No receipt uploaded yet, please upload car receipt");
                }else {
                    btn_upload_papers.setVisibility(View.GONE);
                    //btn_make_payment.setVisibility(View.VISIBLE);
                    img_papers.setVisibility(View.VISIBLE);
                    Picasso.get().load(registerCarModel.getPapers()).placeholder(R.drawable.benz).into(img_papers);
                }

                if (status.equals("Awaiting Approval")){
                    btn_make_payment.setVisibility(View.GONE);

                }else if (status.equals("Approved")){
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
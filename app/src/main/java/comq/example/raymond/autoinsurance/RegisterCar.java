package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

import comq.example.raymond.autoinsurance.Model.RegisterCarModel;

public class RegisterCar extends AppCompatActivity {
    private Toolbar registerCarToolbar;

    private StorageReference mStorageImage;
    private DatabaseReference cars;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private Uri mImageUri = null;
    protected String uId = "";

    private RegisterCarModel registerCarModel;




    private static final int GALLERY_REQUEST_CODE = 1;


    private ImageView img_car_picture;
    private Button btn_upload_img, btn_submit;
    private EditText editTextFName, editTextLName, editTextOName, editTextAddress, editTextOccupation,
    editTextDisability, editTextMake, editTextModel, editTextCapacity, editTextValueInNaira, editTextVUse,
    editTextPolicyType, editTextNoPlate, editTextBvn;
    private Spinner spinnerNoOfSeats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        mProgress = new ProgressDialog(this);

        cars = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("cars");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("autoInsurance").child("cars");

        uId = mAuth.getCurrentUser().getUid();



        //toolbar
        //initialize our toolBar
        registerCarToolbar = findViewById(R.id.register_car_toolbar);
        setSupportActionBar(registerCarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Insure Car");

        img_car_picture = findViewById(R.id.car_image);
        btn_upload_img = findViewById(R.id.buttonUploadCarImg);
        btn_submit = findViewById(R.id.buttonSubmit);
        editTextFName = findViewById(R.id.editTextFName);
        editTextLName = findViewById(R.id.editTextLName);
        editTextOName = findViewById(R.id.editTextOName);
        editTextBvn = findViewById(R.id.editTextBvn);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextOccupation = findViewById(R.id.editTextOccupation);
        editTextDisability = findViewById(R.id.editTextDisability);
        editTextMake = findViewById(R.id.editTextMake);
        editTextModel = findViewById(R.id.editTextModel);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextValueInNaira = findViewById(R.id.editTextValueInNaira);
        editTextVUse = findViewById(R.id.editTextUserOfVehicle);
        editTextPolicyType = findViewById(R.id.editTextPolicyType);
        editTextNoPlate =findViewById(R.id.editTextNumberPlate);
        spinnerNoOfSeats = findViewById(R.id.no_of_seat);

        btn_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCarImage();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCar();
            }
        });

    }

    private void registerCar() {
        mProgress.setTitle("Car Registration");
        mProgress.setMessage("Registering car...");

        final String fName = editTextFName.getText().toString().trim();
        final String lName = editTextLName.getText().toString().trim();
        final String oName = editTextOName.getText().toString().trim();
        final String bvn = editTextBvn.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        final String occupation = editTextOccupation.getText().toString().trim();
        final String disablities = editTextDisability.getText().toString().trim();
        final String make = editTextMake.getText().toString().trim();
        final String model = editTextModel.getText().toString().trim();
        final String capacity = editTextCapacity.getText().toString().trim();
        final String value = editTextValueInNaira.getText().toString().trim();
        final String use = editTextVUse.getText().toString().trim();
        final String policyType = editTextPolicyType.getText().toString().trim();
        final String no_of_seat = spinnerNoOfSeats.getSelectedItem().toString().trim();
        final String noPlate = editTextNoPlate.getText().toString().trim();
        final String papers = "NULL";
        final String status = "Awaiting Approval";

        final long insuranceDate = new Date().getTime();

        if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName) || TextUtils.isEmpty(oName) || TextUtils.isEmpty(address)
                || TextUtils.isEmpty(make) || TextUtils.isEmpty(model) || TextUtils.isEmpty(capacity) || TextUtils.isEmpty(value)
                || TextUtils.isEmpty(use)|| mImageUri == null || TextUtils.isEmpty(policyType) || no_of_seat.equals("Number of Seats") || TextUtils.isEmpty(noPlate)){
            Toast.makeText(this, "Please fill all fields that are not optional", Toast.LENGTH_SHORT).show();
        }else if (bvn.length() != 11){
            Toast.makeText(this, "BVN must be 11 digits ", Toast.LENGTH_SHORT).show();
        }else{
            mProgress.show();
            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String image_url = taskSnapshot.getDownloadUrl().toString();

                    registerCarModel = new RegisterCarModel(insuranceDate,uId,image_url, fName, lName,
                            oName, occupation, disablities, make, papers, model, capacity, value, use,
                            policyType, noPlate, no_of_seat, status);
                    cars.push().setValue(registerCarModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgress.dismiss();
                                Toast.makeText(RegisterCar.this, "Car registered successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterCar.this, UInsuredCars.class));
                                finish();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(RegisterCar.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterCar.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadCarImage() {
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
                img_car_picture.setVisibility(View.VISIBLE);
                img_car_picture.setImageURI(mImageUri);
                btn_upload_img.setText("Change Image");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

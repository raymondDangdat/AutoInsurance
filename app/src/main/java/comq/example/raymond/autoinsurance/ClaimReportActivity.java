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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

import comq.example.raymond.autoinsurance.Model.PaymentModel;
import comq.example.raymond.autoinsurance.Model.ReportModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;

public class ClaimReportActivity extends AppCompatActivity {
    private Toolbar claimReportToolbar;

    private StorageReference mStorageImage;
    private ProgressDialog mProgress;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST_CODE = 1;

    private String carId, uId = "";
    private String name, make, model, noPlate, policyType, use = "";
    private long paymentDate, dateRegistered;
    private double value, amountPaid;

    private PaymentModel paymentModel;
    private ReportModel newReport;

    private DatabaseReference payments, claims;
    private FirebaseAuth mAuth;

    private EditText editTextName, editTextDateRegistered, editTextDatePaid, editTextMake, editTextModel, editTextAmountPaid, editTextValueInNaira, editTextVUse,
            editTextPolicyType, editTextNoPlate;
    private Button btnUploadCarImage, btnSubmit;
    private ImageView imgeCarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_report);

        payments = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("payments");
        claims = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("claims");
        mAuth = FirebaseAuth.getInstance();

        mStorageImage = FirebaseStorage.getInstance().getReference().child("autoInsurance").child("claims");

        uId = mAuth.getCurrentUser().getUid();

        mProgress = new ProgressDialog(this);

        //initialize toolBar
        claimReportToolbar = findViewById(R.id.claim_roprt_toolbar);
        setSupportActionBar(claimReportToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Claim Report");


        editTextName = findViewById(R.id.editTextFullname);
        editTextPolicyType = findViewById(R.id.editTextPolicyType);
        editTextDateRegistered = findViewById(R.id.editTextDateRegistered);
        editTextDatePaid = findViewById(R.id.editTextDatePaid);
        editTextMake = findViewById(R.id.editTextMake);
        editTextModel = findViewById(R.id.editTextModel);
        editTextValueInNaira = findViewById(R.id.editTextValueInNaira);
        editTextVUse = findViewById(R.id.editTextUserOfVehicle);
        editTextAmountPaid = findViewById(R.id.editTextAmountPaid);
        editTextNoPlate =findViewById(R.id.editTextNumberPlate);

        btnSubmit = findViewById(R.id.buttonSubmit);
        btnUploadCarImage = findViewById(R.id.buttonUploadCarImg);
        imgeCarImage = findViewById(R.id.car_image);




        //get crime id from Intent
        if (getIntent() != null){
            carId = getIntent().getStringExtra("carId");

            if (!carId.isEmpty()){
                getCarDetail(carId);
            }

        }

        btnUploadCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCarImage();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportClaim();
            }
        });
    }

    private void reportClaim() {
        mProgress.setTitle("Claim Report");
        mProgress.setMessage("Reporting...");

        if (mImageUri == null){
            Toast.makeText(this, "Please upload current car image", Toast.LENGTH_SHORT).show();
        }else {

            mProgress.show();

            final long reportDate = new Date().getTime();

            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String image_url = taskSnapshot.getDownloadUrl().toString();

                    newReport = new ReportModel(uId, name, make, model, image_url, noPlate, policyType,
                            use, reportDate, paymentDate, value, amountPaid);
                    claims.child(carId).setValue(newReport).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgress.dismiss();
                                Toast.makeText(ClaimReportActivity.this, "Claim reported successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ClaimReportActivity.this, UserHome.class));
                                finish();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(ClaimReportActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ClaimReportActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                imgeCarImage.setVisibility(View.VISIBLE);
                imgeCarImage.setImageURI(mImageUri);
                btnUploadCarImage.setText("Change Image");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void getCarDetail(String carId) {
        payments.child(carId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                paymentModel = dataSnapshot.getValue(PaymentModel.class);
                name = paymentModel.getName();
                make = paymentModel.getMake();
                model = paymentModel.getModel();
                noPlate = paymentModel.getNoPlate();
                policyType = paymentModel.getPolicyType();
                use = paymentModel.getUse();
                dateRegistered = paymentModel.getInsuranceDate();
                paymentDate = paymentModel.getPaymentDate();
                value = paymentModel.getValue();
                amountPaid = paymentModel.getAmountPaid();

                //set the text to the respective editText
                editTextName.setText("Name: " + name);
                editTextMake.setText("Car Make: " + make);
                editTextModel.setText("Car Model: " + model);
                editTextNoPlate.setText("No. Plate: " + noPlate);
                editTextPolicyType.setText("Policy Type: " + policyType);
                editTextVUse.setText("Car Use: " + use);
                editTextAmountPaid.setText(String.valueOf("Amount Paid: " + amountPaid));
                editTextValueInNaira.setText(String.valueOf("Car Value: " + value));
                editTextDatePaid.setText("Date Paid: " + InsuranceUtils.dateFromLong(paymentDate));
                editTextDateRegistered.setText("Date Registered: " + InsuranceUtils.dateFromLong(dateRegistered));



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

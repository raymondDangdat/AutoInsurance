package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

import comq.example.raymond.autoinsurance.Model.PaymentModel;
import comq.example.raymond.autoinsurance.Model.RegisterCarModel;

public class InsuredCarsDetails extends AppCompatActivity {
    private Toolbar insuredCarsToolBar;

    //declare payment parameters
    private long insuranceDate, paymentDate;
    private String uId, car_pic, papers, name, make, model, use, noPlate, status, policyType1, status1 = "";
    private double value;


    private static final int GALLERY_REQUEST_CODE = 1;

    private double insuranceAmount;

    private Uri mImageUri = null;

    private ProgressDialog progressDialog;

    private StorageReference mStorageImage;
    private double carPrice;
    private String policyType = "";

    private FirebaseAuth mAuth;
    private DatabaseReference cars, payment;

    private PaymentModel newPayment;

    private TextView txt_car_owner, txt_car_pic, txt_papers, txt_car_make, txt_policy_type, txt_car_model, txt_use, txt_car_cost;
    private Button btn_upload_papers, btn_status, btn_make_payment;
    private ImageView car_picture, img_papers;

    private String carId = "";

    private MaterialEditText editTextCardName,editTextTotal, editTextCadCCV, editTextExpiry, editTextCardNumber;
    private Button btnSUbmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insured_cars_details);

        progressDialog = new ProgressDialog(this);

        mStorageImage = FirebaseStorage.getInstance().getReference().child("autoInsurance").child("cars");
        cars = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("cars");
        payment = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("payments");
        mAuth = FirebaseAuth.getInstance();

        uId = mAuth.getCurrentUser().getUid();

        //initialize toolBar
        insuredCarsToolBar = findViewById(R.id.insured_cars_detalis_toolbar);
        setSupportActionBar(insuredCarsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Car Details");

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

        editTextCadCCV = findViewById(R.id.edit_ccv);
        editTextCardName = findViewById(R.id.edit_card_name);
        editTextCardNumber = findViewById(R.id.edit_card_number);
        editTextExpiry = findViewById(R.id.edit_card_expiry);
        editTextTotal = findViewById(R.id.edit_total_amount);
        btnSUbmit = findViewById(R.id.btn_submit);


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

    private void getCarDetail(final String carId) {
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

                policyType = registerCarModel.getPolicyType();
                carPrice = dataSnapshot.child("value").getValue(Double.class);
                //Toast.makeText(InsuredCarsDetails.this, ""+carPrice, Toast.LENGTH_SHORT).show();
                //Toast.makeText(InsuredCarsDetails.this, ""+policyType, Toast.LENGTH_SHORT).show();

                final String papers_url = registerCarModel.getPapers().toString();
                final String status = registerCarModel.getStatus().toString();
                btn_status.setText("Status: " + registerCarModel.getStatus());



                if (papers_url.equals("NULL")){

                    btn_make_payment.setVisibility(View.GONE);
                    btn_upload_papers.setVisibility(View.VISIBLE);
                    txt_papers.setText("No receipt uploaded yet, please upload car receipt");
                }else {
                    //get the details for payment
                    car_pic = registerCarModel.getCar_pic().toString();
                    insuranceDate = registerCarModel.getInsuranceDate();
                    papers = registerCarModel.getPapers().toString();
                    make = registerCarModel.getMake().toString();
                    model = registerCarModel.getModel().toString();
                    value = registerCarModel.getValue();
                    use = registerCarModel.getUse().toString();
                    noPlate = registerCarModel.getNoPlate().toString();
                    status1 = registerCarModel.getStatus().toString();
                    policyType1 = registerCarModel.getPolicyType().toString();

                    String fname = registerCarModel.getfName().toString();
                    String otherName = registerCarModel.getoName().toString();
                    String lastName = registerCarModel.getlName().toString();

                    name = lastName + " " + fname + " " + otherName;


                    btn_upload_papers.setVisibility(View.GONE);
                    //btn_make_payment.setVisibility(View.VISIBLE);
                    img_papers.setVisibility(View.VISIBLE);
                    Picasso.get().load(registerCarModel.getPapers()).placeholder(R.drawable.benz).into(img_papers);
                }

                if (status.equals("Awaiting Approval")){
                    btn_make_payment.setVisibility(View.GONE);

                }else if (status.equals("Approved")){
                    btn_make_payment.setVisibility(View.VISIBLE);
                    btn_make_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (policyType.equals("Fully Comprehensive") && carPrice <= 1500000){
                                insuranceAmount = 100000;
                            }else if (policyType.equals("Fully Comprehensive") && carPrice >=3500000){
                                insuranceAmount = 200000;
                            }else if (policyType.equals("Fully Comprehensive") && carPrice >= 5500000){
                                insuranceAmount = 300000;
                            }else if (policyType.equals("Fully Comprehensive") && carPrice >= 7500000){
                                insuranceAmount = 400000;
                            }else if (policyType.equals("Fully Comprehensive") && carPrice >= 10000000){
                                insuranceAmount = 500000;
                            }else if (policyType.equals("Third Party, Fire and Theft") && carPrice <= 1500000){
                                insuranceAmount = 70000;
                            }else if (policyType.equals("Third Party, Fire and Theft") && carPrice >=3500000){
                                insuranceAmount = 250000;
                            }else if (policyType.equals("Third Party, Fire and Theft") && carPrice >= 5500000){
                                insuranceAmount = 250000;
                            }else if (policyType.equals("Third Party, Fire and Theft") && carPrice >= 7500000){
                                insuranceAmount = 360000;
                            }else if (policyType.equals("Third Party, Fire and Theft") && carPrice >= 10000000){
                                insuranceAmount = 450000;
                            }else if (policyType.equals("Third Party") && carPrice <= 1500000){
                                insuranceAmount = 50000;
                            }else if (policyType.equals("Third Party") && carPrice >=3500000){
                                insuranceAmount = 100000;
                            }else if (policyType.equals("Third Party") && carPrice >= 5500000){
                                insuranceAmount = 150000;
                            }else if (policyType.equals("Third Party") && carPrice >= 7500000){
                                insuranceAmount = 200000;
                            }else if (policyType.equals("Third Party") && carPrice >= 10000000){
                                insuranceAmount = 250000;
                            }


                            btn_status.setText("Insurance Amount to be paid: N" + insuranceAmount);
                            btn_make_payment.setText("Proceed to pay");
                            btn_make_payment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    btn_make_payment.setVisibility(View.GONE);
                                    editTextTotal.setVisibility(View.VISIBLE);
                                    final String insuranceAmount2 = String.valueOf(insuranceAmount);
                                    editTextTotal.setText(insuranceAmount2);
                                    editTextTotal.setEnabled(false);
                                    editTextCardName.setVisibility(View.VISIBLE);
                                    editTextCardNumber.setVisibility(View.VISIBLE);
                                    editTextCadCCV.setVisibility(View.VISIBLE);
                                    editTextExpiry.setVisibility(View.VISIBLE);
                                    btnSUbmit.setVisibility(View.VISIBLE);

                                    btnSUbmit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String total =insuranceAmount2;
                                            String cardNo = editTextCardNumber.getText().toString().trim();
                                            String cardName = editTextCardName.getText().toString().trim();
                                            String ccv = editTextCadCCV.getText().toString().trim();
                                            String expiry = editTextExpiry.getText().toString().trim();

                                            if (cardNo.length()<16){
                                                Toast.makeText(InsuredCarsDetails.this, "Please enter a valid card number", Toast.LENGTH_SHORT).show();
                                            }else if (TextUtils.isEmpty(cardName)){
                                                Toast.makeText(InsuredCarsDetails.this, "Enter name on card", Toast.LENGTH_SHORT).show();
                                            }else if (ccv.length()<3){
                                                Toast.makeText(InsuredCarsDetails.this, "Enter a valid card CCV", Toast.LENGTH_SHORT).show();
                                            }else if (expiry.length()<4){
                                                Toast.makeText(InsuredCarsDetails.this, "Enter a valid card expiry date", Toast.LENGTH_SHORT).show();
                                            }else {
                                                progressDialog.setMessage("Making payment...");
                                                progressDialog.show();

                                                cars.child(carId).child("status").setValue("Paid").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        paymentDate = new Date().getTime();

                                                        //save payment record
                                                        newPayment = new PaymentModel(insuranceDate, paymentDate, uId, car_pic
                                                        , papers, name, make, model, value, insuranceAmount, use, noPlate, status1, policyType1);
                                                        payment.child(carId).setValue(newPayment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(InsuredCarsDetails.this, "Payment successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(InsuredCarsDetails.this, PaymentDetailsActivity.class));
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    });
                }else if (status.equals("Paid")){
                    btn_make_payment.setText("Report Claim");
                    btn_make_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent claimReport = new Intent(InsuredCarsDetails.this, ClaimReportActivity.class);
                            claimReport.putExtra("carId", carId);
                            startActivity(claimReport);
                        }
                    });



                }

                Picasso.get().load(registerCarModel.getCar_pic()).placeholder(R.drawable.benz).into(car_picture);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
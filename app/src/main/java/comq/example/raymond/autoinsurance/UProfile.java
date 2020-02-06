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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UProfile extends AppCompatActivity {
    private Toolbar profileToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference users;
    private StorageReference mStorageImage;
    private String uId = "";
    private EditText editTextFullName, editTextAddress, editTextPhone;
    private TextView txtEmail;
    private Button btn_update_profile;

    private Uri mImageUri = null;

    private ProgressDialog mProgress;

    private static final int GALLERY_REQUEST_CODE = 1;


    private CircleImageView profile_pix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uprofile);

        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        users = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("users");
        uId = mAuth.getCurrentUser().getUid();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("autoInsurance").child("profilePix");


        txtEmail = findViewById(R.id.txt_email);
        editTextFullName = findViewById(R.id.edt_full_name);
        editTextAddress = findViewById(R.id.edt_address);
        editTextPhone = findViewById(R.id.edt_phone);
        profile_pix = findViewById(R.id.profile_pix);
        btn_update_profile = findViewById(R.id.btn_update_profile);
        //toolbar
        //initialize our toolBar
        profileToolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Profile");

        profile_pix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePix();
            }
        });

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        getUserProfile();
    }

    private void updateProfile() {
        mProgress.setTitle("Update Profile");
        mProgress.setMessage("Updating...");
        final String address = editTextAddress.getText().toString().trim();
       final String phone = editTextPhone.getText().toString().trim();
        final String name = editTextFullName.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || phone.length()<11 || phone.length()>11){
            Toast.makeText(this, "Please type in a valid phone number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
        }else {
            if (mImageUri != null){
                mProgress.show();
                StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String profile_url = taskSnapshot.getDownloadUrl().toString();
                        users.child(uId).child("phone").setValue(phone);
                        users.child(uId).child("name").setValue(name);
                        users.child(uId).child("address").setValue(address);
                        users.child(uId).child("profile_pix").setValue(profile_url).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgress.dismiss();
                                        Toast.makeText(UProfile.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(UProfile.this, UserHome.class));
                                        finish();
                                    }
                                }
                        ).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgress.dismiss();
                                Toast.makeText(UProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                mProgress.show();
                users.child(uId).child("phone").setValue(phone);
                users.child(uId).child("name").setValue(name);
                users.child(uId).child("address").setValue(address).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgress.dismiss();
                                Toast.makeText(UProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UProfile.this, UserHome.class));
                                finish();
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        Toast.makeText(UProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void uploadProfilePix() {
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
                profile_pix.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void getUserProfile() {
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name_retrieved = dataSnapshot.child(uId).child("name").getValue(String.class);
                final String email_retrieved = dataSnapshot.child(uId).child("email").getValue(String.class);
                final String phone_retrieved = dataSnapshot.child(uId).child("phone").getValue(String.class);
                final String address_retrieved = dataSnapshot.child(uId).child("address").getValue(String.class);
                final String profile_pix_data = dataSnapshot.child(uId).child("profile_pix").getValue(String.class);

                editTextPhone.setText(phone_retrieved);
                editTextFullName.setText(name_retrieved);
                txtEmail.setText(email_retrieved);
                editTextAddress.setText(address_retrieved);
                Picasso.get().load(profile_pix_data).placeholder(R.drawable.images).into(profile_pix);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

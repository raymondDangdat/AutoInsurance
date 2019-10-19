package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private Toolbar signUpToolBar;

    private Button buttonSignUp;
    private EditText editTextName, editTextEmail, editTextPassword, editTextCPassword, editTextPhone;
    private TextView textViewLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference users;

    private ProgressDialog registerationProgress;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //toolbar
        //initialize our toolBar
        signUpToolBar = findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(signUpToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        registerationProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        users = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("users");

        buttonSignUp = findViewById(R.id.buttonSignUp);
        editTextCPassword = findViewById(R.id.editTextCPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewLogin = findViewById(R.id.textviewLogin);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);

        //when textview is clicked take user back to login activity
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });




    }

    private void registerUser() {
        final String fullName = editTextName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cPassword = editTextCPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        //check for emptiness
        if (TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(phone) || phone.length()<11){
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(cPassword)){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(cPassword)){
            Toast.makeText(this, "Your password does not match confirm password", Toast.LENGTH_SHORT).show();
        }else {
            registerationProgress.setTitle("Create Account");
            registerationProgress.setMessage("Registering...");
            registerationProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    userId = mAuth.getCurrentUser().getUid();
                    users.child(userId).child("email").setValue(email);
                    users.child(userId).child("phone").setValue(phone);
                    users.child(userId).child("name").setValue(fullName);
                    users.child(userId).child("userId").setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                registerationProgress.dismiss();
                                Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(Register.this, MainActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}

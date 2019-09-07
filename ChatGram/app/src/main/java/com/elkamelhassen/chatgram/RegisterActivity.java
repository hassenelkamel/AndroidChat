package com.elkamelhassen.chatgram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by enigma on 28/03/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText emaile,name,password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnRegister;
    private Button btnLogin;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ImageView imageView;
    private EditText txtImageName;


    private Uri imgUri;
    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name=findViewById(R.id.edit_nom_utilisateur_register);
        emaile=findViewById(R.id.edit_email_register);
        password=findViewById(R.id.edit_pwd_register);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        btnRegister=findViewById(R.id.btn_creer_register);
        btnLogin=findViewById(R.id.btn_login_register);

        //image insertion

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name_content, password_content, email_content;
                name_content = name.getText().toString().trim();
                password_content = password.getText().toString().trim();
                email_content = emaile.getText().toString().trim();
                if (!TextUtils.isEmpty(email_content) && !TextUtils.isEmpty(name_content) && !TextUtils.isEmpty(password_content)) {
                    mAuth.createUserWithEmailAndPassword(email_content, password_content).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = mDatabase.child(user_id);
                                current_user_db.child("Name").setValue(name_content);
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }

                        }
                    });
                }

            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));


            }
        });



    }
    public void btnBrowse_Click(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @SuppressWarnings("VisibleForTests")
    public void btnUpload_Click(View v) {
        if (imgUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading image");
            dialog.show();

            //Get the storage reference
            StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            //Add file to reference

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    //Dimiss dialog when success
                    dialog.dismiss();
                    //Display success toast msg
                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                   // ImageUpload imageUpload = new ImageUpload(txtImageName.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                    ImageUpload imageUpload=new ImageUpload(taskSnapshot.getDownloadUrl().toString());
                    //Save image info in to firebase database
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(imageUpload);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Dimiss dialog when error
                            dialog.dismiss();
                            //Display err toast msg
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show upload progress

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
        }
    }



}


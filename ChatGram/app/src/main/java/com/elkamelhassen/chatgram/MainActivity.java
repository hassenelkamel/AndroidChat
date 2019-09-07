package com.elkamelhassen.chatgram;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText editMessage;
    private DatabaseReference mDatabase;
    private Button sendButtonClicked;
    private RecyclerView mMessageRcv;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;
    private Uri imgUri;


    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButtonClicked=findViewById(R.id.btn_main_send);
        editMessage=findViewById(R.id.editMessageE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Messages");
        mMessageRcv=findViewById(R.id.messageRec);
        mMessageRcv.setHasFixedSize(true);//initialisation
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRcv.setLayoutManager(linearLayoutManager);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                }
            }
        };


        sendButtonClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentUser=mAuth.getCurrentUser();
                mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
                final String messageValue=editMessage.getText().toString().trim();
                if(!TextUtils.isEmpty(messageValue)){
                    final DatabaseReference newPost=mDatabase.push();
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("content").setValue(messageValue);
                            newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mMessageRcv.scrollToPosition(mMessageRcv.getAdapter().getItemCount());
                }

            }

        });

    }



    @Override
    protected void onStart() {//create the firebase recycler adapter
        super.onStart();
        FirebaseRecyclerAdapter<Message,MessageViewHolder> FBRA=new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
           Message.class,
                R.layout.singlemessagelayout,
                MessageViewHolder.class,
                mDatabase
        )
        {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.setContent(model.getContent());
                viewHolder.setUsername(model.getUsername());

            }
        };
        mMessageRcv.setAdapter(FBRA);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MessageViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setContent(String content){
            TextView message_content=mView.findViewById(R.id.txv_singlesms_message);
            message_content.setText(content);
        }

        public void setUsername(String username){
            TextView username_content=mView.findViewById(R.id.txv_singlesms_nom);
            username_content.setText(username);
        }

    }

}

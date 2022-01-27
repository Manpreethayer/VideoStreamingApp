package com.example.hp.teacher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.teacher.Camera.CameraActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ChatActivity extends AppCompatActivity {

    private String chatUser;
    private Toolbar chatToolbar;

    private DatabaseReference rootDatabase;

    private TextView display_name;
    private TextView last_seen;
    private SimpleDraweeView user_image;

    private FirebaseAuth firebaseAuth;
    private String current_user;
    public Boolean isFirstPageLoaded=true;

    private ImageButton cameraButton;
    private EditText chatMessageView;
    private ImageButton chatSendButton;

    private RecyclerView messagesRecyclerList;
    private SwipeRefreshLayout refreshLayout;
    private DatabaseReference databaseReference,chatDatabaseReference;

    private final List<Messages> messageList=new ArrayList<>();
   // private final List<Messages> messageTime=new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD=10;
    private int currentPage=1;

    private int Item_Position=0;
    private String last_Key="";
    private String prev_key="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Fresco.initialize(this);

        chatToolbar = (Toolbar) findViewById(R.id.chat_Toolbar);

        setSupportActionBar(chatToolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });







       /* ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);*/

        rootDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user=firebaseAuth.getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());


        chatUser = getIntent().getStringExtra("user_id");
        String Username = getIntent().getStringExtra("user_name");


        // getSupportActionBar().setTitle(Username);

       /* LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile_intent=new Intent(ChatActivity.this,UsersProfileActivity.class);
                profile_intent.putExtra("user_id",chatUser);
                startActivity(profile_intent);
            }
        });*/
       // actionBar.setCustomView(actionBarView);


        //-------------custom view items-----------------------

        display_name=(TextView) findViewById(R.id.chat_display_name);
        last_seen=(TextView) findViewById(R.id.chat_last_seen);
        user_image=(SimpleDraweeView) findViewById(R.id.custom_bar_image);


        cameraButton=(ImageButton) findViewById(R.id.chat_camera_btn);
        chatMessageView=(EditText) findViewById(R.id.chat_message_view);
        chatSendButton=(ImageButton) findViewById(R.id.chat_send_btn);

        messageAdapter=new MessageAdapter(messageList);

        messagesRecyclerList=(RecyclerView) findViewById(R.id.messages_list);
        refreshLayout=(SwipeRefreshLayout) findViewById(R.id.message_swipe_refresh);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.DKGRAY,Color.MAGENTA);

        linearLayoutManager=new LinearLayoutManager(this);
        messagesRecyclerList.setHasFixedSize(true);
        messagesRecyclerList.setLayoutManager(linearLayoutManager);
        messagesRecyclerList.setItemAnimator(new DefaultItemAnimator());
        messagesRecyclerList.setAdapter(messageAdapter);
        loadMessage();


        display_name.setText(Username);






        rootDatabase.child("users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online=dataSnapshot.child("online").getValue().toString();
                String image=dataSnapshot.child("thumb_image").getValue().toString();

                Uri uri = Uri.parse(image);
                user_image.setImageURI(uri);


                if(online.equals("true"))
                {
                    last_seen.setText("Active Now");
                }
                else
                {

                    GetTimeAgo getTimeAgo=new GetTimeAgo();
                    Long lastTime=Long.parseLong(online);
                    String lastSeenTime=getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                    last_seen.setText(lastSeenTime);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rootDatabase.child("chat").child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(chatUser))
                {
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap=new HashMap();
                    chatUserMap.put("chat/" + current_user + "/" + chatUser ,chatAddMap);
                    chatUserMap.put("chat/" + chatUser + "/" + current_user ,chatAddMap);

                    rootDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError!=null)
                            {
                                Log.d("Chat Log",databaseError.getMessage().toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent=new Intent(ChatActivity.this, CameraActivity.class);
                startActivity(cameraIntent);
            }
        });

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                currentPage++;


                Item_Position=0;


                loadMoreMessage();

            }
        });

    }

    public void showprofile(View view)
    {

        Intent profile_intent=new Intent(ChatActivity.this,UsersProfileActivity.class);
        profile_intent.putExtra("user_id",chatUser);
        startActivity(profile_intent);
    }


    private void loadMoreMessage() {

        DatabaseReference messageref= rootDatabase.child("messages").child(current_user).child(chatUser);


        Query messageQuery=messageref.orderByKey().endAt(last_Key).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages messages=dataSnapshot.getValue(Messages.class);
                String message_key=dataSnapshot.getKey();

                if(! prev_key.equals(message_key)) {
                    messageList.add(Item_Position++, messages);

                }
                else
                {
                    prev_key=last_Key;
                }

                if(Item_Position==1)
                {
                    last_Key=message_key;
                }


                messageAdapter.notifyDataSetChanged();



                refreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadMessage() {
        DatabaseReference messageref= rootDatabase.child("messages").child(current_user).child(chatUser);

        Query messageQuery=messageref.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages messages=dataSnapshot.getValue(Messages.class);
               // Messages time=dataSnapshot.getValue(Messages.class);
                Item_Position++;


                if(Item_Position==1)
                {
                    String message_key=dataSnapshot.getKey();


                    last_Key=message_key;
                    prev_key=message_key;
                }

                messageList.add(messages);
              //  messageTime.add(time);

                messageAdapter.notifyDataSetChanged();

                messagesRecyclerList.scrollToPosition(messageList.size()-1);

                refreshLayout.setRefreshing(false);


                isFirstPageLoaded=false;

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message=chatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message))
        {




            String current_user_ref="messages/" + current_user + "/" +chatUser;
            String chat_user_ref="messages/" + chatUser + "/" +current_user;

            DatabaseReference user_message_push=rootDatabase.child("messages").child(current_user).child(chatUser).push();
            String push_id=user_message_push.getKey();

            final String current_time= DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

            final String date= DateFormat.getTimeInstance(DateFormat.LONG).format(new Date());
            createChatUser(date);

            Map messagingMap=new HashMap();
            messagingMap.put("message", message);
            messagingMap.put("seen", false);
            messagingMap.put("type", "text");
            messagingMap.put("time", current_time);
            messagingMap.put("from",current_user);

            Map messageUserMap=new HashMap();

            messageUserMap.put(current_user_ref + "/" + push_id,messagingMap);
            messageUserMap.put(chat_user_ref + "/" + push_id,messagingMap);

            chatMessageView.setText("");



            rootDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError!=null)
                    {
                        Log.d("Chat Log",databaseError.getMessage().toString());
                    }

                }
            });
        }


    }

    private void createChatUser(final String date) {

        chatDatabaseReference=FirebaseDatabase.getInstance().getReference();


        chatDatabaseReference.child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(chatUser))
                {
                    Map chatAddMap=new HashMap();

                    chatAddMap.put("date",date);

                    Map chatUserMap=new HashMap();
                    chatUserMap.put("chat_users/" + current_user + "/" + chatUser ,chatAddMap);
                    chatUserMap.put("chat_users/" + chatUser + "/" + current_user ,chatAddMap);

                    chatDatabaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError!=null)
                            {
                                Log.d("Chat Log",databaseError.getMessage().toString());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.chat_menu_delete:
                Toast.makeText(ChatActivity.this,"Delete Chat",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.chat_menu_block:
                Toast.makeText(ChatActivity.this,"Block",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

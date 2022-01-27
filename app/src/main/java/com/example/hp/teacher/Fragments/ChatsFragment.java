package com.example.hp.teacher.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hp.teacher.ChatActivity;
import com.example.hp.teacher.Friends;
import com.example.hp.teacher.R;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private ShimmerFrameLayout mShimmerViewContainer;

    private RecyclerView friendsList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference friendsDatabase;
    private DatabaseReference usersDatabase;
    private TextView nofriends;
    private EditText chatSearch;






    private String current_user_id;
    private View mainView;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView= inflater.inflate(R.layout.fragment_home, container, false);

        Fresco.initialize(getContext());

        friendsList=(RecyclerView)mainView.findViewById(R.id.friends_list);
        firebaseAuth=FirebaseAuth.getInstance();

        mShimmerViewContainer = mainView.findViewById(R.id.shimmer_view_container);

        nofriends=(TextView)mainView.findViewById(R.id.nofriendsyet);







        current_user_id=firebaseAuth.getCurrentUser().getUid();

        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("chat_users").child(current_user_id);
        friendsDatabase.keepSynced(true);






        usersDatabase= FirebaseDatabase.getInstance().getReference().child("users");
        usersDatabase.keepSynced(true);

        friendsList.setHasFixedSize(true);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));





        return mainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        friendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    FirebaseRecyclerOptions<Friends> options =
                            new FirebaseRecyclerOptions.Builder<Friends>()
                                    .setQuery(friendsDatabase, Friends.class)
                                    .build();


                    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull Friends model) {


                            holder.setDate(model.getDate());
                            final String date = model.getDate();

                            final String user_list_id = getRef(position).getKey();

                            usersDatabase.child(user_list_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    final String user_name = dataSnapshot.child("name").getValue().toString();
                                    String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                    mShimmerViewContainer.stopShimmer();
                                    mShimmerViewContainer.setVisibility(View.GONE);


                                    holder.setName(user_name);
                                    holder.setImage(userThumb, getContext());

                                    if (dataSnapshot.hasChild("online")) {

                                        String userOnline = dataSnapshot.child("online").getValue().toString();
                                        holder.setUserOnline(userOnline);
                                    }

                                    holder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {


                                            //go to chat activity
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("user_id", user_list_id);
                                            chatIntent.putExtra("user_name", user_name);
                                            chatIntent.putExtra("friends_date", date);
                                            startActivity(chatIntent);


                                /* CharSequence options[]=new CharSequence[]{"View Profile","Send Message"};

                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i==0)
                                        {
                                            Intent profile_intent=new Intent(getContext(),UserProfileActivity.class);
                                            profile_intent.putExtra("user_id",user_list_id);
                                            startActivity(profile_intent);
                                        }

                                        if(i==1)
                                        {
                                            Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",user_list_id);
                                            chatIntent.putExtra("user_name",user_name);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });
                                builder.show();
                                */
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mShimmerViewContainer.stopShimmer();
                                    mShimmerViewContainer.setVisibility(View.GONE);

                                }
                            });
                        }


                        @NonNull
                        @Override
                        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view = LayoutInflater.from(viewGroup.getContext())
                                    .inflate(R.layout.single_user_layout, viewGroup, false);

                            return new FriendsViewHolder(view);
                        }
                    };

                    friendsRecyclerViewAdapter.startListening();


                    friendsList.setAdapter(friendsRecyclerViewAdapter);

                }
                else
                {
                    nofriends.setVisibility(View.VISIBLE);
                    friendsList.setVisibility(View.GONE);

                  mShimmerViewContainer.stopShimmer();
                  mShimmerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }








    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();

    }

    @Override
    public void onResume() {
        mShimmerViewContainer.startShimmer();

        super.onResume();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setDate(String date)
        {
            TextView userNameView=(TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);
        }

        public void setName(String name)
        {
            TextView userNameView=(TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setImage(String image, Context context)
        {
            Uri uri = Uri.parse(image);
            SimpleDraweeView userimage = (SimpleDraweeView) mView.findViewById(R.id.user_single_image);
            userimage.setImageURI(uri);

        }

        public void setUserOnline(String online_status)
        {
            TextView userOnlineView=(TextView) mView.findViewById(R.id.online_icon);

            if(online_status.equals("true"))
            {
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else
            {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }


    }

}

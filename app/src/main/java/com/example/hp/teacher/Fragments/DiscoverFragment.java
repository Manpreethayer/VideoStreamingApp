package com.example.hp.teacher.Fragments;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.teacher.R;
import com.example.hp.teacher.UsersModel;
import com.example.hp.teacher.UsersProfileActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    private RecyclerView peopleList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ShimmerFrameLayout mShimmerViewContainer;



    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_discover, container, false);


        Fresco.initialize(getContext());

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
            databaseReference.keepSynced(true);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container_discover);

        firebaseAuth=FirebaseAuth.getInstance();



        peopleList=(RecyclerView) view.findViewById(R.id.people_list);
        peopleList.setHasFixedSize(true);
        peopleList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<UsersModel> options =
                new FirebaseRecyclerOptions.Builder<UsersModel>()
                        .setQuery(databaseReference, UsersModel.class)
                        .build();

        FirebaseRecyclerAdapter<UsersModel,PeopleViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UsersModel, DiscoverFragment.PeopleViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DiscoverFragment.PeopleViewHolder holder, int position, @NonNull UsersModel model) {

                holder.setName(model.getName());

                //  holder.setStatus(model.getStatus());

                holder.setProfile_image(model.getImage(),getContext());
                holder.setUser_country(model.getUser_country());


                //  holder.setFriendsCount(model.getFriends_Count());

                String current_user=firebaseAuth.getCurrentUser().getUid();

                final String user_id=getRef(position).getKey();


                assert user_id != null;
                if(user_id.equals(current_user))
                {

                    //holder.setYou();
                }



                holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profile_intent=new Intent(getContext(),UsersProfileActivity.class);
                        profile_intent.putExtra("user_id",user_id);
                        startActivity(profile_intent);

                    }
                });



                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
            }


            @NonNull
            @Override
            public DiscoverFragment.PeopleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.people_layout_single, viewGroup, false);

                return new PeopleViewHolder(view);

            }
        };
        firebaseRecyclerAdapter.startListening();
        peopleList.setAdapter(firebaseRecyclerAdapter);
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

    public static class PeopleViewHolder extends RecyclerView.ViewHolder
    {
        View userView;
        TextView usernameview;
        SimpleDraweeView userimage;
        TextView userCity;


        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            userView=itemView;
        }

        public void setName(String name)
        {
             usernameview=(TextView) userView.findViewById(R.id.people_display_name_single);
            usernameview.setText(name);
        }

     /*   public void setFriendsCount(int friendsCount)
        {
            TextView FriendsCount=(TextView) userView.findViewById(R.id.people_followers_single);
            FriendsCount.setText(friendsCount);
        }
        */

      /*  public void setStatus(String status)
        {
            TextView userstatusview=(TextView) userView.findViewById(R.id.people_single_status);
            userstatusview.setText(status);
        }
        */

        public void setProfile_image(String image, Context context)
        {
            Uri uri = Uri.parse(image);
                userimage = (SimpleDraweeView) userView.findViewById(R.id.user_single_image_people);
                userimage.setImageURI(uri);

        }




        /*public void setYou() {

            request_button=(Button) userView.findViewById(R.id.people_request) ;
            request_button.setVisibility(View.GONE);

            }
*/
        public void setUser_country(String city)
        {
            userCity=(TextView) userView.findViewById(R.id.people_country);
            userCity.setText(city);
        }

    }

}

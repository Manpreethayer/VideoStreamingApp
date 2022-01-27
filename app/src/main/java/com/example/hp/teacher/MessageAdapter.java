package com.example.hp.teacher;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.sql.Time;
import java.util.Date;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter{

    private FirebaseAuth firebaseAuth;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v ;


        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout, parent, false);
            return new MessageViewHolder(v);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recieved_single_layout, parent, false);
            return new MessageReceivedViewHolder(v);
        }

        return null;

    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {



        Messages c = mMessageList.get(i);

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((MessageViewHolder) viewHolder).bind(c);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((MessageReceivedViewHolder) viewHolder).bind(c);
        }


        }


    @Override
    public int getItemViewType(int position) {
        Messages message = (Messages) mMessageList.get(position);

        firebaseAuth= FirebaseAuth.getInstance();
        String current_user_id=firebaseAuth.getCurrentUser().getUid();

        if (message.getFrom().equals(current_user_id)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView sent_message_time;

        //   public TextView displayName;
        // public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            sent_message_time=(TextView) view.findViewById(R.id.text_message_time_outgoing);

        }

        void bind(Messages message) {
            messageText.setText(message.getMessage());
          //  Time time=new Time(message.getTime());
            sent_message_time.setText(message.getTime());


        }


    }


    public class MessageReceivedViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView received_message_time;

        //   public TextView displayName;
        // public ImageView messageImage;

        public MessageReceivedViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_received_layout);
            received_message_time=(TextView) view.findViewById(R.id.text_message_time_incoming);

        }

        void bind(Messages message) {
            messageText.setText(message.getMessage());
           // java.sql.Date date=new java.sql.Date(message.getTime());
           // Time time=new Time(message.getTime());
            received_message_time.setText(message.getTime());


        }
    }


}
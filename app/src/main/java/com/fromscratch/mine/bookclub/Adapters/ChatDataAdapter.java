package com.fromscratch.mine.bookclub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fromscratch.mine.bookclub.Classes.ChatMessage;
import com.fromscratch.mine.bookclub.Classes.UserData;
import com.fromscratch.mine.bookclub.ProfileActivity;
import com.fromscratch.mine.bookclub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String USER_ID = "Uid";
    private static final String USERS_DATA_BRANCH = "UsersData";
    SimpleDateFormat sfd;
    // private final SetOncLickListener mListener;
    private Context mContext;
    private String uid;
    private ArrayList<ChatMessage> chatMessages;
    private DatabaseReference mDatabase;

    public ChatDataAdapter(Context context) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getUid();
        mContext = context;

        chatMessages = new ArrayList<>();
        sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    }

    public void insertItem(ChatMessage chatMessage) {

        chatMessages.add(chatMessage);
        notifyDataSetChanged();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root;

        switch (viewType) {

            case 0:
                root = inflater.inflate(R.layout.sent_message_item, parent, false);
                return new SentMessageViewHolder(root);
            case 1:
                root = inflater.inflate(R.layout.received_message_item, parent, false);
                return new ReceivedMessageViewHolder(root);
            case 2:
                root = inflater.inflate(R.layout.sent_image_item, parent, false);
                return new SentImageViewHolder(root);
            default:
                root = inflater.inflate(R.layout.received_image_item, parent, false);
                return new ReceivedImageViewHolder(root);


        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        switch (holder.getItemViewType()) {
            case 0: {
                SentMessageViewHolder sentMessageVH = (SentMessageViewHolder) holder;
                sentMessageVH.messageBody.setText(chatMessages.get(position).getMessageBody());

                sentMessageVH.time.setText(sfd.format(new Date((Long) chatMessages.get(position).timeStamp)));
                break;
            }

            case 1: {
                final ReceivedMessageViewHolder receivedMessageVH = (ReceivedMessageViewHolder) holder;
                receivedMessageVH.messageBody
                        .setText(chatMessages.get(position).getMessageBody());

                receivedMessageVH.time
                        .setText(sfd.format(new Date((Long) chatMessages.get(position).timeStamp)));
                mDatabase.child(USERS_DATA_BRANCH).child(chatMessages.get(position).getMessageAuthorUID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserData userData = dataSnapshot.getValue(UserData.class);
                                receivedMessageVH.senderName.setText(userData.getUserName());
                                Picasso.with(mContext).load(userData.getProfileImage()).noFade()
                                        .into(receivedMessageVH.profileImage);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(mContext, mContext.getResources().getText(R.string.update_chat_data_error)
                                        , Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                receivedMessageVH.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, ProfileActivity.class)
                                .putExtra(USER_ID, chatMessages.get(position).getMessageAuthorUID()));
                    }
                });
                break;
            }
            case 2: {
                SentImageViewHolder sentImageVH = (SentImageViewHolder) holder;
                Picasso.with(mContext).load(chatMessages.get(position).getMessageBody())
                        .into(sentImageVH.messageBody);
                sentImageVH.time.setText(sfd.format(new Date((Long) chatMessages.get(position).timeStamp)));
                break;
            }
            case 3: {
                final ReceivedImageViewHolder receivedImageVH = (ReceivedImageViewHolder) holder;
                Picasso.with(mContext).load(chatMessages.get(position).getMessageBody())
                        .into(receivedImageVH.messageBody);
                receivedImageVH.time
                        .setText(sfd.format(new Date((Long) chatMessages.get(position).timeStamp)));
                mDatabase.child(USERS_DATA_BRANCH).child(chatMessages.get(position).getMessageAuthorUID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserData userData = dataSnapshot.getValue(UserData.class);
                                receivedImageVH.senderName.setText(userData.getUserName());
                                Picasso.with(mContext).load(userData.getProfileImage()).noFade()
                                        .into(receivedImageVH.profileImage);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(mContext, mContext.getResources().getText(R.string.update_chat_data_error)
                                        , Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                receivedImageVH.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, ProfileActivity.class)
                                .putExtra(USER_ID, chatMessages.get(position).getMessageAuthorUID()));
                    }
                });
                break;

            }

        }


    }

    @Override
    public int getItemCount() {

        return (chatMessages != null ? chatMessages.size() : 0);

    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getMessageAuthorUID().equals(uid)) {
            if (chatMessages.get(position).isImage())
                return 2;
            else
                return 0;
        } else {
            if (chatMessages.get(position).isImage())
                return 3;
            else
                return 1;
        }
    }

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.notifyDataSetChanged();
    }


    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {


        private TextView time;
        private TextView messageBody;
        private TextView senderName;
        private ImageView profileImage;

        ReceivedMessageViewHolder(View itemView) {

            super(itemView);
            time = itemView.findViewById(R.id.timestamp);
            messageBody = itemView.findViewById(R.id.message_body);
            senderName = itemView.findViewById(R.id.sender_name);
            profileImage = itemView.findViewById(R.id.profile_image);


        }


    }

    class ReceivedImageViewHolder extends RecyclerView.ViewHolder {


        private TextView time;
        private ImageView messageBody;
        private TextView senderName;
        private ImageView profileImage;

        ReceivedImageViewHolder(View itemView) {

            super(itemView);
            time = itemView.findViewById(R.id.timestamp);
            messageBody = itemView.findViewById(R.id.image_body);
            senderName = itemView.findViewById(R.id.sender_name);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }


    }


    class SentMessageViewHolder extends RecyclerView.ViewHolder {


        private TextView time;
        private TextView messageBody;

        SentMessageViewHolder(View itemView) {

            super(itemView);
            time = itemView.findViewById(R.id.timestamp);
            messageBody = itemView.findViewById(R.id.message_body);


        }

    }

    class SentImageViewHolder extends RecyclerView.ViewHolder {

        private TextView time;
        private ImageView messageBody;

        SentImageViewHolder(View itemView) {

            super(itemView);
            time = itemView.findViewById(R.id.timestamp);
            messageBody = itemView.findViewById(R.id.image_body);


        }


    }
}

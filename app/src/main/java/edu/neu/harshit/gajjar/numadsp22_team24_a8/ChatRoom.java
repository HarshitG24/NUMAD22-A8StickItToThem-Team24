package edu.neu.harshit.gajjar.numadsp22_team24_a8;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.harshit.gajjar.numadsp22_team24_a8.Model.MessageHistory;
import edu.neu.harshit.gajjar.numadsp22_team24_a8.Utils.FirebaseDB;
import edu.neu.harshit.gajjar.numadsp22_team24_a8.Utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoom extends AppCompatActivity {
    private RecyclerView chatRoomRecyclerView;
    private List<Message> messageList;
    private MessageAdapter messageAdpater;
    private String receiver;
    private StickerNotification notification;
    private FloatingActionButton fab;
    private Activity activity;
    private ActivityResultLauncher<Intent> resultLauncher =
            registerForActivityResult(new
                            ActivityResultContracts.StartActivityForResult(),
                    result ->
                    {
                        if(result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            String id = intent.getStringExtra("id");
                            String name = intent.getStringExtra("name");
                            int count = intent.getIntExtra("count", 0);
                            sendMessageToFirebase(id, name, count);
                        }

                    }
                            );

    // Receiver Username
    String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        this.activity = this;
        Intent intent = getIntent();
        receiver = intent.getStringExtra("currentUserName");
        receiverName = intent.getStringExtra("clickedUserName");
        getSupportActionBar().setTitle(receiver);

        chatRoomRecyclerView = findViewById(R.id.chat_room_recycler_view);
        this.fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent stickerIntent = new Intent(activity, StickerActivity.class);
            resultLauncher.launch(stickerIntent);
        });

        messageList = new ArrayList<Message>();

        fetchChatHistory();

        // Stickers
        this.notification = new StickerNotification(this);

    }

    public void sendMessageToFirebase(String stickerId, String stickerName, int count){
        Log.i("current_username", FirebaseDB.currentUser.getUsername());
        Log.i("receiver_username", receiverName);

        DatabaseReference reference = FirebaseDB.getReferencetoRootDB();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", FirebaseDB.currentUser.getUsername());
        hashMap.put("receiver", receiverName);
        hashMap.put("message", stickerId);
        hashMap.put("senderID", FirebaseDB.currentUser.getId());
//        hashMap.put("receiverID", receiverUserId);
        hashMap.put("timestamp", Util.getGMTTimestamp());

        String chat_id = Util.generateChatID(FirebaseDB.currentUser.getUsername(), receiverName);
        reference.child(getString(R.string.chat)).child(chat_id).push().setValue(hashMap);

        FirebaseDB.getDataReference(getString(R.string.user_db)).child(FirebaseDB.getCurrentUser().getUid())
                .child("image_count")
                .child(stickerName)
                .setValue(String.valueOf(count + 1));
    }

    public void fetchChatHistory(){
        String chatid = Util.generateChatID(FirebaseDB.currentUser.getUsername(), receiverName);
        DatabaseReference chatRef = FirebaseDB.getDataReference(getString(R.string.chat)).child(chatid);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    MessageHistory history = (MessageHistory) snap.getValue(MessageHistory.class);

                    if(history != null){
                        messageList.add(new Message(history.getTimestamp(), history.getSender(), Integer.valueOf(history.getMessage())));
                    }
                }
                messageAdpater = new MessageAdapter(ChatRoom.this,messageList);
                chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(ChatRoom.this));
                chatRoomRecyclerView.setAdapter(messageAdpater);
                chatRoomRecyclerView.scrollToPosition(messageList.size() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.infero00o.massad;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEnergyProfile;
import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Config;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin extends AppCompatActivity {


    public int exitCount = 0;

    public int personCount = 0;


    public ArrayList admin_ids = new ArrayList();
    public String tel;
    public String location;
    public String uuid;
    public String senderID;
    public PeerAdapter peerAdapter = new PeerAdapter(new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        location = extras.getString("location");


        RecyclerView peerRecyclerView=findViewById(R.id.peerRecyclerView);
        RecyclerView.LayoutManager peerLayoutManager;
        peerRecyclerView.setAdapter(peerAdapter);


        peerLayoutManager = new LinearLayoutManager(this);
        peerRecyclerView.setLayoutManager(peerLayoutManager);

        startBridgefy();

        final TextView t = findViewById(R.id.userInfoText);
        t.setText("You are admin ID: " + uuid + "\n" + "Your location: " + location);

        final Button fire = findViewById(R.id.fire);
        fire.setVisibility(View.VISIBLE);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fire();
            }
        });

        final Button flood = findViewById(R.id.flood);
        flood.setVisibility(View.VISIBLE);
        flood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flood();
            }
        });

        final Button custom = findViewById(R.id.custom);
        custom.setVisibility(View.VISIBLE);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom();
            }
        });

        final Button active = findViewById(R.id.active_shooter);
        active.setVisibility(View.VISIBLE);
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active();
            }
        });


    }


    public void startBridgefy() {
        Config.Builder builder = new Config.Builder();
        builder.setEnergyProfile(BFEnergyProfile.HIGH_PERFORMANCE);
        builder.setEncryption(true);
        Bridgefy.start(messageListener, stateListener);
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();

    }

    private MessageListener messageListener = new MessageListener() {
        @Override

        public void onMessageReceived(Message message) {
            if (message.getContent().containsKey("alertType")){
                double alertType = (double) message.getContent().get("alertType");

                if (alertType == 9){
                    int position = peerAdapter.getPeerPosition(message.getSenderId());
                    if (position > -1) {
                        Peer peer = peerAdapter.peers.get(position);
                        peer.setConnected(false);
                        peer.setOutside(true);
                        peerAdapter.peers.set(position, peer);
                        peerAdapter.notifyItemChanged(position);
                    }
                    Toast.makeText(getApplicationContext(), message.getSenderId().toString() + " has exit the building", Toast.LENGTH_LONG).show();

                } else if (alertType == 5){


                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    String uuid = message.getSenderId();
                    Intent intent = new Intent(getApplicationContext(), directMessage.class);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("senderID", uuid);
                    startActivity(intent);


                } else  if (alertType == 6){

                    Bridgefy.stop();
                    finish();
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);

                }} else {

                Peer peer = new Peer((String) message.getContent().get("ID"), (String) message.getContent().get("Make"), (String) message.getContent().get("Model"));
                if ((double) message.getContent().get("Status") == 1) {
                    peer.setConnected(Boolean.TRUE);
                }
                if (admin_ids.contains(message.getContent().get("ID")) && (double) message.getContent().get("Admin") == 1) {
                    peer.setAdmin(Boolean.TRUE);
                } else {
                    peer.setAdmin(false);
                }
                peer.setOutside(false);
                peerAdapter.addPeer(peer);


            }
        }




        public void onBroadcastMessageReceived(Message message) {
            int alertType = (int) message.getContent().get("alertType");
            senderID = message.getSenderId();
            //{"id":"0d5f5dd6-7ef8-4596-b5c5-cee8550d741b"}


            if (admin_ids.contains(senderID)) {
                if (alertType != 3) {
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TEL", tel);
                    startActivity(intent);
                }
                else{
                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("TEL", tel);
                    startActivity(intent);
                }

            }
        }

    };
    private StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {


            HashMap<String, Object> map = new HashMap<>();
            map.put("ID", uuid);
            map.put("Make", Build.MANUFACTURER);
            map.put("Model", Build.MODEL);
            if (admin_ids.contains(uuid)){
                map.put("Admin", 1);} else {map.put("Admin", 0);}
            map.put("Status", 1);
            device.sendMessage(map);
        }
        @Override
        public void onDeviceLost(Device device) {
            peerAdapter.removePeer(device);
        }

        @Override
        public void onStartError(String message, int errorCode) {
            Log.e("Main", "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(Admin.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };




    public void fire() {
        int alertType = 1;
        HashMap<String, Object> content = new HashMap<>();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }

    public void flood() {
        int alertType = 2;
        HashMap<String, Object> content = new HashMap<>();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }

    public void custom() {
        Intent intent = new Intent(getApplicationContext(), customMessage.class);
        startActivity(intent);
    }


    public void active() {
        int alertType = 4;
        HashMap<String, Object> content = new HashMap<>();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }
}
class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder> {
    public final List<Peer> peers;

    PeerAdapter(List<Peer> peers) {
        this.peers = peers;
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    void addPeer(Peer peer){
        int position = getPeerPosition(peer.getUuid());
        if (position > -1){
            peers.set(position, peer);
            notifyItemChanged(position);
        } else {
            peers.add(peer);
            notifyItemChanged(peers.size() - 1);
        }
    }

    void removePeer(Device lostPeer) {
        int position = getPeerPosition(lostPeer.getUserId());
        if (position > -1) {
            Peer peer = peers.get(position);
            peer.setConnected(false);
            peers.set(position, peer);
            notifyItemChanged(position);
        }
    }

    public int getPeerPosition(String peerId) {
        for (int i = 0; i < peers.size(); i++) {
            if (peers.get(i).getUuid().equals(peerId))
                return i;
        }
        return -1;
    }
    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.peer_row, parent, false);
        return new PeerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PeerViewHolder peerHolder, int position) {
        peerHolder.setPeer(peers.get(position));

        peerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Intent intent = new Intent(view.getContext(), sendDirectMessage.class);
                                String uuid = peers.get(position).getUuid();
                                intent.putExtra("UUID", uuid);
                                view.getContext().startActivity(intent);
                                break;
                            case R.id.menu2:
                                HashMap<String, Object> content = new HashMap<>();
                                content.put("alertType", 6);
                                uuid = peers.get(position).getUuid();

                                com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
                                builder.setContent(content);
                                builder.setReceiverId(uuid);
                                Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }


    public class PeerViewHolder extends RecyclerView.ViewHolder {
        final TextView peerID;
        final TextView peerMake;
        final TextView peerModel;
        final TextView peerStatus;
        final TextView peerAdmin;
        Peer peer;

        PeerViewHolder(View view){
            super(view);
            peerID = view.findViewById(R.id.peerID);
            peerMake = view.findViewById(R.id.peerMake);
            peerModel = view.findViewById(R.id.peerModel);
            peerStatus = view.findViewById(R.id.peerStatus);
            peerAdmin = view.findViewById(R.id.peerAdmin);

        }

        void setPeer(Peer peer){
            this.peer = peer;
            peerID.setText(peer.getUuid());
            peerMake.setText(peer.getMake());
            peerModel.setText(peer.getModel());
            if (peer.getConnected()){
                peerStatus.setText("Connected");
            } else  {peerStatus.setText("Disconnected");}
            if (peer.getOutside()){peerStatus.setText("Outside");}
            if (peer.getAdmin()){
                peerAdmin.setText("Admin");
            } else {peerAdmin.setText("User");}
        }



    }

}

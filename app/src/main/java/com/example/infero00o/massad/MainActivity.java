
package com.example.infero00o.massad;

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
import com.example.infero00o.massad.R;
import com.example.infero00o.massad.*;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

//{"id": ["0d5f5dd6-7ef8-4596-b5c5-cee8550d741b", "4d437b0c-b550-4aea-bee3-b228019123eb"]}
public class MainActivity extends AppCompatActivity {

    public ArrayList admin_ids = new ArrayList();
    public String tel;
    public String location;
    public String uuid;
    public String senderID;
    public PeerAdapter peerAdapter = new PeerAdapter(new ArrayList<>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView peerRecyclerView=findViewById(R.id.peerRecyclerView);
        RecyclerView.LayoutManager peerLayoutManager;
        peerRecyclerView.setAdapter(peerAdapter);


        peerLayoutManager = new LinearLayoutManager(this);
        peerRecyclerView.setLayoutManager(peerLayoutManager);


        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Admin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(intent, 0);
            }
        });




        if (admin_ids.isEmpty()) {
            qrScanner();
        }

        BluetoothAdapter btA = BluetoothAdapter.getDefaultAdapter();
        if (!btA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }


        Bridgefy.initialize(getApplicationContext(), "c567d3e3-cb18-4f5f-af10-ef5cf018aa3a", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                uuid = bridgefyClient.getUserUuid();
                startBridgefy();
                Toast.makeText(getApplicationContext(), "Bridgefy Start", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }


        });





    }

    public void startBridgefy() {
        Bridgefy.start(messageListener, stateListener);
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();
    }

    private MessageListener messageListener = new MessageListener() {
        @Override

        public void onMessageReceived(Message message) {
                Peer peer = new Peer((String) message.getContent().get("ID"), (String) message.getContent().get("Make"), (String) message.getContent().get("Model"));
                peer.setConnected(Boolean.TRUE);
                if (admin_ids.contains(message.getContent().get("ID"))) {
                    peer.setAdmin(Boolean.TRUE);
                } else {
                    peer.setAdmin(false);
                }
                peerAdapter.addPeer(peer);
            }




        public void onBroadcastMessageReceived(Message message) {
            int alertType = (int) message.getContent().get("alertType");
            senderID = message.getSenderId();
            //{"id":"0d5f5dd6-7ef8-4596-b5c5-cee8550d741b"}


            if (admin_ids.contains(senderID)) {
                if (alertType == 6){

                    Bridgefy.stop();
                    finish();
                    System.exit(0);
                } else if (alertType == 5){


                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    Intent intent = new Intent(getApplicationContext(), directMessage.class);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    startActivity(intent);


                }else if (alertType != 3) {
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
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };

    public void qrScanner(){
        Intent intent = new Intent(getApplicationContext(), QRScanner.class);
        startActivityForResult(intent, 1);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                admin_ids = data.getStringArrayListExtra("admin_ids");
                tel = data.getStringExtra("tel");
                location = data.getStringExtra("location");

                if (admin_ids.contains(uuid)){
                    Intent intent = new Intent(getApplicationContext(), Admin.class);
                    intent.putExtra("UUID", uuid);
                    intent.putExtra("location", location);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), User.class);
                    intent.putExtra("UUID", uuid);
                    intent.putExtra("location", location);
                    intent.putExtra("tel", tel);
                    startActivity(intent);

                }


            }
        }
    }

}
class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.PeerViewHolder> {

    private final List<Peer> peers;

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

    private int getPeerPosition(String peerId) {
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
                                Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
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
            if (peer.getAdmin()){
                peerAdmin.setText("Admin");
            } else {peerAdmin.setText("User");}
        }



    }

}
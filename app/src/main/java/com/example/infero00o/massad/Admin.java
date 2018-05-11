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
    //Variable to be used within Admin
    //Creates a new array list to store the admin ids passed by the QRScanner
    public ArrayList admin_ids = new ArrayList();
    //Stores the telephone number passed by the QRScanner
    public String tel;
    //Stores the location passed by the QRScanner
    public String location;
    //Stores the UUID of the device passed by the QRScanner
    public String uuid;
    //Stores the senderID of any messages that are recieved
    public String senderID;
    //Creates a new instance of the PeerAdapter class used to store the list of connected peers
    public PeerAdapter peerAdapter = new PeerAdapter(new ArrayList < > ());

    //When the application starts anything in this method will execute first
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Creates a bundle and retrieves the data sent by the QRScanner
        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        location = extras.getString("location");
        admin_ids = extras.getStringArrayList("admin_ids");

        //Creates a new RecyclerView to display the connected peers
        //The adapter that holds the peers is set as the peerAdapter
        RecyclerView peerRecyclerView = findViewById(R.id.peerRecyclerView);
        RecyclerView.LayoutManager peerLayoutManager;
        peerRecyclerView.setAdapter(peerAdapter);

        //Sets the layout of the peerRecyclerView to linear
        peerLayoutManager = new LinearLayoutManager(this);
        peerRecyclerView.setLayoutManager(peerLayoutManager);

        //Calls the method below to start Bridgefy messageListener and
        //stateListener
        startBridgefy();

        //Creates a new textview t used to control the user information displayed
        final TextView t = findViewById(R.id.userInfoText);
        //Sets the text of the textview t to the user UUID and their location
        t.setText("You are admin ID: " + uuid + "\n" + "Your location: " + location);

        //Creates a button used to control the fire button
        final Button fire = findViewById(R.id.fire);
        //Sets the buttons visibility to visible
        fire.setVisibility(View.VISIBLE);
        //Creates a onClick listener for the fire button
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            //Calls the fire method when the button is pressed
            public void onClick(View view) {
                fire();
            }
        });

        //Creates a button used to control the flood button
        final Button flood = findViewById(R.id.flood);
        //Sets buttons visibility to visible
        flood.setVisibility(View.VISIBLE);
        //Creates an onClick listener for the flood button
        flood.setOnClickListener(new View.OnClickListener() {
            //Calls the flood method when the button is pressed
            @Override
            public void onClick(View view) {
                flood();
            }
        });

        //Creates a button used to control the custom message button
        final Button custom = findViewById(R.id.custom);
        //Sets the buttons visibility to visible
        custom.setVisibility(View.VISIBLE);
        //Creates an onClick listener for the button
        custom.setOnClickListener(new View.OnClickListener() {
            //Calls the custom method when pressed
            @Override
            public void onClick(View view) {
                custom();
            }
        });
        //Creates a button used to control the active shooter button
        final Button active = findViewById(R.id.active_shooter);
        //Sets the buttons visibility to true
        active.setVisibility(View.VISIBLE);
        //Creates an onClick listener for the button
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            //Calls the active method when pressed
            public void onClick(View view) {
                active();
            }
        });


    }

    //Starts the Bridgefy messageListener and stateListener
    //Creates a new configuration for Bridgefy to use
    //Sets the Bluetooth performance mode to High
    //Sets encryption as true to use RSA message encryption
    //Displays a start message to signal Bridgefy has been started
    public void startBridgefy() {
        Config.Builder builder = new Config.Builder();
        builder.setEnergyProfile(BFEnergyProfile.HIGH_PERFORMANCE);
        builder.setEncryption(true);
        Bridgefy.start(messageListener, stateListener);
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();

    }
    //Creates the messageListener which listens for incoming messages
    private MessageListener messageListener = new MessageListener() {
        @Override
        //When a message is recieved
        public void onMessageReceived(Message message) {
            //If the message conatins a alerType integer value
            if (message.getContent().containsKey("alertType")) {
                //Get the integer value from the message
                double alertType = (double) message.getContent().get("alertType");
                //If the alertType integer value is 9 == device is outside
                if (alertType == 9) {
                    //Get the peers position in the peerAdapter using the sender UUID to find it
                    int position = peerAdapter.getPeerPosition(message.getSenderId());
                    //If its position is found
                    if (position > -1) {
                        //Create a new peer and assign the peer to remove values to this
                        Peer peer = peerAdapter.peers.get(position);
                        //Set connected to false
                        peer.setConnected(false);
                        //Set outside to true
                        peer.setOutside(true);
                        //Reset its position
                        peerAdapter.peers.set(position, peer);
                        //Notify the RecyclerView of the change
                        peerAdapter.notifyItemChanged(position);
                    }
                    //Notify the user that the device has exit the building
                    Toast.makeText(getApplicationContext(), message.getSenderId().toString() + " has exit the building", Toast.LENGTH_LONG).show();
                    //If the alertType integer is 5 == custom message
                } else if (alertType == 5) {

                    //Get the title of the message
                    String title = (String) message.getContent().get("title");
                    //Get the message text
                    String text = (String) message.getContent().get("text");
                    //Get the sender UUID
                    String uuid = message.getSenderId();
                    //Create a new intent to start the directMessage class
                    Intent intent = new Intent(getApplicationContext(), directMessage.class);
                    //Send the collected variables to this class
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("senderID", uuid);
                    //Start the directMessage class activity
                    startActivity(intent);

                    //If the alertType integer is 6 == remove device from network
                } else if (alertType == 6) {
                    //Stop Bridgefy messageListener and stateListener
                    Bridgefy.stop();
                    //Finish the running activity
                    finish();
                    //Move the activity out of view to the user
                    moveTaskToBack(true);
                    //Kill any massAd running processes
                    android.os.Process.killProcess(android.os.Process.myPid());
                    //Exit the application
                    System.exit(1);

                }
            } else {
                //If no alertType integer discovered
                //The peer has sent a detection message
                //Create a new instance of the peer class with collected UUID, MAKE and MODEL from the message
                Peer peer = new Peer((String) message.getContent().get("ID"), (String) message.getContent().get("Make"), (String) message.getContent().get("Model"));
                //If the status of the peer is 1 set connected to true
                if ((double) message.getContent().get("Status") == 1) {
                    peer.setConnected(Boolean.TRUE);
                }
                //If the admin_ids arraylist scanned from the QR Code contains the message senders UUID then
                //Set the status of this peer to admin else set the admin status to false
                if (admin_ids.contains(message.getSenderId()) && (double) message.getContent().get("Admin") == 1) {
                    peer.setAdmin(Boolean.TRUE);
                } else {
                    peer.setAdmin(false);
                }
                //Set the outside status of the peer to false
                peer.setOutside(false);
                //Add the peer to the adapter
                peerAdapter.addPeer(peer);


            }
        }



        //When an emergency broadcast message is recieved
        public void onBroadcastMessageReceived(Message message) {
            //Ge the alertType integer from the message
            int alertType = (int) message.getContent().get("alertType");
            //Get the sender UUID from the message
            senderID = message.getSenderId();


            //If the admin_ids arraylist contains the sender UUID
            //The sender therefore has admin status
            if (admin_ids.contains(senderID)) {
                //If the alert type does not equal 3 == not a custom message
                if (alertType != 3) {
                    //Create a new intent to start the Alert class
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    //Send the SENDER ID, ALERT TYPE AND TEL(retrieved from QR Code) to Alert class
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TEL", tel);
                    //Start the Alert class activity
                    startActivity(intent);
                }
                //If the alertType == 3 == custom message
                else {
                    //Get the title and text of the message
                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    //Create an intent to start the Alert class
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    //Send the SENDER ID, ALERT TYPE, TITLE, MESSAGE AND TEL(retrieved from QR Code) to Alert class
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("TEL", tel);
                    //Start the Alert class activity
                    startActivity(intent);
                }

            }
        }

    };
    //Creates the stateListener to listener for devices in range
    private StateListener stateListener = new StateListener() {
        //When a device is connected to the network
        @Override
        public void onDeviceConnected(final Device device, Session session) {

            //Creat a HashMap and insert device UUID, MAKE, MODEL
            //ADMIN status if the admin_ids contains this devices UUID
            //Set the connected status to 1
            //Send the message to the connected device
            HashMap < String, Object > map = new HashMap < > ();
            map.put("ID", uuid);
            map.put("Make", Build.MANUFACTURER);
            map.put("Model", Build.MODEL);
            if (admin_ids.contains(uuid)) {
                map.put("Admin", 1);
            } else {
                map.put("Admin", 0);
            }
            map.put("Status", 1);
            device.sendMessage(map);
        }
        //If a device disconnects
        //Remove the device from the peerAdapter
        @Override
        public void onDeviceLost(Device device) {
            peerAdapter.removePeer(device);
        }
        //If the stateListener fails to start log the error
        //If the error is insufficient permissions ask the user for permission
        @Override
        public void onStartError(String message, int errorCode) {
            Log.e("Main", "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(Admin.this,
                        new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 0);
            }
        }
    };



    //For the Fire Flood and Active shooter methods below
    //Set the unique alertType to that corresponding to the alert
    //Create a HashMap to send
    //Insert into the HashMap the alertType integer, and the make and model of the device
    //The devices UUID is inserted automatially by the Bridgefy message builder.
    //The content of the message is set as the HashMap created
    //The message is broadcast using the Bridgefy.sendBroadcastMessage method
    public void fire() {
        int alertType = 1;
        HashMap < String, Object > content = new HashMap < > ();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }

    public void flood() {
        int alertType = 2;
        HashMap < String, Object > content = new HashMap < > ();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }
    //Creat an intent to start the customMessage class, then start its activity
    public void custom() {
        Intent intent = new Intent(getApplicationContext(), customMessage.class);
        startActivity(intent);
    }


    public void active() {
        int alertType = 4;
        HashMap < String, Object > content = new HashMap < > ();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }
}
//PeerAdapter class holds the peers connected to the network in a List<> called peers
class PeerAdapter extends RecyclerView.Adapter < PeerAdapter.PeerViewHolder > {
    public final List < Peer > peers;

    //Constructor
    PeerAdapter(List < Peer > peers) {
        this.peers = peers;
    }

    //Gets the number of peers in the peers list
    @Override
    public int getItemCount() {
        return peers.size();
    }

    //Checks if the peer list contains the peer if it does place it in its orginal position else
    //Add the peer to the end of the peer list
    //notify the peer RecylcerView
    void addPeer(Peer peer) {
        int position = getPeerPosition(peer.getUuid());
        if (position > -1) {
            peers.set(position, peer);
            notifyItemChanged(position);
        } else {
            peers.add(peer);
            notifyItemChanged(peers.size() - 1);
        }
    }
    //Removes the peer from the peer list
    //Finds its position using the peer UUID
    //If the peer is found in the peer list
    //Set connected to false
    //Notify the peer RecyclerView
    void removePeer(Device lostPeer) {
        int position = getPeerPosition(lostPeer.getUserId());
        if (position > -1) {
            Peer peer = peers.get(position);
            peer.setConnected(false);
            peers.set(position, peer);
            notifyItemChanged(position);
        }
    }
    //Loops through the peer list checking each peer and comparing its UUID to the peer to be found
    //Returns the peers position if found else returns -1
    public int getPeerPosition(String peerId) {
        for (int i = 0; i < peers.size(); i++) {
            if (peers.get(i).getUuid().equals(peerId))
                return i;
        }
        return -1;
    }
    //Used to display the peer in the RecyclerView
    //Sets the view as the peer_row.xml
    //Returns the display
    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.peer_row, parent, false);
        return new PeerViewHolder(view);
    }

    //Binds the peer viewholders in the recycler view
    @Override
    public void onBindViewHolder(final PeerViewHolder peerHolder, int position) {
        //Sets the peer position in the view
        peerHolder.setPeer(peers.get(position));
        //Creates an onClick listener for each peer in the View
        peerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            //When a peer is clicked
            public void onClick(View view) {
                //Create a new popup menu based on options_menu.xml
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                //Open the options menu
                popup.inflate(R.menu.options_menu);
                //Sets a onClick listener for each item in the menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //If message is clicked
                            case R.id.menu1:
                                //Create a new intent to start the sendDirectMessage class
                                Intent intent = new Intent(view.getContext(), sendDirectMessage.class);
                                //Get the UUID of the peer to message
                                String uuid = peers.get(position).getUuid();
                                //Send this UUID to the sendDirectMessage class
                                intent.putExtra("UUID", uuid);
                                //Start the sendDirectMessage class activity
                                view.getContext().startActivity(intent);
                                break;
                            //If remove is clicked
                            case R.id.menu2:
                                //Create a new HashMap and insert the alertType 6
                                HashMap < String, Object > content = new HashMap < > ();
                                content.put("alertType", 6);
                                //Get the UUID of the peer to remove
                                uuid = peers.get(position).getUuid();

                                //Set the content of the message as the HashMap
                                //Build and send the message directly to the peer using sendMessage method.
                                com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
                                builder.setContent(content);
                                builder.setReceiverId(uuid);
                                Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
                                break;
                        }
                        return false;
                    }
                });
                //Shows the popup menu when the peer is clicked
                popup.show();
            }
        });
    }

    //Displays the peers within the RecyclerView
    public class PeerViewHolder extends RecyclerView.ViewHolder {
        //Creates TextViews for the peer data
        //Creates a new instance of peer to hold the peer
        final TextView peerID;
        final TextView peerMake;
        final TextView peerModel;
        final TextView peerStatus;
        final TextView peerAdmin;
        Peer peer;
        //Finds the textviews within the interface and assigns them to the variables above
        PeerViewHolder(View view) {
            super(view);
            peerID = view.findViewById(R.id.peerID);
            peerMake = view.findViewById(R.id.peerMake);
            peerModel = view.findViewById(R.id.peerModel);
            peerStatus = view.findViewById(R.id.peerStatus);
            peerAdmin = view.findViewById(R.id.peerAdmin);

        }
        //Adds the peer data to the display
        //By setting the text of the textview variables
        //If the peer is connected then set the status text to connected, else set is to disconnected
        //If the peer is outside (the user has pressed the exit building button) set the status text to outside
        //If the peer has admin status set the status text to admin
        void setPeer(Peer peer) {
            this.peer = peer;
            peerID.setText(peer.getUuid());
            peerMake.setText(peer.getMake());
            peerModel.setText(peer.getModel());
            if (peer.getConnected()) {
                peerStatus.setText("Connected");
            } else {
                peerStatus.setText("Disconnected");
            }
            if (peer.getOutside()) {
                peerStatus.setText("Outside");
            }
            if (peer.getAdmin()) {
                peerAdmin.setText("Admin");
            } else {
                peerAdmin.setText("User");
            }
        }



    }

}

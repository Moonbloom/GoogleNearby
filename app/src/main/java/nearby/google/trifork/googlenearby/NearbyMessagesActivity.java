package nearby.google.trifork.googlenearby;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyMessagesActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "NERDBY - Messages";

    private GoogleApiClient mGoogleApiClient;
    private Message mActiveMessage;
    private MessageListener mMessageListener;
    private SubscribeOptions options;

    private List<Message> activeMessages = new ArrayList<>();

    private NearbyMessagesAdapter nearbyMessagesAdapter;

    @BindView(R.id.listview)
    ListView listView;

    //region Android Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        nearbyMessagesAdapter = new NearbyMessagesAdapter(this, activeMessages);
        listView.setAdapter(nearbyMessagesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NearbyMessagesActivity.this, new String(activeMessages.get(position).getContent()), Toast.LENGTH_LONG).show();
            }
        });

        Strategy strategy = new Strategy.Builder()
                .setTtlSeconds(10)
                .build();

        options = new SubscribeOptions.Builder()
                .setStrategy(strategy)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Found message: " + messageAsString);

                activeMessages.add(message);
                nearbyMessagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);

                activeMessages.remove(message);
                nearbyMessagesAdapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onStop() {
        unpublish();
        unsubscribe();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }
    //endregion

    //region GoogleApiClient
    @SuppressLint("HardwareIds")
    @Override
    public void onConnected(Bundle connectionHint) {
        publish(Utils.getNameFromDeviceId(this));
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended - " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed - " + connectionResult);
    }
    //endregion

    //region Private methods
    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes());
        Nearby.Messages.publish(mGoogleApiClient, mActiveMessage);
    }

    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.Messages.unpublish(mGoogleApiClient, mActiveMessage);
            mActiveMessage = null;
        }
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }
    //endregion
}
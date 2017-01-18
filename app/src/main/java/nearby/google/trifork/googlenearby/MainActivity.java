package nearby.google.trifork.googlenearby;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.EddystoneUid;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private GoogleApiClient mGoogleApiClient;
    private Message mActiveMessage;
    private MessageListener mMessageListener;
    private SubscribeOptions options;

    private List<Message> activeMessages = new ArrayList<>();

    private NearbyAdapter nearbyAdapter;

    @BindView(R.id.listview)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        nearbyAdapter = new NearbyAdapter(this, activeMessages);
        listView.setAdapter(nearbyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, new String(activeMessages.get(position).getContent()), Toast.LENGTH_LONG).show();
            }
        });

        Strategy strategy = new Strategy.Builder()
                .build();

        MessageFilter messageFilter = new MessageFilter.Builder()
                .includeAllMyTypes()
                .includeEddystoneUids("f7826da6bc5b71e0893e", null /* any instance */)
                .build();

        options = new SubscribeOptions.Builder()
                .setStrategy(strategy)
                .setFilter(messageFilter)
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
                nearbyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);

                activeMessages.remove(message);
                nearbyAdapter.notifyDataSetChanged();
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

    @Override
    public void onConnected(Bundle connectionHint) {
        publish("KHL");
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
}
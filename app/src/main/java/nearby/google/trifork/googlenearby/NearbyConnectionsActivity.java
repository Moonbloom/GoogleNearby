package nearby.google.trifork.googlenearby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyConnectionsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Connections.ConnectionRequestListener, Connections.MessageListener, Connections.EndpointDiscoveryListener {

    private static final String TAG = "NERDBY - Connections";
    private static int[] NETWORK_TYPES = {ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_ETHERNET};

    private GoogleApiClient mGoogleApiClient;

    private List<Endpoint> endpoints = new ArrayList<>();

    private NearbyConnectionsAdapter nearbyConnectionsAdapter;

    @BindView(R.id.listview)
    ListView listView;

    //region Android Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        nearbyConnectionsAdapter = new NearbyConnectionsAdapter(this, endpoints);
        listView.setAdapter(nearbyConnectionsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Endpoint endpoint = endpoints.get(position);
                Toast.makeText(NearbyConnectionsActivity.this, endpoint.endpointId, Toast.LENGTH_LONG).show();

                String payload = "LOL";
                Nearby.Connections.sendReliableMessage(mGoogleApiClient, endpoint.endpointId, payload.getBytes());
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.CONNECTIONS_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onStop() {
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
        if (Utils.getNameFromDeviceId(this).equals(Utils.ESO)) {
            startDiscovery();
        } else {
            startAdvertising();
        }
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

    //region Nearby Connections API
    @Override
    public void onConnectionRequest(final String remoteEndpointId, String remoteDeviceId, final String remoteEndpointName, byte[] payload) {
        byte[] myPayload = null;
        // Automatically accept all requests
        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, remoteEndpointId, myPayload, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Toast.makeText(NearbyConnectionsActivity.this, "Connected to " + remoteEndpointName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NearbyConnectionsActivity.this, "Failed to connect to: " + remoteEndpointName, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onEndpointFound(String endpointId, String deviceId, String serviceId, String endpointName) {
        Endpoint endpoint = new Endpoint(endpointId, deviceId, serviceId, endpointName);
        endpoints.add(endpoint);
        nearbyConnectionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEndpointLost(String endpointId) {
        Iterator<Endpoint> iterator = endpoints.iterator();

        while (iterator.hasNext()) {
            Endpoint end = iterator.next();
            if (end.endpointId.equals(endpointId)) {
                iterator.remove();
            }
        }
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {

    }

    @Override
    public void onDisconnected(String s) {
        Log.d(TAG, "onDisconnected");
    }
    //endregion

    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (int networkType : NETWORK_TYPES) {
            NetworkInfo info = connManager.getNetworkInfo(networkType);
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    private void startDiscovery() {
        if (!isConnectedToNetwork()) {
            Toast.makeText(this, "NO INTERNET", Toast.LENGTH_LONG).show();
            return;
        }

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 0L; // 0 = infinite

        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(mGoogleApiClient, getPackageName(), DISCOVER_TIMEOUT, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            // Device is discovering
                        } else {
                            int statusCode = status.getStatusCode();
                            Log.d(TAG, "onResult discovery failed: " + statusCode);
                        }
                    }
                });
    }

    private void startAdvertising() {
        if (!isConnectedToNetwork()) {
            Toast.makeText(this, "NO INTERNET", Toast.LENGTH_LONG).show();
            return;
        }

        // Advertising with an AppIdentifer lets other devices on the
        // network discover this application and prompt the user to
        // install the application.
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // The advertising timeout is set to run indefinitely
        // Positive values represent timeout in milliseconds
        long DISCOVER_TIMEOUT = 0L; // 0 = infinite

        Nearby.Connections.startAdvertising(mGoogleApiClient, null, appMetadata, DISCOVER_TIMEOUT, this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // Device is advertising
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                    Log.d(TAG, "onResult advertising failed: " + statusCode);
                }
            }
        });
    }
}
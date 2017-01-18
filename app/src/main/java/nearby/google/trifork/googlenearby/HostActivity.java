package nearby.google.trifork.googlenearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HostActivity extends AppCompatActivity {

    @OnClick(R.id.messages)
    public void messagesClick() {
        Intent intent = new Intent(this, NearbyMessagesActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.connections)
    public void connectionsClick() {
        Intent intent = new Intent(this, NearbyConnectionsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        ButterKnife.bind(this);
    }
}

package nearby.google.trifork.googlenearby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NearbyConnectionsAdapter extends BaseAdapter {

    private Context context;
    private List<Endpoint> endpoints;

    public NearbyConnectionsAdapter(Context context, List<Endpoint> endpoints) {
        this.context = context;
        this.endpoints = endpoints;
    }

    @Override
    public int getCount() {
        return endpoints != null ? endpoints.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return endpoints != null ? endpoints.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nearby_connection, null, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(endpoints.get(position).endpointId);

        return convertView;
    }
}

package nearby.google.trifork.googlenearby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.nearby.messages.Message;

import java.util.List;

public class NearbyMessagesAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messages;

    public NearbyMessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return messages != null ? messages.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nearby_message, null, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text);
        textView.setText(new String(messages.get(position).getContent()));

        return convertView;
    }
}

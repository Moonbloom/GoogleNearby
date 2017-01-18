package nearby.google.trifork.googlenearby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.nearby.messages.EddystoneUid;
import com.google.android.gms.nearby.messages.Message;

import java.util.List;

public class NearbyAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messages;

    public NearbyAdapter(Context context, List<Message> messages) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nearby, null, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text);

        Message message = messages.get(position);
        if (Message.MESSAGE_NAMESPACE_RESERVED.equals(message.getNamespace())
                && Message.MESSAGE_TYPE_EDDYSTONE_UID.equals(message.getType())) {
            EddystoneUid eddystoneUid = EddystoneUid.from(message);
            textView.setText(new String(eddystoneUid.toString()));
        }else {
            textView.setText(new String(message.getContent()));
        }

        return convertView;
    }
}

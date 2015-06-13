package drawer;

/**
 * Created by mifercre on 18/02/15.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shipeer.app.R;

public class NavigationAdapter extends BaseAdapter {
    private Activity activity;
    ArrayList<String> arrayitms;
    //ArrayList<Integer> arrayIcons;

    public NavigationAdapter(Activity activity,ArrayList<String>  listarry) {//, ArrayList<Integer> arrayIcons) {
        super();
        this.activity = activity;
        this.arrayitms = listarry;
        //this.arrayIcons = arrayIcons;
    }

    @Override
    public Object getItem(int position) {
        return arrayitms.get(position);
    }

    public int getCount() {
        return arrayitms.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class Row {
        TextView titulo_itm;
        //ImageView icon_itm;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Row row;
        View view = convertView;
        LayoutInflater inflator = activity.getLayoutInflater();
        String itm = arrayitms.get(position);
        if(convertView == null) {
            row = new Row();
            view = inflator.inflate(R.layout.drawer_itm, null);
            view.setTag(row);
        } else {
            row = (Row) view.getTag();
        }
        row.titulo_itm = (TextView) view.findViewById(R.id.title_item);
        row.titulo_itm.setText(itm);

        //row.icon_itm = (ImageView) view.findViewById(R.id.icon_item);
        //row.icon_itm.setImageResource(arrayIcons.get(position));
        return view;
    }
}

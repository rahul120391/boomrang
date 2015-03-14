package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import Boomerang.R;
import commonutils.UIutill;

/**
 * Created by rahul on 3/11/2015.
 */
public class SideBarAdapter extends BaseAdapter {
    int images[]={R.drawable.iv_sideclose,R.drawable.iv_sideprofile,R.drawable.iv_myfiless,R.drawable.iv_sidesync,R.drawable.iv_dashboard,R.drawable.iv_contactus,R.drawable.iv_logout};
    String drawer_text[]={"Close","Profile","My Files","Sync Files","Dashboard","Contact us","Logout"};
    Context context;
    LayoutInflater inflator;
    TextView tv_itemname;
    ImageView iv_itemimage;
    public  SideBarAdapter(Context context){
        this.context=context;
        inflator=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflator.inflate(R.layout.navigationdrawer_row_item,null);
        }
        iv_itemimage=(ImageView)convertView.findViewById(R.id.iv_itemimage);
        tv_itemname=(TextView)convertView.findViewById(R.id.tv_itemname);
        tv_itemname.setTypeface(UIutill.SetFont(context,"segoeuilght.ttf"));
        iv_itemimage.setImageResource(images[position]);
        tv_itemname.setText(drawer_text[position]);
        return convertView;
    }
}

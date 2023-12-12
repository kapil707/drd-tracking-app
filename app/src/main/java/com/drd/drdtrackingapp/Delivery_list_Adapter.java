package com.drd.drdtrackingapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Delivery_list_Adapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Delivery_list_get_or_set> movieItems;

    public Delivery_list_Adapter(Context context, List<Delivery_list_get_or_set> arraylist)
    {
        this.context = context;
        this.movieItems = arraylist;
    }
 
    @Override
	public int getCount() {
		return movieItems.size();
	}
    
   
    @Override
	public View getView(int position, View view, ViewGroup parent) 
    {
		// TODO Auto-generated method stub
       
        LayoutInflater abc = ((Activity) context).getLayoutInflater();
		View itemView = abc.inflate(R.layout.delivery_list_item, null,true);
		final Delivery_list_get_or_set m = movieItems.get(position);

        LinearLayout select_chemist_LinearLayout = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout);
        TextView select_b_lbl1 = (TextView) itemView.findViewById(R.id.select_b_lbl1);
        TextView select_b_lbl2 = (TextView) itemView.findViewById(R.id.select_b_lbl2);


        LinearLayout select_chemist_LinearLayout1 = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout1);
        TextView select_w_lbl1 = (TextView) itemView.findViewById(R.id.select_w_lbl1);
        TextView select_w_lbl2 = (TextView) itemView.findViewById(R.id.select_w_lbl2);

        int intid = 0;
        intid = Integer.valueOf(m.intid());
        if(intid%2==0)
        {
            select_chemist_LinearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            select_chemist_LinearLayout1.setVisibility(View.VISIBLE);
        }

        select_b_lbl1.setText("Tagno : " + m.mytagno());
        select_b_lbl2.setText(m.mydate() +" - "+m.mytime());

        select_w_lbl1.setText("Tagno : " + m.mytagno());
        select_w_lbl2.setText(m.mydate() +" - "+m.mytime());

        return itemView;
    }

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
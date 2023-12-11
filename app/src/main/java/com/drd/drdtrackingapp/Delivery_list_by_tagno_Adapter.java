package com.drd.drdtrackingapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Delivery_list_by_tagno_Adapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Delivery_list_by_tagno_get_or_set> arrayitems;

    public Delivery_list_by_tagno_Adapter(Context context, List<Delivery_list_by_tagno_get_or_set> arraylist)
    {
        this.context = context;
        this.arrayitems = arraylist;
    }
 
    @Override
	public int getCount() {
		return arrayitems.size();
	}
    
   
    @Override
	public View getView(int position, View view, ViewGroup parent) 
    {
		// TODO Auto-generated method stub
       
        LayoutInflater abc = ((Activity) context).getLayoutInflater();
		View itemView = abc.inflate(R.layout.delivery_list_by_tagno_item, null,true);
		final Delivery_list_by_tagno_get_or_set m = arrayitems.get(position);

        LinearLayout select_chemist_LinearLayout = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout);
        TextView select_chemist_name = (TextView) itemView.findViewById(R.id.select_chemist_name);
        TextView select_chemist_altercode = (TextView) itemView.findViewById(R.id.select_chemist_altercode);
        TextView select_amt = (TextView) itemView.findViewById(R.id.select_amt);
        TextView select_gstvno = (TextView) itemView.findViewById(R.id.select_gstvno);
        TextView select_medicine = (TextView) itemView.findViewById(R.id.select_medicine);


        LinearLayout select_chemist_LinearLayout1 = (LinearLayout) itemView.findViewById(R.id.select_chemist_LinearLayout1);
        TextView select_chemist_name1 = (TextView) itemView.findViewById(R.id.select_chemist_name1);
        TextView select_chemist_altercode1 = (TextView) itemView.findViewById(R.id.select_chemist_altercode1);
        TextView select_amt1 = (TextView) itemView.findViewById(R.id.select_amt1);
        TextView select_gstvno1 = (TextView) itemView.findViewById(R.id.select_gstvno1);
        TextView select_medicine1 = (TextView) itemView.findViewById(R.id.select_medicine1);
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

        select_chemist_name.setText(m.gstvno());
        select_chemist_altercode.setText(m.mydate());
        select_gstvno.setText(m.chemist_code());
        select_amt.setText(m.amount());
        //select_medicine.setText(m.medicine_items());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            select_medicine.setText(Html.fromHtml(m.medicine_items(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            select_medicine.setText(Html.fromHtml(m.medicine_items()));
        }

        select_chemist_name1.setText(m.gstvno());
        select_chemist_altercode1.setText(m.mydate());
        select_gstvno1.setText(m.chemist_code());
        select_amt1.setText(m.amount());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            select_medicine1.setText(Html.fromHtml(m.medicine_items(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            select_medicine1.setText(Html.fromHtml(m.medicine_items()));
        }

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
package ap.edu.ingloriousbrewstars;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SingleListAdapter extends BaseAdapter{
	Context ctx;
	LayoutInflater lInflater;
	List<String> data;

	SingleListAdapter(Context context, List<String> data) {
		ctx = context;
		this.data = data;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = lInflater.inflate(R.layout.single_choice_items, parent, false);
		}

		((TextView) view.findViewById(R.id.singleitemId)).setText(data.get(position));
		
		return view;
	}
}
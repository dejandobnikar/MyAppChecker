package dd.appchecker.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dd.appchecker.R;
import dd.appchecker.aws.data.ApkInfo;

/**
 * Created by Dejan on 23.10.2014.
 */
public class MainAdapter extends BaseAdapter {

    private List<ApkInfo> mList;
    private LayoutInflater mInflater;

    public MainAdapter(Context c) {
        mList = new ArrayList<ApkInfo>();
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<ApkInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ApkInfo getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.main_list_item, viewGroup, false);

            ViewHolder vh = new ViewHolder();
            vh.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            vh.tvSubtitle = (TextView) convertView.findViewById(R.id.tvSubtitle);
            vh.tvDetails = (TextView) convertView.findViewById(R.id.tvDetails);
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv);

            convertView.setTag(vh);
        }

        ViewHolder vh = (ViewHolder) convertView.getTag();

        ApkInfo info = getItem(i);

        vh.tvTitle.setText(info.getApkName());
        vh.tvSubtitle.setText(info.getBranchName() + " | Ver: " + info.getVersion() );
        vh.tvDetails.setText("B: " + info.getBuildNumber() + " | " + info.getDate());

        if (info.exsistsOnFileSystem()) {
            vh.imageView.setImageResource(R.drawable.ok);
        }
        else {
            vh.imageView.setImageResource(R.drawable.missing);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvDetails;
        ImageView imageView;
    }
}

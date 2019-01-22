package com.uroad.dubai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.uroad.dubai.R;
import com.uroad.dubai.model.ContactMDL;
import java.util.List;


/**
 * @author: xp
 * @date: 2017/7/19
 */

public class SortCityAdapter extends RecyclerView.Adapter<SortCityAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<ContactMDL> mData;
    private Context mContext;

    private static OnItemClickListener mOnItemClickListener;

    public SortCityAdapter(Context context, List<ContactMDL> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    @Override
    public SortCityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_city_name, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SortCityAdapter.ViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getName());
        holder.tvPhone.setText(mData.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //**************************************************************

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name1);
            tvPhone = itemView.findViewById(R.id.tv_phone1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<ContactMDL> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}

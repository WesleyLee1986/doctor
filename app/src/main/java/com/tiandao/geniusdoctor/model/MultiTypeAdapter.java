package com.tiandao.geniusdoctor.model;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MultiTypeAdapter<T> extends BaseAdapter {
    private Context mContext;
    private List<T> mDataList;
    private List<ListViewType<T>> mViewTypes = new ArrayList<>();

    public MultiTypeAdapter(Context context) {
        mContext = context;
    }

    public MultiTypeAdapter(Context context, List<T> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setData(List<T> data) {
        mDataList = data;
    }

    public void addViewType(ListViewType<T> viewType) {
        mViewTypes.add(viewType);
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return (position >= 0 && position < mDataList.size()) ? mDataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        int count = mViewTypes.size();
        return count > 0 ? count : super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < mViewTypes.size(); ++i) {
            ListViewType<T> viewType = mViewTypes.get(i);
            if (viewType.isThisType(getItem(position), position)) {
                return i;
            }
        }

        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mViewTypes.size() == 0) {
            throw new IllegalStateException("call addViewType first");
        }

        ViewHolder viewHolder;
        ListViewType<T> viewType = mViewTypes.get(getItemViewType(position));

        if (convertView == null) {
            int layoutId = viewType.getItemViewLayoutId();
            View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            viewHolder = new ViewHolder(mContext, view, layoutId, position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.setPosition(position);
        }

        viewType.convert(viewHolder, getItem(position), position);

        return viewHolder.getConvertView();
    }


    public interface ListViewType<T> {

        int getItemViewLayoutId();

        boolean isThisType(T item, int position);

        void convert(ViewHolder holder, T item, int position);
    }
}


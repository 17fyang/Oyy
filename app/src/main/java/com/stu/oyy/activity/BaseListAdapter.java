package com.stu.oyy.activity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2020/12/22 14:12
 * @Description:
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    private List<T> itemData;
    private int layoutResource;

    public BaseListAdapter(List<T> itemData, int layoutResource) {
        this.itemData = itemData;
        this.layoutResource = layoutResource;
    }

    @Override
    public int getCount() {
        return itemData == null ? 0 : itemData.size();
    }

    @Override
    public T getItem(int position) {
        return itemData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, layoutResource, position);
        bindView(holder, getItem(position));
        return holder.getItem();
    }

    /**
     * 子类实现，用来定义每个view的渲染方式
     *
     * @param holder
     * @param item
     */
    public abstract void bindView(ViewHolder holder, T item);

    public void setItemData(List<T> itemData) {
        this.itemData = itemData;
    }

    public List<T> getItemData() {
        return itemData;
    }

    /**
     * 封装每个item的holder
     */
    public static class ViewHolder {
        private View item;                  //该item的view对象
        private int position;               //对应的item游标

        private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
            View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
            this.item = convertView;
        }

        public static ViewHolder bind(Context context, View convertView, ViewGroup parent, int layoutRes, int position) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder(context, parent, layoutRes);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }

        public View getItem() {
            return item;
        }

        public int getPosition() {
            return position;
        }
    }
}
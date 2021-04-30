package com.stu.oyy.activity;

import android.widget.TextView;
import com.stu.oyy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2021/4/30 18:20
 * @Description:
 */
public class ProvinceAdapter extends BaseListAdapter<String> {
    public static final List<String> provinceList = new ArrayList<>();

    static {
        provinceList.add("广东省");
        provinceList.add("山东省");
        provinceList.add("湖北省");
        provinceList.add("湖南省");
        provinceList.add("北京市");
        provinceList.add("重庆市");
    }

    public ProvinceAdapter(int layoutResource) {
        super(provinceList, layoutResource);
    }

    @Override
    public void bindView(ViewHolder holder, String item) {
        TextView textView = holder.getItem().findViewById(R.id.province_name);
        textView.setText(item);
    }
}

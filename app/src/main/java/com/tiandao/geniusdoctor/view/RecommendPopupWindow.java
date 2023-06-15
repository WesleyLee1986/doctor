package com.tiandao.geniusdoctor.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.tiandao.geniusdoctor.R;
import com.tiandao.geniusdoctor.helper.Utils;
import com.tiandao.geniusdoctor.model.MultiTypeAdapter;
import com.tiandao.geniusdoctor.model.ViewHolder;

import database.DatabaseManager;
import database.model.IData;
import database.table.TableColumnDef;

public class RecommendPopupWindow {
    private PopupWindow popupWindow;
    private EditText editText;
    private TableColumnDef columnDef;
    private MultiTypeAdapter<IData> adapter;
    private boolean onItemSelect = false;
    private LinearLayout container;

    public RecommendPopupWindow(Context context) {
        ListView listView = new ListView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dp2px(context, 0.5f);
        lp.setMargins(margin, margin, margin, margin);
        listView.setLayoutParams(lp);

        adapter = new MultiTypeAdapter<>(context, null);
        adapter.addViewType(new FZHistoryEditInfoCell());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onItemSelect = true;
                editText.setText(adapter.getItem(position).getName());
            }
        });

        TextView emptyText = new TextView(context);
        emptyText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(context, 80)));
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setTextColor(context.getResources().getColor(R.color.title_text_color));
        emptyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        listView.setEmptyView(emptyText);

        container = new LinearLayout(context);
        container.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(listView);
        container.addView(emptyText);
        RadiusDrawable drawable = new RadiusDrawable(0, true);
        drawable.setStrokeColor(context.getResources().getColor(R.color.accent_color_alpha50));
        drawable.setStrokeWidth(Utils.dp2px(context, 0.5f));
        drawable.setFillColor(context.getResources().getColor(R.color.primary_background_color));
        container.setBackground(drawable);

        popupWindow = new PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(context,180.0f), false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void bindView(EditText editText, TableColumnDef tableColumnDef) {
        this.editText = editText;
        this.columnDef = tableColumnDef;

        this.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showPopupView();
                    adapter.setData(DatabaseManager.getInstance().getRecord(columnDef.getRelated_table_name(),""));
                    adapter.notifyDataSetChanged();
                } else {
                    popupWindow.dismiss();
                }
            }
        });

        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(onItemSelect){
                    onItemSelect = false;
                    return;
                }

                showPopupView();
                adapter.setData(DatabaseManager.getInstance().getRecord(columnDef.getRelated_table_name(),s.toString()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void showPopupView(){
        if(!popupWindow.isShowing()){
            popupWindow.setWidth(editText.getWidth());
            Rect rect = locateView(editText);
            popupWindow.showAtLocation(editText, Gravity.NO_GRAVITY, rect.left, rect.bottom);
        }
    }

    private static Rect locateView(View v) {
        int[] loc = {0, 0};
        Rect rect = new Rect();

        if (v != null) {
            v.getLocationInWindow(loc);
            rect.left = loc[0];
            rect.top = loc[1];
            rect.right = rect.left + v.getWidth();
            rect.bottom = rect.top + v.getHeight();
        }
        return rect;
    }

    private class FZHistoryEditInfoCell implements MultiTypeAdapter.ListViewType<IData> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.history_edit_info_item;
        }

        @Override
        public boolean isThisType(IData item, int position) {
            return true;
        }

        @Override
        public void convert(ViewHolder holder, IData item, int position) {
            TextView textView = holder.getView(R.id.text);
            textView.setText(item.getName());
            textView.setPadding(editText.getLeft(), 0, 0, 0);
        }
    }
}

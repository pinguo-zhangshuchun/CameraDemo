package kari.com.org.camerademo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ws-kari on 15-4-2.
 */
public final class ResolutionPopupWindow {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private ListView mListView;
    private List<Camera.Size> mSizes;
    private MyAdapter mAdapter;
    private Camera.Size mCurrSize;
    private OnPictureSizeChangeListener mListener;

    public ResolutionPopupWindow(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View mRootView = mInflater.inflate(R.layout.view_popupwindow, null);
        mListView = (ListView) mRootView.findViewById(R.id.popupwindow_listview);

        mPopupWindow = new PopupWindow(mRootView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                false);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public void setDataSource(List<Camera.Size> list) {
        if (mSizes == null) {
            mSizes = list;
            mAdapter = new MyAdapter();
            mListView.setAdapter(mAdapter);
        } else {
            mSizes = list;
        }
    }

    public View getContentView() {
        return mPopupWindow.getContentView();
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void setCurrCameraSize(Camera.Size size) {
        mCurrSize = size;
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setPictureSizeChangedListener(OnPictureSizeChangeListener listener) {
        mListener = listener;
    }

    public void showAtLocation(View v, int gravity, int x, int y) {
        mPopupWindow.showAtLocation(v, gravity, x, y);
    }

    public void showAsDropDown(View view) {
        mPopupWindow.showAsDropDown(view);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    final class MyAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public MyAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mSizes.size();
        }

        @Override
        public Object getItem(int position) {
            return mSizes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.view_choose_item, null);
                holder = new ViewHolder();
                holder.label = (TextView) convertView.findViewById(R.id.item_choose_label);
                holder.tv = (TextView) convertView.findViewById(R.id.item_choose_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String label = mSizes.get(position).width + " x " + mSizes.get(position).height;
            holder.tv.setText(label);

            if (mSizes.get(position).equals(mCurrSize)) {
                holder.label.setVisibility(View.VISIBLE);
            } else {
                holder.label.setVisibility(View.INVISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSizes.get(position).equals(mCurrSize)) {
                        mCurrSize = mSizes.get(position);
                        notifyDataSetChanged();
                        if (null != mListener) {
                            mListener.onChanged(mCurrSize);
                        }
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv;
            TextView label;
        }
    }

    public interface OnPictureSizeChangeListener {
        public void onChanged(Camera.Size size);
    }
}

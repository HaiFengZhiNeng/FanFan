package com.fanfan.novel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanfan.novel.utils.BaseRecyclerViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, H extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    protected static final String TAG = BaseRecyclerAdapter.class.getSimpleName();

    protected final Context context;

    protected int layoutResId;

    protected List<T> datas;

    private OnItemClickListener mOnItemClickListener = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public BaseRecyclerAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }


    public BaseRecyclerAdapter(Context context, int layoutResId, List<T> datas) {
        this.datas = datas == null ? new ArrayList<T>() : datas;
        this.context = context;
        this.layoutResId = layoutResId;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
        BaseRecyclerViewHolder vh = new BaseRecyclerViewHolder(view, mOnItemClickListener, mOnItemLongClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        T item = getItem(position);
        convert((H) viewHolder, item, position);
    }

    @Override
    public int getItemCount() {
        if (datas == null || datas.size() <= 0)
            return 0;

        return datas.size();
    }

    public T getItem(int position) {
        if (position >= datas.size()) return null;
        return datas.get(position);
    }

    public void clear() {
        if (datas == null || datas.size() <= 0)
            return;

        for (Iterator it = datas.iterator(); it.hasNext(); ) {

            T t = (T) it.next();
            int position = datas.indexOf(t);
            it.remove();
            notifyItemRemoved(position);
        }
    }

    public void removeItem(T t) {

        int position = datas.indexOf(t);
        datas.remove(position);
        notifyItemRemoved(position);
    }

//    public void removeItemPos(int position) {
//
//        datas.remove(position);
//        notifyItemRemoved(position);
//    }


//    public List<T> getDatas() {
//
//        return datas;
//    }

    public void addData(T t) {
        datas.add(datas.size(), t);
        notifyItemInserted(datas.size());
    }


//    public void addData(List<T> datas) {
//
//        addData(0, datas);
//    }

//    public void addData(int position, List<T> list) {
//        if (list != null && list.size() > 0) {
//            for (T t : list) {
//                datas.add(position, t);
//                notifyItemInserted(position);
//            }
//        }
//    }

    public void refreshData(List<T> list) {
        clear();
        if (list != null && list.size() > 0) {

            int size = list.size();
            for (int i = 0; i < size; i++) {
                datas.add(i, list.get(i));
                notifyItemInserted(i);
            }
        }
    }

    public void loadMoreData(List<T> list) {

        if (list != null && list.size() > 0) {

            int size = list.size();
            int begin = datas.size();
            for (int i = 0; i < size; i++) {
                datas.add(list.get(i));
                notifyItemInserted(i + begin);
            }
        }
    }


    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param viewHolder A fully initialized helper.
     * @param item       The item that needs to be displayed.
     */
    protected abstract void convert(H viewHolder, T item, int pos);


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

}

package com.hmkm1c.attendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.database.AttendItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private AdapterListListener listener;

    private Activity activity;
    private List<AttendItem> itemList = new ArrayList<>();
    private int selectedIndex = 0;

    public AdapterList(Activity activity, AdapterListListener listener)
    {
        super();

        this.activity = activity;
        this.listener = listener;

        itemList.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_list, parent, false);
        return new AdapterListHolder(activity, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position)
    {
        if(hldr == null) return;

        AttendItem item = (AttendItem)getItem(position);
        final AdapterListHolder holder = (AdapterListHolder) hldr;
        if (item == null)
        {
        }
        else
        {
            holder.textName.setText(item.name);
            holder.textStamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(item.stamp)));

        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder hldr)
    {
        super.onViewDetachedFromWindow(hldr);

    }

    @Override
    public int getItemCount()
    {
        return itemList.size();
    }

    public Object getItem(int position)
    {
        if (position >= 0 && position < itemList.size())
            return itemList.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position)
    {
        Object item = getItem(position);
        if(item != null)
        {
            return position;
        }
        else
        {
            return -1;
        }
    }

    public void setSelectedIndex(int index)
    {
        selectedIndex = index;

        notifyDataSetChanged();
    }

    public void update(String type)
    {
        itemList.clear();

        List<AttendItem> list = AttendItem.list(AttendItem.loadByType(type));
        if(list != null)
        {
            itemList.addAll(list);
        }

        notifyDataSetChanged();
    }

    public boolean removeById(long id)
    {
        for(int i = 0; i < itemList.size(); i++)
        {
            if(itemList.get(i).id == id)
            {
                itemList.remove(i);
                notifyItemRemoved(i);
                return true;
            }
        }
        return false;
    }

    public class AdapterListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        private TextView textName;
        private TextView textStamp;

        private FrameLayout layoutRow;

        public AdapterListHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            textName = view.findViewById(R.id.textName);
            textStamp = view.findViewById(R.id.textStamp);
            layoutRow = view.findViewById(R.id.layoutRow);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (listener == null) return;

            selectedIndex = getAdapterPosition();
            setSelectedIndex(selectedIndex);

            listener.onItemClick(AdapterList.this, v, selectedIndex);
        }
    }

    public interface AdapterListListener
    {
        void onItemClick(RecyclerView.Adapter adp, View view, int position);
    }

}


package com.hmkm1c.attendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmkm1c.attendance.objects.HomeItem;
import com.hmkm1c.attendance.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private AdapterHomeListener listener;

    private Activity activity;
    private List<HomeItem> itemList = new ArrayList<>();
    private int selectedIndex = 0;
    private int height = 100;
    private int width = 100;

    public AdapterHome(Activity activity, AdapterHomeListener listener)
    {
        super();

        this.activity = activity;
        this.listener = listener;

        itemList.clear();
        itemList.add(new HomeItem(HomeItem.Id.CHECK_IN,     "Check-in",         R.drawable.ic_check_in));
        itemList.add(new HomeItem(HomeItem.Id.CHECK_OUT,    "Check-out",        R.drawable.ic_check_out));
        itemList.add(new HomeItem(HomeItem.Id.SETTINGS,     "Settings",         R.drawable.ic_settings));
        itemList.add(new HomeItem(HomeItem.Id.ATTENDANCE,   "Attendance",       R.drawable.ic_attendance));
    }

    public void resize(int lw, int lh)
    {
        this.height = lh/2;
        this.width = lw/2;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_home, parent, false);
        return new AdapterHomeHolder(activity, view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position)
    {
        if(hldr == null) return;

        HomeItem item = (HomeItem)getItem(position);
        final AdapterHomeHolder holder = (AdapterHomeHolder) hldr;
        if (item == null)
        {
        }
        else
        {
            holder.layoutRow.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, this.height));

            //holder.layoutRow.setBackgroundResource(selectedIndex == position ? R.color.hilite : R.color.transparent);
            holder.textTitle.setText(item.title);
            holder.imageIcon.setImageResource(item.icon);
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

    public class AdapterHomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        private TextView textTitle;
        private ImageView imageIcon;
        private FrameLayout layoutRow;

        public AdapterHomeHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            textTitle = view.findViewById(R.id.textTitle);
            imageIcon = view.findViewById(R.id.imageIcon);
            layoutRow = view.findViewById(R.id.layoutRow);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (listener == null) return;

            selectedIndex = getAdapterPosition();
            setSelectedIndex(selectedIndex);

            listener.onItemClick(AdapterHome.this, v, selectedIndex);
        }
    }

    public interface AdapterHomeListener
    {
        void onItemClick(RecyclerView.Adapter adp, View view, int position);
    }

}


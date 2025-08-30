package com.example.mycalendertest;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> {
    private List<String> dataList = new ArrayList<>();
    private SwipeLayout openedLayout; // 当前打开的SwipeLayout

    public interface OnItemClickListener {
        void onItemClick(int position, String data);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public SwipeAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 重置状态
        if (holder.swipeLayout.isOpen()) {
            holder.swipeLayout.closeMenuImmediately();
        }

        // 设置内容
        holder.tvContent.setText(dataList.get(position));

        // 设置内容区域点击事件，点击时关闭已展开的menu或执行item点击
        holder.tvContent.setOnClickListener(v -> {
            if (openedLayout != null) {
                // 如果当前item已展开，则关闭
                if (openedLayout == holder.swipeLayout) {
                    openedLayout.closeMenu();
                    openedLayout = null;
                } else {
                    // 如果有其他item已展开，则关闭
                    openedLayout.closeMenu();
                }
            } else {
                // 没有menu展开，执行item点击事件
                int pos = holder.getAdapterPosition();
                if (itemClickListener != null && pos != RecyclerView.NO_POSITION && pos < dataList.size()) {
                    itemClickListener.onItemClick(pos, dataList.get(pos));
                }
            }
        });

        // 设置按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && pos < dataList.size()) {
                dataList.remove(pos);
                notifyItemRemoved(pos);
            }
        });

        // 移除itemView的点击事件（统一用tvContent的点击事件处理）
        holder.itemView.setOnClickListener(null);

        // 设置滑动监听
        holder.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
            @Override
            public void onMenuOpened(SwipeLayout swipeLayout) {
                if (openedLayout != null && openedLayout != swipeLayout) {
                    openedLayout.closeMenu();
                }
                openedLayout = swipeLayout;
            }

            @Override
            public void onMenuClosed(SwipeLayout swipeLayout) {
                if (openedLayout == swipeLayout) {
                    openedLayout = null;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvContent;
        Button btnDelete;
        Button btnTop;

        ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            tvContent = itemView.findViewById(R.id.tv_content);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnTop = itemView.findViewById(R.id.btn_top);
        }
    }
}

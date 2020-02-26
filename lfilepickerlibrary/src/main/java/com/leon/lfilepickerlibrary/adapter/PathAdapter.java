package com.leon.lfilepickerlibrary.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leon.lfilepickerlibrary.R;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.leon.lfilepickerlibrary.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

/**
 * 作者：Leon
 * 时间：2017/3/15 15:47
 */
public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder> {
    public interface OnItemClickListener {
        void click(int position);
    }

    public interface OnCancelChoosedListener {
        void cancelChoosed(CheckBox checkBox);
    }

    private final String TAG = "FilePickerLeon";
    private List<File> mListData;
    private Context mContext;
    public OnItemClickListener onItemClickListener;
    private FileFilter mFileFilter;
    private boolean[] mCheckedFlags;
    private boolean mMutilyMode;
    private int mIconStyle;
    private boolean mIsGreater;
    private long mFileSize;

    public PathAdapter(List<File> mListData, Context mContext, FileFilter mFileFilter, boolean mMutilyMode, boolean mIsGreater, long mFileSize) {
        this.mListData = mListData;
        this.mContext = mContext;
        this.mFileFilter = mFileFilter;
        this.mMutilyMode = mMutilyMode;
        this.mIsGreater = mIsGreater;
        this.mFileSize = mFileSize;
        mCheckedFlags = new boolean[mListData.size()];
    }

    @NonNull
    @Override
    public PathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.lfile_listitem, null);
        return new PathViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final PathViewHolder holder, final int position) {
        final File file = mListData.get(position);
        if (file.isFile()) {
            updateFileIconStyle(holder.ivType);
            holder.tvName.setText(file.getName());
            holder.tvDetail.setText(mContext.getString(R.string.lfile_FileSize) + " " + FileUtils.getReadableFileSize(file.length()));
            holder.cbChoose.setVisibility(View.VISIBLE);
        } else {
            updateFloaderIconStyle(holder.ivType);
            holder.tvName.setText(file.getName());
            //文件大小过滤
            List files = FileUtils.getFileList(file.getAbsolutePath(), mFileFilter, mIsGreater, mFileSize);
            if (files.size() == 0) {
                holder.tvDetail.setText("0 " + mContext.getString(R.string.lfile_LItem));
            } else {
                holder.tvDetail.setText(files.size() + " " + mContext.getString(R.string.lfile_LItem));
            }
            holder.cbChoose.setVisibility(View.GONE);
        }
        if (!mMutilyMode) {
            holder.cbChoose.setVisibility(View.GONE);
        }
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isFile()) {
                    holder.cbChoose.setChecked(!holder.cbChoose.isChecked());
                }
                onItemClickListener.click(position);
            }
        });
        holder.cbChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同步复选框和外部布局点击的处理
                onItemClickListener.click(position);
            }
        });
        holder.cbChoose.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        holder.cbChoose.setChecked(mCheckedFlags[position]);//用数组中的值设置CheckBox的选中状态
        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        holder.cbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCheckedFlags[position] = b;
            }
        });
    }

    private void updateFloaderIconStyle(ImageView imageView) {
        switch (mIconStyle) {
            case Constant.ICON_STYLE_BLUE:
                imageView.setBackgroundResource(R.mipmap.lfile_folder_style_blue);
                break;
            case Constant.ICON_STYLE_GREEN:
                imageView.setBackgroundResource(R.mipmap.lfile_folder_style_green);
                break;
            case Constant.ICON_STYLE_YELLOW:
                imageView.setBackgroundResource(R.mipmap.lfile_folder_style_yellow);
                break;
        }
    }

    private void updateFileIconStyle(ImageView imageView) {
        switch (mIconStyle) {
            case Constant.ICON_STYLE_BLUE:
                imageView.setBackgroundResource(R.mipmap.lfile_file_style_blue);
                break;
            case Constant.ICON_STYLE_GREEN:
                imageView.setBackgroundResource(R.mipmap.lfile_file_style_green);
                break;
            case Constant.ICON_STYLE_YELLOW:
                imageView.setBackgroundResource(R.mipmap.lfile_file_style_yellow);
                break;
        }
    }

    /**
     * 设置监听器
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置数据源
     *
     * @param mListData
     */
    public void setmListData(List<File> mListData) {
        this.mListData = mListData;
        mCheckedFlags = new boolean[mListData.size()];
    }

    public void setmIconStyle(int mIconStyle) {
        this.mIconStyle = mIconStyle;
    }

    /**
     * 设置是否全选
     *
     * @param isAllSelected
     */
    public void updateAllSelelcted(boolean isAllSelected) {
        Arrays.fill(mCheckedFlags, isAllSelected);
        notifyDataSetChanged();
    }

    class PathViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layoutRoot;
        private ImageView ivType;
        private TextView tvName;
        private TextView tvDetail;
        private CheckBox cbChoose;

        PathViewHolder(View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.iv_type);
            layoutRoot = itemView.findViewById(R.id.layout_item_root);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDetail = itemView.findViewById(R.id.tv_detail);
            cbChoose = itemView.findViewById(R.id.cb_choose);
        }
    }
}



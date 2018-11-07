package net.umatoma.torrentmediaapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.umatoma.torrentmediaapp.R;
import net.umatoma.torrentmediaapp.repository.DownloadedFile;

import java.util.ArrayList;
import java.util.List;


public class DownloadedFilesAdapter
        extends RecyclerView.Adapter<DownloadedFilesAdapter.DownloadedFileViewHolder> {

    private List<DownloadedFile> downloadedFiles;
    private OnClickItemListener onClickItemListener;

    public DownloadedFilesAdapter() {
        this.downloadedFiles = new ArrayList<>();
    }

    @NonNull
    @Override
    public DownloadedFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.downloaded_file_list_item, viewGroup, false);
        return new DownloadedFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedFileViewHolder holder, final int position) {
        holder.bind(this.downloadedFiles.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadedFilesAdapter.this.onClickItemListener.onClickItem(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.downloadedFiles.size();
    }

    public DownloadedFile getItem(int position) {
        return this.downloadedFiles.get(position);
    }

    public void setItems(List<DownloadedFile> downloadedFiles) {
        this.downloadedFiles = downloadedFiles;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public class DownloadedFileViewHolder extends RecyclerView.ViewHolder {

        TextView fileNameTextView;
        TextView fileTypeTextView;

        public DownloadedFileViewHolder(@NonNull View itemView) {
            super(itemView);

            this.fileNameTextView = itemView.findViewById(R.id.downloaded_file_name_tv);
            this.fileTypeTextView = itemView.findViewById(R.id.downloaded_file_type_tv);
        }

        public void bind(DownloadedFile downloadedFile) {
            this.fileNameTextView.setText(downloadedFile.getName());
            this.fileTypeTextView.setText(downloadedFile.getType());
        }
    }
}

package com.example.testcustomwidgetslibrary.fileDirectory.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcustomwidgetslibrary.R;
import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModel;
import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModelType;
import com.example.testcustomwidgetslibrary.fileDirectory.res.ITSFileResources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sasaarsenovic
 */
public class ITSFileExplorerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final String EXTENSION_PDF = "pdf";
    private final String EXTENSION_IMAGE = "image";
    private final String EXTENSION_AUDIO = "audio";
    private final String EXTENSION_VIDEO = "video";
    private final String EXTENSION_TEXT = "text";
    private final String EXTENSION_APK = "apk";
    private final String EXTENSION_DOCX = "word";
    private final String EXTENSION_XLSX = "excel";
    private final String EXTENSION_PPT = "powerPoint";
    private final String EXTENSION_CONTACTS = "contacts";
    private final String EXTENSION_HTML = "html";
    private final String EXTENSION_XML = "xml";
    private final String EXTENSION_UNKNOWN = "unknown";

    private Activity activity;
    private List<FileModel> fileModels;
    private List<FileModel> filteredFileModels;
    private FileExplorerListener listener;

    public ITSFileExplorerRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.fileModels = new ArrayList<>();
        this.filteredFileModels = new ArrayList<>();
    }

    public void setListener(FileExplorerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_file_explorer, viewGroup, false);
        return new FileExplorerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof FileExplorerHolder) {
            FileExplorerHolder fileExplorerHolder = (FileExplorerHolder) viewHolder;
            FileModel fileModel = filteredFileModels.get(i);
            if (fileModel.isEnabled()) {
                fileExplorerHolder.mainLayout.setAlpha(1f);
            } else {
                fileExplorerHolder.mainLayout.setAlpha(0.2f);
            }
            this.fillRows(fileExplorerHolder.name, fileExplorerHolder.name2, fileExplorerHolder.icon, fileExplorerHolder.icon2, fileExplorerHolder.fileExplorerLayout,
                    fileModel.getFileModelType(), fileModel.getAbsolutePath());
            this.setLayoutOnClickListenerByFileType(fileExplorerHolder.mainLayout, fileModel.getFileModelType(), fileModel, i);
        }
    }

    public void loadDirectory(List<FileModel> filesList) {
        fileModels = new ArrayList<>(filesList);
        Collections.sort(fileModels, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel o1, FileModel o2) {
                int directorySortResult = o2.getFileModelType().compareTo(o1.getFileModelType());
                if (directorySortResult == 0) {
                    return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                }
                return directorySortResult;
            }
        });
        filteredFileModels = fileModels;
        this.notifyDataSetChanged();
    }

    private void fillRows(TextView textView, TextView textView2, ImageView imageView, ImageView imageView2, LinearLayout linearLayout, FileModelType fileModelType, String filePath) {
        int fileImageId = 0;
        int directoryImageId = 0;
        if (ITSFileResources.imageFileId == null) {
            fileImageId = ITSFileResources.defaultImageFileId;
        }
        if (ITSFileResources.imageDirectoryId == null) {
            directoryImageId = ITSFileResources.defaultImageDirectoryId;
        }

        String[] splittedString = filePath.split("/");
        textView.setText(splittedString[splittedString.length - 1]);
        textView2.setText(splittedString[splittedString.length - 1]);
        switch (fileModelType) {
            case FILE:
                File imgFile = new File(filePath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (bitmap != null) {
                        linearLayout.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        imageView2.setVisibility(View.VISIBLE);
                        textView2.setVisibility(View.VISIBLE);

                        imageView2.setImageBitmap(bitmap);
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        imageView2.setVisibility(View.GONE);
                        textView2.setVisibility(View.GONE);

                        imageView.setImageResource(setImageDependOnExtension(filePath));
                    }
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    imageView2.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);

                    imageView.setImageResource(setImageDependOnExtension(filePath));
                }
                break;
            case DIRECTORY:
                linearLayout.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);

                imageView.setImageResource(directoryImageId);
                break;
            default:
                break;
        }
    }

    private void setLayoutOnClickListenerByFileType(CardView layout, final FileModelType fileModelType, final FileModel fileModel, final int index) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileModel.isEnabled()) {
                    switch (fileModelType) {
                        case FILE:
                            if (fileModel.isSelected()) {
                                fileModel.setSelected(false);
                                if (listener != null)
                                    listener.onFileClick(fileModel);
                                notifyDataSetChanged();
                                break;
                            }
                            fileModel.setSelected(true);
                            if (listener != null)
                                listener.onFileClick(fileModel);
                            notifyDataSetChanged();
                            break;
                        case DIRECTORY:
                            if (listener != null)
                                listener.onDirectoryClick(fileModel.getAbsolutePath());
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(activity, "This file type isn't allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredFileModels.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredFileModels = (ArrayList<FileModel>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<FileModel> FilteredArrList = new ArrayList<>();

                if (fileModels == null) {
                    fileModels = new ArrayList<>(filteredFileModels);
                }

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = fileModels.size();
                    results.values = fileModels;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < fileModels.size(); i++) {
                        String data = fileModels.get(i).getAbsolutePath();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new FileModel(fileModels.get(i).getAbsolutePath(), fileModels.get(i).getDirectoryPath(), fileModels.get(i).getFileModelType()));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    private int setImageDependOnExtension(String path) {
        switch (extension(path)) {
            case EXTENSION_PDF:
                return R.drawable.file_pdf;
            case EXTENSION_IMAGE:
                return R.drawable.file_image;
            case EXTENSION_AUDIO:
                return R.drawable.file_audio;
            case EXTENSION_VIDEO:
                return R.drawable.file_video;
            case EXTENSION_TEXT:
                return R.drawable.file_txt;
            case EXTENSION_APK:
                return R.drawable.file_apk;
            case EXTENSION_DOCX:
                return R.drawable.file_docx;
            case EXTENSION_XLSX:
                return R.drawable.file_xlsx;
            case EXTENSION_PPT:
                return R.drawable.file_ppt;
            case EXTENSION_CONTACTS:
                return R.drawable.file_vcf;
            case EXTENSION_HTML:
                return R.drawable.file_html;
            case EXTENSION_XML:
                return R.drawable.file_xml;
            case EXTENSION_UNKNOWN:
                return R.drawable.file_unknown;
            default:
                return R.drawable.file_unknown;
        }
    }

    private String extension(String path) {
        if (path != null) {
            if (path.contains(".")) {
                String extension = path.substring(path.lastIndexOf("."));
                if (extension.equalsIgnoreCase(".pdf") || extension.equalsIgnoreCase(".PDF")) {
                    return EXTENSION_PDF;
                } else if (extension.equalsIgnoreCase(".bmp") ||
                        extension.equalsIgnoreCase(".gif") ||
                        extension.equalsIgnoreCase(".jpg") ||
                        extension.equalsIgnoreCase(".jpeg") ||
                        extension.equalsIgnoreCase(".png") ||
                        extension.equalsIgnoreCase(".webp") ||
                        extension.equalsIgnoreCase(".heic") ||
                        extension.equalsIgnoreCase(".helf")) {
                    return EXTENSION_IMAGE;
                } else if (extension.equalsIgnoreCase(".flac") ||
                        extension.equalsIgnoreCase(".gsm") ||
                        extension.equalsIgnoreCase(".mp3") ||
                        extension.equalsIgnoreCase(".mkv") ||
                        extension.equalsIgnoreCase(".wav") ||
                        extension.equalsIgnoreCase(".aac") ||
                        extension.equalsIgnoreCase(".aiff") ||
                        extension.equalsIgnoreCase(".agg") ||
                        extension.equalsIgnoreCase(".wma") ||
                        extension.equalsIgnoreCase(".alac")) {
                    return EXTENSION_AUDIO;
                } else if (extension.equalsIgnoreCase(".mp4") ||
                        extension.equalsIgnoreCase(".webm") ||
                        extension.equalsIgnoreCase(".avi") ||
                        extension.equalsIgnoreCase(".3gp") ||
                        extension.equalsIgnoreCase(".mov")) {
                    return EXTENSION_VIDEO;
                } else if (extension.equalsIgnoreCase(".txt") ||
                        extension.equalsIgnoreCase(".text")) {
                    return EXTENSION_TEXT;
                } else if (extension.equalsIgnoreCase(".apk")) {
                    return EXTENSION_APK;
                } else if (extension.equalsIgnoreCase(".doc") ||
                        extension.equalsIgnoreCase(".docx") ||
                        extension.equalsIgnoreCase(".dot") ||
                        extension.equalsIgnoreCase(".wbk")) {
                    return EXTENSION_DOCX;
                } else if (extension.equalsIgnoreCase(".xls") ||
                        extension.equalsIgnoreCase(".xlsx") ||
                        extension.equalsIgnoreCase(".xlt") ||
                        extension.equalsIgnoreCase(".xla") ||
                        extension.equalsIgnoreCase(".xlw") ||
                        extension.equalsIgnoreCase(".odt") ||
                        extension.equalsIgnoreCase(".ods")) {
                    return EXTENSION_XLSX;
                } else if (extension.equalsIgnoreCase(".ppt") ||
                        extension.equalsIgnoreCase(".pptx") ||
                        extension.equalsIgnoreCase(".pptm") ||
                        extension.equalsIgnoreCase(".potx") ||
                        extension.equalsIgnoreCase(".potm") ||
                        extension.equalsIgnoreCase(".pps") ||
                        extension.equalsIgnoreCase(".ppa")) {
                    return EXTENSION_PPT;
                } else if (extension.equalsIgnoreCase(".vcf")) {
                    return EXTENSION_CONTACTS;
                } else if (extension.equalsIgnoreCase(".html") ||
                        extension.equalsIgnoreCase(".htm") ||
                        extension.equalsIgnoreCase(".xhtml")) {
                    return EXTENSION_HTML;
                } else if (extension.equalsIgnoreCase(".xml")) {
                    return EXTENSION_XML;
                } else {
                    return EXTENSION_UNKNOWN;
                }
            } else {
                return EXTENSION_UNKNOWN;
            }
        } else {
            return "";
        }
    }

    class FileExplorerHolder extends RecyclerView.ViewHolder {
        private ImageView icon, icon2;
        private TextView name, name2;
        private CardView mainLayout;
        private LinearLayout fileExplorerLayout;

        public FileExplorerHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.file_explorer_image);
            icon2 = itemView.findViewById(R.id.file_explorer_image_2);
            name = itemView.findViewById(R.id.file_explorer_text);
            name2 = itemView.findViewById(R.id.file_explorer_text_2);
            mainLayout = itemView.findViewById(R.id.main_layout);
            fileExplorerLayout = itemView.findViewById(R.id.file_explorer_layout);
        }
    }

    public interface FileExplorerListener {
        void onDirectoryClick(String selectedAbsolutePath);

        void onFileClick(FileModel fileModel);
    }
}

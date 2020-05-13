package com.example.testcustomwidgetslibrary.fileDirectory;


import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testcustomwidgetslibrary.R;
import com.example.testcustomwidgetslibrary.fileDirectory.adapter.ITSFileExplorerRecyclerAdapter;
import com.example.testcustomwidgetslibrary.fileDirectory.listener.ActivityListener;
import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModel;
import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModelType;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class ITSFileExplorerFragment extends Fragment implements ITSFileExplorerRecyclerAdapter.FileExplorerListener {
    private RecyclerView recyclerView;
    private ITSFileExplorerRecyclerAdapter fileExplorerAdapter;
    private String selectedAbsolutePath;
    private ActivityListener activityListener;
    private LinearLayout filesLayout;
    private TextView noFilesHereText;
    private ImageView toolbarBack;
    private String fileTypeKeyword = "";
    private long fileSizeLimit = -1;
    private boolean listJustSelectedType = false;

    public static ITSFileExplorerFragment newInstance(String fileTypeKeyword, long fileSizeLimit, boolean listJustSelectedType) {
        ITSFileExplorerFragment fragment = new ITSFileExplorerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("fileTypeKeyword", fileTypeKeyword);
        bundle.putLong("fileSizeLimit", fileSizeLimit);
        bundle.putBoolean("listJustSelectedType", listJustSelectedType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileTypeKeyword = getArguments().getString("fileTypeKeyword");
            fileSizeLimit = getArguments().getLong("fileSizeLimit");
            listJustSelectedType = getArguments().getBoolean("listJustSelectedType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.its_file_explorer_fragment, container, false);


        EditText searchEditText = view.findViewById(R.id.edit_text_search);
        noFilesHereText = view.findViewById(R.id.no_files_text);
        filesLayout = view.findViewById(R.id.files_layout);
        toolbarBack = view.findViewById(R.id.toolbarBack);

        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fileExplorerAdapter != null) {
                    fileExplorerAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.initViews(view);
        this.loadDirectory();

        return view;
    }

    private void initRecyclerView(View view) {
        this.recyclerView = view.findViewById(R.id.file_explorer_recycler_view);
        this.fileExplorerAdapter = new ITSFileExplorerRecyclerAdapter(getActivity());
        this.fileExplorerAdapter.setListener(this);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.fileExplorerAdapter);
    }

    private void loadDirectory() {
        File root = Environment.getExternalStorageDirectory();

        if (this.selectedAbsolutePath != null) {
            root = new File(this.selectedAbsolutePath);
        } else {
            this.selectedAbsolutePath = root.getAbsolutePath();
        }

        List<FileModel> fileModelList = new ArrayList<>();

        if (root.isDirectory()) {
            if (root.listFiles().length > 0) {
                filesLayout.setVisibility(View.VISIBLE);
                noFilesHereText.setVisibility(View.GONE);
            } else {
                filesLayout.setVisibility(View.GONE);
                noFilesHereText.setVisibility(View.VISIBLE);
            }

            List<File> files = Arrays.asList(root.listFiles());

            Iterator iterator = files.iterator();
            while (iterator.hasNext()) {
                try {
                    File file = (File) iterator.next();

                    String[] splitted = file.getAbsolutePath().split("/");
                    if (!splitted[splitted.length - 1].startsWith(".")) {
                        if (file.isDirectory()) {
                            fileModelList.add(new FileModel(file.getAbsolutePath(), FileModelType.DIRECTORY));
                        } else {
                            FileModel fileModel = new FileModel(file.getAbsolutePath(), file.getParentFile().getAbsolutePath(), FileModelType.FILE);
                            boolean limitObserved = true;
                            if (fileSizeLimit > -1) {
                                if (getFileSizeInMb(file) > fileSizeLimit) {
                                    limitObserved = false;
                                }
                            }
                            if (fileTypeKeyword != null && !fileTypeKeyword.equals("")) {
                                if (file.getAbsolutePath() != null) {
                                    String[] extension = file.getAbsolutePath().split("/");
                                    if (extension[extension.length - 1].toLowerCase().contains(fileTypeKeyword.toLowerCase())) {
                                        if (limitObserved) {
                                            fileModel.setEnabled(true);
                                        } else {
                                            fileModel.setEnabled(false);
                                        }
                                    } else {
                                        fileModel.setEnabled(false);
                                    }
                                }
                                if (listJustSelectedType) {
                                    if (fileModel.isEnabled())
                                        fileModelList.add(fileModel);
                                } else {
                                    fileModelList.add(fileModel);
                                }
                            } else {
                                if (limitObserved)
                                    fileModelList.add(fileModel);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            for (File file : root.listFiles()) {
//                String[] splitted = file.getAbsolutePath().split("/");
//                if (!splitted[splitted.length - 1].startsWith(".")) {
//                    if (file.isDirectory()) {
//                        fileModelList.add(new FileModel(file.getAbsolutePath(), FileModelType.DIRECTORY));
//                    } else {
//                        FileModel fileModel = new FileModel(file.getAbsolutePath(), file.getParentFile().getAbsolutePath(), FileModelType.FILE);
//                        boolean limitObserved = true;
//                        if (fileSizeLimit > -1) {
//                            if (getFileSizeInMb(file) > fileSizeLimit) {
//                                limitObserved = false;
//                            }
//                        }
//                        if (fileTypeKeyword != null && !fileTypeKeyword.equals("")) {
//                            if (file.getAbsolutePath() != null) {
//                                String[] extension = file.getAbsolutePath().split("/");
//                                if (extension[extension.length - 1].toLowerCase().contains(fileTypeKeyword.toLowerCase())) {
//                                    if (limitObserved)
//                                        fileModel.setEnabled(true);
//                                    else
//                                        fileModel.setEnabled(false);
//                                } else {
//                                    fileModel.setEnabled(false);
//                                }
//                            }
//                        }
//                        fileModelList.add(fileModel);
//                    }
//                }
//            }
        } else {
            fileModelList.add(new FileModel(root.getAbsolutePath(), FileModelType.DIRECTORY));
        }

        this.updateRecyclerList(fileModelList);
    }

    public static double getFileSizeInMb(File file) {
        double fileSizeInBytes = (double) file.length();
        double fileSizeInKb = fileSizeInBytes / 1024;
        DecimalFormat df = new DecimalFormat("#.##");
        String sizeInMb = df.format(fileSizeInKb / 1024);
        try {
            return Double.parseDouble(sizeInMb);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateRecyclerList(List<FileModel> fileModels) {
        this.fileExplorerAdapter.loadDirectory(fileModels);
    }


    private void initViews(View view) {
        this.initRecyclerView(view);
    }

    @Override
    public void onDirectoryClick(String selectedAbsolutePath) {
        this.activityListener.onDirectoryChanged(selectedAbsolutePath);
    }

    @Override
    public void onFileClick(FileModel fileModel) {
        this.activityListener.onFileSelect(fileModel);
    }

    void setListeners(ActivityListener activityListener) {
        this.activityListener = activityListener;
    }

    void setDirectory(String dir) {
        this.selectedAbsolutePath = dir;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.activityListener.onBackButtonPressed(this.selectedAbsolutePath);
    }
}

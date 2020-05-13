package com.example.testcustomwidgetslibrary.fileDirectory;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.testcustomwidgetslibrary.R;
import com.example.testcustomwidgetslibrary.fileDirectory.adapter.ITSFileExplorerRecyclerAdapter;
import com.example.testcustomwidgetslibrary.fileDirectory.listener.ActivityListener;
import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModel;
import com.example.testcustomwidgetslibrary.fileDirectory.model.FileModelType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sasaarsenovic
 */
public class ITSFileExplorerActivity extends AppCompatActivity implements ITSFileExplorerRecyclerAdapter.FileExplorerListener, ActivityListener {
    private String STACK_KEY = "FRAGMENT_STACK_KEY";
    public static final String FILE_TYPE_BUNDLE_KEY = "KEYWORD_FILE_TYPE_BUNDLE";
    public static final String FILE_SIZE_LIMIT_MB_BUNDLE_KEY = "KEYWORD_FILE_SIZE_MB_LIMIT_BUNDLE";
    public static final String FILE_LIST_JUST_SELECTED_FILE_TYPE_BUNDLE_KEY = "KEYWORD_FILE_LIST_JUST_SELECTED_FILE_TYPE_BUNDLE";
    private RecyclerView recyclerView;
    private ITSFileExplorerRecyclerAdapter adapter;
    private String selectedAbsolutePath;
    private String keywordFileType = "";
    private long fileSizeLimit = -1;
    private boolean listJustSelectedType = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.its_file_explorer_activity);
        checkPermissionForActivity();

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            keywordFileType = bundle.getString(FILE_TYPE_BUNDLE_KEY, "");
            fileSizeLimit = bundle.getLong(FILE_SIZE_LIMIT_MB_BUNDLE_KEY, -1);
            listJustSelectedType = bundle.getBoolean(FILE_LIST_JUST_SELECTED_FILE_TYPE_BUNDLE_KEY, false);
        }
        ITSFileExplorerFragment fragment = ITSFileExplorerFragment.newInstance(keywordFileType, fileSizeLimit, listJustSelectedType);
        fragment.setListeners(this);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment).commit();
        this.selectedAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private void checkPermissionForActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException(String.format("Permission %s is not granted.", Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
    }

    @Override
    public void onDirectoryClick(String selectedAbsolutePath) {
        this.selectedAbsolutePath = selectedAbsolutePath;
    }

    @Override
    public void onFileClick(FileModel fileModel) {
        if (fileModel.isSelected()) {
            this.selectedAbsolutePath = fileModel.getAbsolutePath();
        } else {
            this.selectedAbsolutePath = fileModel.getDirectoryPath();
        }
    }

    @Override
    public void onDirectoryChanged(String absolutePath) {
        this.selectedAbsolutePath = absolutePath;
        ITSFileExplorerFragment fragment = ITSFileExplorerFragment.newInstance(keywordFileType, fileSizeLimit, listJustSelectedType);
        fragment.setListeners(this);
        fragment.setDirectory(absolutePath);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(STACK_KEY).commit();
    }

    @Override
    public void onFileSelect(FileModel fileModel) {
        if (fileModel.isSelected()) {
            this.selectedAbsolutePath = fileModel.getAbsolutePath();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("resultKey", selectedAbsolutePath);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            this.selectedAbsolutePath = fileModel.getDirectoryPath();
        }
    }

    @Override
    public void onBackButtonPressed(String absolutePath) {
        System.out.println();
    }
}

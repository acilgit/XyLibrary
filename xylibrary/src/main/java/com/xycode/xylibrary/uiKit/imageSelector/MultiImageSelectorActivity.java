package com.xycode.xylibrary.uiKit.imageSelector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;

import static com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions.options;

public class MultiImageSelectorActivity extends BaseActivity implements MultiImageSelectorFragment.Callback{

    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;
    public static final String EXTRA_RESULT = "EXTRA_RESULT";

    //    private List<String> options().selectedList = new ArrayList<>();
    private Button submitButton;
//    private int defaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ImageSelectorOptions.setOptions(ImageSelectorOptions.options());
        setContentView(R.layout.activity_image_selector);

        Bundle bundle = new Bundle();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fmImageGrid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getThis().setSupportActionBar(toolbar);
        getThis().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });*/
        
        submitButton = (Button) findViewById(R.id.btnCommit);
        if(options().selectedList == null || options().selectedList.size()<=0){
            submitButton.setText(R.string.text_done);
            submitButton.setEnabled(false);
        }else{
            updateDoneText();
            submitButton.setEnabled(true);
        }
        submitButton.setOnClickListener(view -> {
            if(options().selectedList != null && options().selectedList.size() >0){
                Intent data = new Intent();
                data.putStringArrayListExtra(EXTRA_RESULT, (ArrayList<String>) options().selectedList);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    protected boolean useEventBus() {
        return false;
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    private void updateDoneText(){
        submitButton.setText(String.format("%s(%d/%d)", getString(R.string.text_done), options().selectedList.size(), options().defaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        options().selectedList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, (ArrayList<String>) options().selectedList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!options().selectedList.contains(path)) {
            options().selectedList.add(path);
        }
        // change btn state
        if(options().selectedList.size() > 0){
            updateDoneText();
            if(!submitButton.isEnabled()){
                submitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(options().selectedList.contains(path)){
            options().selectedList.remove(path);
        }
        updateDoneText();
        if(options().selectedList.size() == 0){
            submitButton.setText(R.string.text_done);
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
    }


/*    protected ImageSelectorOptions getOptions(){
        return ImageSelectorOptions.options();
    }*/

}

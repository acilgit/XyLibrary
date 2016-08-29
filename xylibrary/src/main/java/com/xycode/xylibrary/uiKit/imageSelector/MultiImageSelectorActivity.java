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

public abstract class MultiImageSelectorActivity extends BaseActivity implements MultiImageSelectorFragment.Callback{

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button submitButton;
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageSelectorOptions.setOptions(getOptions());
        setContentView(R.layout.activity_image_selector);
        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
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

        // 完成按钮
        submitButton = (Button) findViewById(R.id.btnCommit);
        if(resultList == null || resultList.size()<=0){
            submitButton.setText(options().textSubmitBtn);
            submitButton.setEnabled(false);
        }else{
            updateDoneText();
            submitButton.setEnabled(true);
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultList != null && resultList.size() >0){
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    @Override
    protected AlertDialog setLoadingDialog() {
        return null;
    }

    private void updateDoneText(){
        submitButton.setText(String.format("%s(%d/%d)",
                getString(options().textSubmitBtn), resultList.size(), mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if(resultList.size() > 0){
            updateDoneText();
            if(!submitButton.isEnabled()){
                submitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
        }
        updateDoneText();
        // 当为选择图片时候的状态
        if(resultList.size() == 0){
            submitButton.setText(options().textSubmitBtn);
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
    }

    protected abstract ImageSelectorOptions getOptions();

}
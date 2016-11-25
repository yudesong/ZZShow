package com.ys.yoosir.zzshow.mvp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ys.yoosir.zzshow.R;
import com.ys.yoosir.zzshow.mvp.modle.netease.NewsDetail;
import com.ys.yoosir.zzshow.mvp.presenter.NewsDetailPresenterImpl;
import com.ys.yoosir.zzshow.mvp.presenter.interfaces.NewsDetailPresenter;
import com.ys.yoosir.zzshow.mvp.ui.activities.base.BaseActivity;
import com.ys.yoosir.zzshow.mvp.view.NewsDetailView;
import com.ys.yoosir.zzshow.utils.httputil.RxJavaCustomTransform;
import com.ys.yoosir.zzshow.widget.phototext.PhotoTextView;
import com.ys.yoosir.zzshow.widget.phototext.UrlImageGetter;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;

public class NewsDetailActivity extends BaseActivity<NewsDetailPresenter> implements NewsDetailView {

    private static final String NEWS_POST_ID = "NEWS_POST_ID";
    private static final String NEWS_IMG_RES = "NEWS_IMG_RES";

    private String mPostId;
    private String mPostImgPath;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.news_content_text)
    PhotoTextView newsContentTextTv;

    @BindView(R.id.news_content_from)
    TextView newsContentFromTv;

    @BindView(R.id.news_detail_picture_iv)
    ImageView newsDetailPictureIv;

    public static Intent getNewsDetailIntent(Context context, String postId, String postImgPath){
        Intent intent = new Intent(context,NewsDetailActivity.class);
        intent.putExtra(NEWS_POST_ID,postId);
        intent.putExtra(NEWS_IMG_RES,postImgPath);
        return intent;
    }


    @Override
    protected void onDestroy() {
        if(newsContentTextTv != null){
            ViewGroup parent = (ViewGroup) newsContentTextTv.getParent();
            parent.removeView(newsContentTextTv);
            newsContentTextTv.destroyDrawingCache();
        }
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_news_detail;
    }

    @Override
    public void initVariables() {
        getIntentParams();
        mPresenter = new NewsDetailPresenterImpl();
        mPresenter.attachView(this);
        mPresenter.setPostId(mPostId);
    }

    /**
     * 获取跳转传递的参数
     */
    private void getIntentParams(){
        mPostId = getIntent().getStringExtra(NEWS_POST_ID);
        mPostImgPath = getIntent().getStringExtra(NEWS_IMG_RES);
    }

    @Override
    public void initViews() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.tv_color_white));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.tv_color_primary_white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Glide.with(this).load(mPostImgPath).asBitmap()
                .centerCrop()
                .placeholder(R.mipmap.ic_loading)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .error(R.mipmap.ic_load_fail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(newsDetailPictureIv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {

    }

    @Override
    public void showNewsContent(NewsDetail newsDetail) {
        String mShareLink = newsDetail.getShareLink();
        toolbarLayout.setTitle(newsDetail.getTitle());
        newsContentFromTv.setText(getString(R.string.news_content_from_value,newsDetail.getSource(),newsDetail.getPtime()));
        newsContentTextTv.setContainText(newsDetail.getBody(),false);
//        setBodyView(newsDetail.getBody());
    }

    private void setBodyView(final String bodyText){
        if(bodyText != null)
            Observable.timer(500, TimeUnit.MILLISECONDS)
            .compose(RxJavaCustomTransform.<Long>defaultSchedulers())
            .subscribe(new Subscriber<Long>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Long aLong) {
                    newsContentTextTv.setContainText(bodyText,false);
                }
            });

    }

}
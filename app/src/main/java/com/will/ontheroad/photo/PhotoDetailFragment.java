package com.will.ontheroad.photo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Will on 2016/4/16.
 */
public class PhotoDetailFragment extends android.support.v4.app.Fragment implements PhotoViewAttacher.OnPhotoTapListener{
    private PhotoViewAttacher attacher;
    private RelativeLayout root;
    private RelativeLayout info;
    public static PhotoDetailFragment getInstance( String url){
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        PhotoDetailFragment instance = new PhotoDetailFragment();
        instance.setArguments(bundle);
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater ,ViewGroup container,Bundle savedInstanceState){
        String url = getArguments().getString("url","none");
        View view  = inflater.inflate(R.layout.fragment_photo_viewpager,null);
        final ImageViewTouch imageView = (ImageViewTouch) view.findViewById(R.id.fragment_photo_detail_image);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.fragment_photo_detail_progress_bar);
        //imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        if(url.equals("none")){
            imageView.setBackgroundColor(getResources().getColor(R.color.dark_black));
        }else{
            Picasso.with(getActivity()).load(url).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                   progressBar.setVisibility(View.GONE);
                    attacher = new PhotoViewAttacher(imageView);
                    attacher.setOnPhotoTapListener(PhotoDetailFragment.this);
                }
                @Override
                public void onError() {}
            });
        }
        return view;
    }
    //动画过渡隐藏图片info以及toolbar
    @Override
    public void onPhotoTap(View view, float v, float v1) {
        root = (RelativeLayout) getActivity().findViewById(R.id.photo_Detail_root);
        info = (RelativeLayout) getActivity().findViewById(R.id.photo_detail_info);
        TransitionManager.beginDelayedTransition(root,new Fade());
        if(info.getVisibility() == View.VISIBLE){
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            info.setVisibility(View.GONE);
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
            info.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onOutsidePhotoTap() {}
}

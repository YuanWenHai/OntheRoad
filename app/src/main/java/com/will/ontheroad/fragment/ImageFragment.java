package com.will.ontheroad.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Will on 2016/3/22.
 */
public class ImageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.image_page,container,false);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.fragment_progress);
        ImageViewTouch imageView = (ImageViewTouch) view.findViewById(R.id.scale_image_view);
        imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        String path = getArguments().getString("path");
        if(path != null) {
            Picasso.with(getActivity()).load(path).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onError() {}
            });
        }
        return view;
    }
}

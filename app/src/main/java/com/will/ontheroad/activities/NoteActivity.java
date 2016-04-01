package com.will.ontheroad.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.adapter.NoteAdapter;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.bean.Note;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Will on 2016/3/31.
 */
public class NoteActivity extends BaseActivity {
    //private AppBarLayout appBarLayout;
    //private RecyclerView recyclerView;
    private ArrayList<Note> list = new ArrayList<>();
    private NoteAdapter adapter;
    private RelativeLayout layout;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ImageView bgImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_page);
        sp = getSharedPreferences("bg", MODE_PRIVATE);
        initializeViews();
        loadBG();
        queryNote();
    }
    private void initializeViews(){
        layout = (RelativeLayout) findViewById(R.id.layout);
        bgImageView = (ImageView) findViewById(R.id.bg);
        bgImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
                    return true;
            }
        });
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("心情");
        loadUser();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.note_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(list);
        /*
        *item 中选项的点击事件
         */
        adapter.setOnItemClickListener(new NoteAdapter.ItemClickListener() {
            @Override
            public void onEditClick(View v, int position) {
                Intent intent = new Intent(NoteActivity.this, AddNoteActivity.class);
                intent.putExtra("content", list.get(position).getContent());
                intent.putExtra("image", list.get(position).getImageThumbnail());
                intent.putExtra("id", list.get(position).getObjectId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(View v, final int position) {
                AlertDialog dialog = new AlertDialog.Builder(NoteActivity.this).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note note = new Note();
                                note.setObjectId(list.get(position).getObjectId());
                                note.delete(NoteActivity.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        list.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        showToast(s);
                                    }
                                });
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
                dialog.setMessage("确认删除本条目?");
                dialog.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void applyPalette( final CollapsingToolbarLayout toolbar,final FloatingActionButton fab ){
        Bitmap bitmap;
        if(!sp.getString("bg","").isEmpty()){
            BitmapDrawable drawable = (BitmapDrawable) BitmapDrawable.createFromPath(sp.getString("bg",""));
            bitmap = drawable.getBitmap();
        }else{
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.profile_bg);
        }
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                toolbar.setContentScrimColor(palette.getDarkMutedColor(Color.BLACK));
                toolbar.setStatusBarScrimColor(palette.getDarkMutedColor(Color.BLACK));
                fab.setRippleColor(palette.getLightVibrantColor(Color.WHITE));
                fab.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantColor(
                        getResources().getColor(R.color.primary))));
            }
        });
    }
    private void queryNote(){
        BmobQuery<Note> query = new BmobQuery<>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(this, MyUser.class));
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> notes) {
                list.clear();
                list.addAll(notes);
                adapter.notifyItemRangeChanged(0, notes.size());
            }

            @Override
            public void onError(int i, String s) {
                showToast(s);
            }
        });
    }
    private void loadUser(){
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteActivity.this,AddNoteActivity.class));
            }
        });
        MyUser user = BmobUser.getCurrentUser(this,MyUser.class);
            ImageView userImage = (ImageView) findViewById(R.id.note_page_user_image);
            TextView userName = (TextView) findViewById(R.id.note_page_user_name);
            if(user.getUserName() != null){
               userName.setText(user.getUserName());
            }else{
                userName.setText("请修改名字");
            }
            if(user.getUserImageThumbnail() != null){
                Picasso.with(this).load(user.getUserImageThumbnail()).into(userImage);
            }else{
                userImage.setImageResource(R.drawable.noimgavailable);
            }
        applyPalette( collapsingToolbarLayout, fab);
    }

    @Override
    public void onNewIntent(Intent intent ){
        queryNote();
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("应用中..");
            progressDialog.show();
            Uri uri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            final String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            new BmobProFile().getLocalThumbnail(filePath, 5, 1000, 1000, new LocalThumbnailListener() {
                @Override
                public void onSuccess(String s) {
                    editor.putString("bg", s);
                    editor.apply();
                    Drawable drawable = Drawable.createFromPath(s);
                    bgImageView.setImageDrawable(drawable);
                    progressDialog.dismiss();
                }
                @Override
                public void onError(int i, String s) {}
            });
        }
    }
    private void loadBG(){
        editor = sp.edit();
        String bg = sp.getString("bg","");
        if(!bg.isEmpty()){
            Drawable drawable = Drawable.createFromPath(bg);
            bgImageView.setImageDrawable(drawable);
        }
    }
}

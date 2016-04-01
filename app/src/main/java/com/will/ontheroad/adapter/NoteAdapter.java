package com.will.ontheroad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.Note;

import java.util.List;

/**
 * Created by Will on 2016/3/31.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements View.OnClickListener{
    private List<Note> data;
    private ItemClickListener listener;
    public NoteAdapter(List<Note> data){
        this.data = data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int type){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_page_item,parent,false);
        view.findViewById(R.id.edit).setOnClickListener(this);
        view.findViewById(R.id.delete).setOnClickListener(this);
        return new ViewHolder(view);
    }
    @Override
    public int getItemCount(){
        return data.size();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        holder.edit.setTag(position);
        holder.delete.setTag(position);
        if(!data.get(position).getContent().isEmpty()){
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(data.get(position).getContent());
        }else{
            holder.content.setVisibility(View.GONE);
        }
        if(data.get(position).getImage()!= null && !data.get(position).getImage().isEmpty()){
            Picasso.with(holder.image.getContext()).load(data.get(position).getImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        }else{
            holder.image.setVisibility(View.GONE);
        }
        holder.time.setText(data.get(position).getCreatedAt());
    }
    @Override
    public void onClick(View v){
        int position = (int) v.getTag();
        switch(v.getId()){
            case R.id.edit:
                listener.onEditClick(v,position);
                break;
            case R.id.delete:
                listener.onDeleteClick(v,position);
                break;
        }
    }


    public interface ItemClickListener{
        void onEditClick(View v,int position);
        void onDeleteClick(View v ,int position);
    }
    public void setOnItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView content;
        public TextView time;
        public ImageView image;
        public TextView edit;
        public TextView delete;
        public ViewHolder(View item){
            super(item);
            content = (TextView) item.findViewById(R.id.note_page_item_content);
            time = (TextView) item.findViewById(R.id.note_page_item_time);
            image = (ImageView) item.findViewById(R.id.note_page_item_image);
            edit = (TextView) item.findViewById(R.id.edit);
            delete = (TextView) item.findViewById(R.id.delete);
        }
    }
}

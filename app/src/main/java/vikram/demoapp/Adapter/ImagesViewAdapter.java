package vikram.demoapp.Adapter;/* Created by Vikram on 01-10-2018. */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import vikram.demoapp.Acitivty.FullScreenImage;
import vikram.demoapp.R;

public class ImagesViewAdapter extends
        RecyclerView.Adapter<ImagesViewAdapter.ViewHolder> {

    private static final String TAG = ImagesViewAdapter.class.getSimpleName();

    private Context context;
    private List<String> list;

    public ImagesViewAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (itemView).findViewById(R.id.item_img);
        }

        public void bind(final String path) {
            Glide.with(context).load(path)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullScreenImage.class);
                    intent.putExtra("image",path);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.image_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = list.get(position);
        holder.bind(item);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
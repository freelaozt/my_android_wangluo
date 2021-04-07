package com.fxd.wangluo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.Bean.Character;
import com.fxd.wangluo.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


/**
 * Created by aspsine on 15/9/11.
 */
public class CharacterAdapter extends BaseArrayAdapter<Character> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hero_grid, parent, false);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_link_title);
            holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
            holder.tvName.setMaxLines(1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Character character = getItem(position);
        holder.tvName.setText(character.getName());

        Picasso.with(parent.getContext())
                .load(character.getAvatar())
                .transform(new CropCircleTransformation())
                .into(holder.ivAvatar);



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), character.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
    }
}

/* Copyright (c) 2010 Pierre LEVY androidsoft.org
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.androidsoft.games.memory.tux;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 *
 * @author pierre
 */
public class ImageAdapter extends BaseAdapter
{

    private Context mContext;
    private int mTileSize;
    private Memory mMemory;

    public ImageAdapter(Context c, int width, int height, int margin , Memory memory )
    {
        mContext = c;
        mMemory = memory;

        if (width > height)
        {
            mTileSize = getTileSize(width, height, memory.getMaxTilesPerRow(), memory.getMinTilesPerRow(), margin);
        } else
        {
            mTileSize = getTileSize(height, width, memory.getMaxTilesPerRow(), memory.getMinTilesPerRow(), margin);

        }

    }

    private int getTileSize(int max, int min, int countMax, int countMin, int margin)
    {
        int a = max / countMax;
        int b = min / countMin;
        return ((a < b) ? a : b ) - margin;
    }

    public int getCount()
    {
        return mMemory.getCount();
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mTileSize, mTileSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else
        {
            imageView = (ImageView) convertView;
        }


        imageView.setImageResource( mMemory.getResId( position ));
        return imageView;
    }

}

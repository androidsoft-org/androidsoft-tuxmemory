/* Copyright (c) 2010-2011 Pierre LEVY androidsoft.org
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
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 *
 * @author pierre
 */
public class MemoryGridView extends GridView
{
    private static final int MARGIN = 15;

    private Memory mMemory;
    private Context mContext;

    public MemoryGridView(Context context)
    {
        super(context);

        mContext = context;

        setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                mMemory.onPosition( position );
            }
        });

    }

    public MemoryGridView (Context context, AttributeSet attrs)
    {
        super( context , attrs );
        mContext = context;
        setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                mMemory.onPosition( position );
            }
        });
    }

    public MemoryGridView (Context context, AttributeSet attrs, int defStyle)
    {
        super( context , attrs , defStyle );
        mContext = context;
        setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                mMemory.onPosition( position );
            }
        });
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        update( );
    }

    void update()
    {
        setAdapter(new ImageAdapter( mContext, getWidth(), getHeight(), MARGIN , mMemory));
    }


    public void setMemory( Memory memory )
    {
        mMemory = memory;
    }

}

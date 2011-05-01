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

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pierre
 */
public class Memory
{

    private static final String PREF_LIST = "list";
    private static final String PREF_MOVE_COUNT = "move_count";
    private static final String PREF_SELECTED_COUNT = "seleted_count";
    private static final String PREF_FOUND_COUNT = "found_count";
    private static final String PREF_LAST_POSITION = "last_position";
    private static final int MAX_TILES_PER_ROW = 6;
    private static final int MIN_TILES_PER_ROW = 4;
    private static final int SET_SIZE = (MAX_TILES_PER_ROW * MIN_TILES_PER_ROW ) / 2;
    private int mSelectedCount;
    private int mMoveCount;
    private int mFoundCount;
    private int mLastPosition = -1;
    private Tile mT1;
    private Tile mT2;
    private static TileList mList = new TileList();
    private static int[] mTiles;
    private OnMemoryListener mListener;

    public Memory(int[] tiles , OnMemoryListener listener )
    {
        mTiles = tiles;
        mListener = listener;
    }

    void onResume(SharedPreferences prefs)
    {
        String serialized = prefs.getString(PREF_LIST, null);
        if (serialized != null)
        {
            mList = new TileList(serialized);
            mMoveCount = prefs.getInt(PREF_MOVE_COUNT, 0);
            ArrayList<Tile> list = mList.getSelected();
            mSelectedCount = list.size();
            mT1 = (mSelectedCount > 0) ? list.get(0) : null;
            mT2 = (mSelectedCount > 1) ? list.get(1) : null;
            mFoundCount = prefs.getInt(PREF_FOUND_COUNT, 0);
            mLastPosition = prefs.getInt(PREF_LAST_POSITION, -1);

        }
    }

    void onPause(SharedPreferences preferences, boolean quit)
    {
        SharedPreferences.Editor editor = preferences.edit();
        if (!quit)
        {
            // Paused without quit - save state
            editor.putString(PREF_LIST, mList.serialize());
            editor.putInt(PREF_MOVE_COUNT, mMoveCount);
            editor.putInt(PREF_SELECTED_COUNT, mSelectedCount);
            editor.putInt(PREF_FOUND_COUNT, mFoundCount);
            editor.putInt(PREF_LAST_POSITION, mLastPosition);
        } else
        {
            editor.remove(PREF_LIST);
            editor.remove(PREF_MOVE_COUNT);
            editor.remove(PREF_SELECTED_COUNT);
            editor.remove(PREF_FOUND_COUNT);
            editor.remove(PREF_LAST_POSITION);
        }
        editor.commit();
    }

    int getCount()
    {
        return mList.size();
    }
    
    public int getMaxTilesPerRow()
    {
        return MAX_TILES_PER_ROW;
    }

    public int getMinTilesPerRow()
    {
        return MIN_TILES_PER_ROW;
    }

    int getResId(int position)
    {
        return mList.get(position).getResId();
    }

    void reset()
    {
        mFoundCount = 0;
        mMoveCount = 0;
        mList.clear();
        for (Integer tile : getTileSet())
        {
            addRandomly(tile);
        }
    }

    public interface OnMemoryListener
    {

        void onComplete(int moveCount);

        void onUpdateView();
    }

    void onPosition(int position)
    {
        if (position == mLastPosition)
        {
            // Same item clicked
            return;
        }
        mLastPosition = position;
        mList.get(position).select();

        switch (mSelectedCount)
        {
            case 0:
                mT1 = mList.get(position);
                break;

            case 1:
                mT2 = mList.get(position);
                if (mT1.getResId() == mT2.getResId())
                {
                    mT1.setFound(true);
                    mT2.setFound(true);
                    mFoundCount += 2;
                }
                break;

            case 2:
                if (mT1.getResId() != mT2.getResId())
                {
                    mT1.unselect();
                    mT2.unselect();
                }
                mSelectedCount = 0;
                mT1 = mList.get(position);
                break;
        }
        mSelectedCount++;
        mMoveCount++;
        updateView();
        checkComplete();
    }

    private void updateView()
    {
        mListener.onUpdateView();
    }

    private void checkComplete()
    {
        if (mFoundCount == mList.size())
        {
            mListener.onComplete(mMoveCount);
        }
    }

    /**
     * Add a pair of pieces randomly in the list
     * @param nResId The resid of the piece
     */
    private void addRandomly(int nResId)
    {
        double dPos = Math.random() * mList.size();
        int nPos = (int) dPos;
        mList.add(nPos, new Tile(nResId));
        dPos = Math.random() * mList.size();
        nPos = (int) dPos;
        mList.add(nPos, new Tile(nResId));

    }

    private int rand(int nSize)
    {
        double dPos = Math.random() * nSize;
        return (int) dPos;
    }

    private List<Integer> getTileSet()
    {
        List<Integer> list = new ArrayList<Integer>();

        while (list.size() < SET_SIZE)
        {
            int n = rand(mTiles.length);
            int t = mTiles[n];
            if (!list.contains(t))
            {
                list.add(t);
            }
        }
        return list;
    }
}

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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import java.text.MessageFormat;

/**
 * MainActivity
 * @author pierre
 */
public class MainActivity extends AbstractMainActivity implements Memory.OnMemoryListener
{

    private static final String PREF_BEST_MOVE_COUNT = "best_move_count";
    private static final int[] tiles =
    {
        R.drawable.item_1, R.drawable.item_2,
        R.drawable.item_3, R.drawable.item_4, R.drawable.item_5, R.drawable.item_6,
        R.drawable.item_7, R.drawable.item_8, R.drawable.item_9, R.drawable.item_10,
        R.drawable.item_11, R.drawable.item_12, R.drawable.item_13, R.drawable.item_14,
        R.drawable.item_15, R.drawable.item_16, R.drawable.item_17, R.drawable.item_18,
        R.drawable.item_19, R.drawable.item_20, R.drawable.item_21, R.drawable.item_22,
        R.drawable.item_23, R.drawable.item_24, R.drawable.item_25, R.drawable.item_26,
        R.drawable.item_27, R.drawable.item_28, R.drawable.item_29, R.drawable.item_30,
        R.drawable.item_31, R.drawable.item_32, R.drawable.item_33
    };
    private static final int[] not_found_tile_set =
    {
        R.drawable.not_found_1, R.drawable.not_found_2
    };
    private Memory mMemory = new Memory(tiles, this);
    private int mNotFoundResId;
    private MemoryGridView mGridView;

    /**
     * {@inheritDoc }
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);


        newGame();

    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected View getGameView()
    {
        return mGridView;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void newGame()
    {
        initData();
        if (mGridView == null)
        {
            initGrid();
        }
        drawGrid();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void about()
    {
        Intent intent = new Intent( this , CreditsActivity.class );
        startActivity(intent);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences prefs = getPreferences(0);
        mMemory.onResume(prefs);
        mNotFoundResId = not_found_tile_set[0];
        Tile.setNotFoundResId(mNotFoundResId);
        drawGrid();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        mMemory.onPause(getPreferences(0), mQuit);

    }

    private int getBestMoveCount()
    {
        SharedPreferences prefs = getPreferences(0);

        return prefs.getInt(PREF_BEST_MOVE_COUNT, 100);

    }

    private void setBestMoveCount(int nMoveCount)
    {
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putInt(PREF_BEST_MOVE_COUNT, nMoveCount);
        editor.commit();
    }

    /**
     * Initialize game data
     */
    private void initData()
    {
        mMemory.reset();
        mNotFoundResId = not_found_tile_set[0];
        Tile.setNotFoundResId(mNotFoundResId);
    }

    /**
     * Initialize the grid
     */
    private void initGrid()
    {
        mGridView = (MemoryGridView) findViewById(R.id.gridview);
        mGridView.setMemory(mMemory);
    }

    /**
     * Draw or redraw the grid
     */
    private void drawGrid()
    {
        mGridView.update();

    }

    public void onComplete(int countMove)
    {
        int nHighScore = getBestMoveCount();
        String title = getString(R.string.success_title);
        Object[] args = { countMove, nHighScore };
        String message = MessageFormat.format(getString(R.string.success), args );
        int icon = R.drawable.win;
        if (countMove < nHighScore)
        {
            title = getString(R.string.hiscore_title);
            message = MessageFormat.format(getString(R.string.hiscore), args );
            icon = R.drawable.hiscore;

            setBestMoveCount(countMove);
        }
        this.showEndDialog(title, message, icon);
    }

    public void onUpdateView()
    {
        drawGrid();
    }
}

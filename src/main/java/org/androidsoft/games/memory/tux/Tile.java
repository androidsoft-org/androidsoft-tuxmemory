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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pierre
 */
public class Tile
{

    private static final String ATTR_FOUND = "Found";
    private static final String ATTR_SELECTED = "Selected";
    private static final String ATTR_RESID = "ResId";
    boolean mFound;
    boolean mSelected;
    int mResId;
    private static int mNotFoundResId;

    /**
     * Constructor
     */
    Tile()
    {
    }

    /**
     * Constructor
     */
    Tile(int nResId)
    {
        mResId = nResId;
    }

    /**
     * Constructor from a JSON object
     */
    Tile(JSONObject object)
    {
        try
        {
            mFound = object.getBoolean(ATTR_FOUND);
            mSelected = object.getBoolean(ATTR_SELECTED);
            mResId = object.getInt(ATTR_RESID);
        } catch (JSONException ex)
        {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void setNotFoundResId(int nNotFoundResId)
    {
        mNotFoundResId = nNotFoundResId;
    }

    boolean isFound()
    {
        return mFound;
    }

    void setFound(boolean bFound)
    {
        mFound = bFound;
    }

    int getResId()
    {
        return (mFound || mSelected) ? mResId : mNotFoundResId;
    }

    void select()
    {
        mSelected = true;
    }

    public void unselect()
    {
        mSelected = false;
    }

    /**
     * Build a JSONObject representing the tile
     * @return a JSONObject
     */
    JSONObject json()
    {
        JSONObject object = new JSONObject();
        try
        {
            object.accumulate(ATTR_FOUND, mFound);
            object.accumulate(ATTR_SELECTED, mSelected);
            object.accumulate(ATTR_RESID, mResId);
        } catch (JSONException ex)
        {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return object;

    }
}

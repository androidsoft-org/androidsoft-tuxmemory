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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pierre
 */
public class TileList extends ArrayList<Tile>
{

    /**
     * Constructor
     */
    TileList()
    {
    }

    /**
     * Constructeur
     * @param serialized A serialized list
     */
    TileList(String serialized)
    {
        try
        {
            JSONArray array = new JSONArray(serialized);
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);
                Tile t = new Tile(object);
                add(t);
            }
        } catch (JSONException ex)
        {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Serialize the List
     * @return The list as a String
     */
    String serialize()
    {
        JSONArray array = new JSONArray();
        for (Tile t : this)
        {
            array.put(t.json());
        }
        return array.toString();
    }

    ArrayList<Tile> getSelected()
    {
        ArrayList<Tile> list = new ArrayList<Tile>();
        for (Tile t : this)
        {
            if ( t.mSelected && !t.mFound )
            {
                list.add( t );
            }
        }
        return list;
    }
}

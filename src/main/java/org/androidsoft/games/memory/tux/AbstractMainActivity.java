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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import org.androidsoft.games.utils.sound.SoundManager;

/**
 * AbstractMainActivity
 * @author pierre
 */
public abstract class AbstractMainActivity extends Activity implements OnClickListener
{

    private static final String PREF_STARTED = "started";
    private static final int SOUND_NEW_GAME = 1000;
    private static final int SPLASH_SCREEN_ROTATION_COUNT = 2;
    private static final int SPLASH_SCREEN_ROTATION_DURATION = 2000;
    private static final int GAME_SCREEN_ROTATION_COUNT = 2;
    private static final int GAME_SCREEN_ROTATION_DURATION = 2000;
    private static final String KEY_VERSION = "version";
    private static final int DEFAULT_VERSION = 1;  // should be set to 0 after 1.4
    protected boolean mQuit;
    private ViewGroup mContainer;
    private View mSplash;
    private Button mButtonPlay;
    private boolean mStarted;

    protected abstract View getGameView();

    protected abstract void newGame();

    protected abstract void about();

    /**
     * {@inheritDoc }
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);        
        SoundManager.init(AbstractMainActivity.this);

        setContentView(R.layout.main);
        mContainer = (ViewGroup) findViewById(R.id.container);
        mSplash = (View) findViewById(R.id.splash);

        mButtonPlay = (Button) findViewById(R.id.button_play);
        mButtonPlay.setOnClickListener(this);

        ImageView image = (ImageView) findViewById(R.id.image_splash);
        image.setImageResource(R.drawable.splash);

        checkLastVersion();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences prefs = getPreferences(0);
        mStarted = prefs.getBoolean(PREF_STARTED, false);
        if (mStarted)
        {
            mSplash.setVisibility(View.GONE);
            getGameView().setVisibility(View.VISIBLE);
        } else
        {
            mSplash.setVisibility(View.VISIBLE);
            getGameView().setVisibility(View.GONE);
        }

        if (!SoundManager.isInitialized())
        {
            SoundManager.init(this);
        }
        SoundManager.instance().addSound(SOUND_NEW_GAME, R.raw.new_game);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences.Editor editor = getPreferences(0).edit();
        if (!mQuit)
        {
            editor.putBoolean(PREF_STARTED, mStarted);
        } else
        {
            editor.remove(PREF_STARTED);
        }
        editor.commit();

        SoundManager.release();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_new:
                onNewGame();
                return true;
            case R.id.menu_quit:
                quit();
                return true;
            case R.id.menu_credits:
                about();
                return true;
        }
        return false;
    }

    private void onNewGame()
    {
        SoundManager.instance().playSound(SOUND_NEW_GAME);
        newGame();
    }

    /**
     * Quit the application
     */
    void quit()
    {
        mQuit = true;
        AbstractMainActivity.this.finish();
    }

    /**
     * {@inheritDoc }
     */
    public void onClick(View v)
    {
        if (v == mButtonPlay)
        {
            applyRotation(0, SPLASH_SCREEN_ROTATION_COUNT * 360);
        }
    }

    protected void showEndDialog(String title, String message, int icon)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.new_game),
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        onNewGame();
                    }
                });
        builder.setNegativeButton(getString(R.string.quit),
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        quit();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    // 3D anim from Splash Screen to Game screen
    /**
     * Setup a new 3D rotation on the container view.
     *
     * @param start the start angle at which the rotation must begin
     * @param end the end angle of the rotation
     */
    private void applyRotation(float start, float end)
    {

        // Find the center of the container
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(SPLASH_SCREEN_ROTATION_DURATION);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView());

        mContainer.startAnimation(rotation);

    }

    /**
     * This class listens for the end of the first half of the animation.
     * It then posts a new action that effectively swaps the views when the container
     * is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener
    {

        private DisplayNextView()
        {
        }

        public void onAnimationStart(Animation animation)
        {
            SoundManager.instance().playSound(SOUND_NEW_GAME);
        }

        public void onAnimationEnd(Animation animation)
        {
            mContainer.post(new SwapViews());
        }

        public void onAnimationRepeat(Animation animation)
        {
        }
    }

    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable
    {

        public void run()
        {
            final float centerX = mContainer.getWidth() / 2.0f;
            final float centerY = mContainer.getHeight() / 2.0f;
            Rotate3dAnimation rotation;

            mSplash.setVisibility(View.GONE);
            getGameView().setVisibility(View.VISIBLE);
            getGameView().requestFocus();

            rotation = new Rotate3dAnimation(0, 360 * GAME_SCREEN_ROTATION_COUNT, centerX, centerY, 310.0f, false);

            rotation.setDuration(GAME_SCREEN_ROTATION_DURATION);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());

            mContainer.startAnimation(rotation);
            mStarted = true;
        }
    }

    private void checkLastVersion()
    {
        int resTitle;
        int resMessage;
        final int lastVersion = getVersion();
        if (lastVersion < Constants.VERSION)
        {
            if (lastVersion == 0)
            {
                // This is a new install
                resTitle = R.string.first_run_dialog_title;
                resMessage = R.string.first_run_dialog_message;
            } else
            {
                // This is an upgrade.
                resTitle = R.string.whats_new_dialog_title;
                resMessage = R.string.whats_new_dialog_message;
            }
            // show what's new message
            saveVersion(Constants.VERSION);
            showWhatsNewDialog(resTitle, resMessage, R.drawable.icon);
        }
    }

    private int getVersion()
    {
        SharedPreferences prefs = getSharedPreferences(AbstractMainActivity.class.getName(), Activity.MODE_PRIVATE);
        return prefs.getInt(KEY_VERSION, DEFAULT_VERSION);
    }

    private void saveVersion(int version)
    {
        SharedPreferences prefs = getSharedPreferences(AbstractMainActivity.class.getName(), Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(KEY_VERSION, version);
        editor.commit();

    }

    protected void showWhatsNewDialog(int title, int message, int icon)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        onNewGame();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}

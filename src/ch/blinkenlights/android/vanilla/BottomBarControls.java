/*
 * Copyright (C) 2016 Adrian Ulrich <adrian@blinkenlights.ch>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ch.blinkenlights.android.vanilla;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import android.util.Log;



public class BottomBarControls extends LinearLayout
	implements View.OnFocusChangeListener
	{
	/**
	 * The application context
	 */
	private final Context mContext;
	/**
	 * The title of the currently playing song
	 */
	private TextView mTitle;
	/**
	 * The artist of the currently playing song
	 */
	private TextView mArtist;
	/**
	 * Cover image
	 */
	private ImageView mCover;
	/**
	 * The menu button
	 */
	private ImageButton mMenuButton;
	private LinearLayout mSearchContent;
	private LinearLayout mControlsContent;
	private SearchView mSearchView;


	public BottomBarControls(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override 
	public void onFinishInflate() {
		mTitle = (TextView)findViewById(R.id.title);
		mArtist = (TextView)findViewById(R.id.artist);
		mCover = (ImageView)findViewById(R.id.cover);
		mMenuButton = (ImageButton)findViewById(R.id.menu_button);
		mSearchView = (SearchView)findViewById(R.id.search_view);
		mSearchContent = (LinearLayout)findViewById(R.id.content_search);
		mControlsContent = (LinearLayout)findViewById(R.id.content_controls);

		mSearchView.setOnQueryTextFocusChangeListener(this);

		super.onFinishInflate();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus == false && v == mSearchView)
			showSearch(false);
	}

	/**
	 * Hijack this call as we do not want to be clickable:
	 * The setOnClickListener will be set on a sub element
	 * but the callback will still return this instance
	 */
	public void setOnClickListener(final View.OnClickListener l) {
		final View target = (View)this;
		mControlsContent.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				l.onClick(target);
			}
		});
	}

	/**
	 * Configures a query text listener for the search view
	 */
	public void setOnQueryTextListener(SearchView.OnQueryTextListener owner) {
		mSearchView.setOnQueryTextListener(owner);
	}


	/**
	 * Boots the options menu
	 *
	 * @param owner the activity who will receive our callbacks
	 */
	public void enableOptionsMenu(final Activity owner) {
		final PopupMenu popupMenu = new PopupMenu(mContext, mMenuButton);

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return owner.onOptionsItemSelected(item);
			}
		});
		mMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popupMenu.show();
			}
		});

		owner.onCreateOptionsMenu(popupMenu.getMenu());
		mMenuButton.setVisibility(View.VISIBLE);
	}

	/**
	 * Opens the OptionsMenu of this view
	 */
	public void openMenu() {
		mMenuButton.performClick();
	}

	/**
	 * Sets the search view to given state
	 *
	 * @param visible enables or disables the search box visibility
	 * @return boolean old state
	 */
	public boolean showSearch(boolean visible) {
		boolean wasVisible = mSearchContent.getVisibility() == View.VISIBLE;
		if (wasVisible != visible) {
			mSearchContent.setVisibility(visible ? View.VISIBLE : View.GONE);
			mControlsContent.setVisibility(visible ? View.GONE : View.VISIBLE);
			if (visible)
				mSearchView.requestFocus();
			else
				mSearchView.setQuery("", false);
		}
		return wasVisible;
	}

	/**
	 * Updates the cover image of this view
	 *
	 * @param cover the bitmap to display. Will use a placeholder image if cover is null
	 */
	public void setCover(Bitmap cover) {
		if (cover == null)
			mCover.setImageResource(R.drawable.fallback_cover);
		else
			mCover.setImageBitmap(cover);
	}

	/**
	 * Updates the song metadata
	 *
	 * @param song the song info to display, may be null
	 */
	public void setSong(Song song) {
		if (song == null) {
			mTitle.setText(null);
			mArtist.setText(null);
			mCover.setImageBitmap(null);
		} else {
			Resources res = mContext.getResources();
			String title = song.title == null ? res.getString(R.string.unknown) : song.title;
			String artist = song.artist == null ? res.getString(R.string.unknown) : song.artist;
			mTitle.setText(title);
			mArtist.setText(artist);
		}
	}


}

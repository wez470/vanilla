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

import android.content.res.Resources;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.graphics.Bitmap;

import android.util.Log;
/**
 * LinearLayout that contains some hacks for sizing inside an ActionBar.
 */
public class ActionBarControls extends LinearLayout {

	private Context mContext;
	private TextView mTitle;
	private TextView mArtist;
	private ImageView mCover;
	private ImageButton mPlayPauseButton;

	public ActionBarControls(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override 
	public void onFinishInflate() {
		mTitle = (TextView)findViewById(R.id.title);
		mArtist = (TextView)findViewById(R.id.artist);
		mCover = (ImageView)findViewById(R.id.cover);
		mPlayPauseButton = (ImageButton)findViewById(R.id.play_pause);
		super.onFinishInflate();
	}

	public ImageButton getPlayPauseButton() {
		return mPlayPauseButton;
	}

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


	public void setCover(Bitmap cover) {
		if (cover == null)
			mCover.setImageResource(R.drawable.fallback_cover);
		else
			mCover.setImageBitmap(cover);
	}

}

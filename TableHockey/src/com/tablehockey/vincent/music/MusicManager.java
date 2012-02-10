package com.tablehockey.vincent.music;

import java.util.HashMap;

import com.tablehockey.vincent.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class MusicManager {
	
	public static final int SOUND_KICK = 1;
	public static final int SOUND_HOLE = 2;
	public static final int SOUND_PA2 = 3;
	
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	private AudioManager audioManager;
	private int streamVolume;
	private static MusicManager musicManager;
	
	private MusicManager(Context context){
		soundPool = new SoundPool(4,AudioManager.STREAM_MUSIC,100);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(SOUND_KICK, soundPool.load(context, R.raw.pa, 1));
		soundPoolMap.put(SOUND_HOLE, soundPool.load(context, R.raw.into_hole, 1));
		soundPoolMap.put(SOUND_PA2, soundPool.load(context, R.raw.pa2, 1));
		audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	
	public void play(int sound){
		soundPool.play(soundPoolMap.get(sound), streamVolume, streamVolume, 0, 0,1);
	}

	public void release(){
		soundPool.release();
	}
	
	public static MusicManager getInstance(Context context){
		if(musicManager == null){
			musicManager = new MusicManager(context);
		}
		return musicManager;
	}
}

package com.joyplus.xllx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.joyplus.app.MyApp;
import com.joyplus.entity.CurrentPlayDetailData;
import com.joyplus.entity.URLS_INDEX;
import com.joyplus.ui.ArcView;
import com.joyplus.utils.Constant;
import com.joyplus.utils.Utils;

public class VideoPlayerJPActivity extends Activity implements
		MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
		MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener,
		MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener,
		MediaPlayer.OnVideoSizeChangedListener, OnSeekBarChangeListener,
		OnClickListener {
	
	private static final String TAG = "VideoPlayerActivity";

	private static final int MESSAGE_RETURN_DATE_OK = 0;
	private static final int MESSAGE_URLS_READY = MESSAGE_RETURN_DATE_OK + 1;
	private static final int MESSAGE_PALY_URL_OK = MESSAGE_URLS_READY + 1;
	private static final int MESSAGE_URL_NEXT = MESSAGE_PALY_URL_OK + 1;
	private static final int MESSAGE_UPDATE_PROGRESS = MESSAGE_URL_NEXT + 1;
	private static final int MESSAGE_HIDE_PROGRESSBAR = MESSAGE_UPDATE_PROGRESS + 1;
	private static final int MESSAGE_HIDE_VOICE = MESSAGE_HIDE_PROGRESSBAR + 1;

	/**
	 * ���ݼ���
	 */
	private static final int STATUE_LOADING = 0;
	/**
	 * ����
	 */
	private static final int STATUE_PLAYING = STATUE_LOADING + 1;
	/**
	 * ��ͣ
	 */
	private static final int STATUE_PAUSE = STATUE_PLAYING + 1;
	/**
	 * ���ˡ����
	 */
	private static final int STATUE_FAST_DRAG = STATUE_PAUSE + 1;

	private static final int OFFSET = 33;
	private int seekBarWidthOffset = 40;

	private TextView mVideoNameText; // ����
	private ImageView mDefinationIcon;// ������icon
	private SeekBar mSeekBar; // ������
	private RelativeLayout mTimeLayout; // ʱ����ʾ��
	private TextView mCurrentTimeTextView; // ��ǰ����ʱ��
	private TextView mTotalTimeTextView; // ��ʱ��
	private RelativeLayout mFastIcon; // ������ˣ���ʶͼ��
	private TextView mFastTextView; // ������ˣ���ʶ��ʾ

	private TextView mLastTimeTextView;// �ϴβ���ʱ��
	private TextView mResourceTextView;// ��Ƶ��Դ
	private TextView mSpeedTextView;// ����
	private TextView mPercentTextView;// ��ɰٷֱ�

	private ImageButton mPreButton;// ��һ��
	private ImageButton mNextButton;// ��һ��
	private ImageButton mTopButton;// ����ģ���������ť
	private ImageButton mBottomButton;// ����ģ��ղأ���ť
	private ImageButton mCenterButton;// �м�İ�ť

	private ImageButton mContinueButton;// ����

	private ArcView mVoiceProgress; // ������С��ʾ

	/**
	 * Ԥ���ز�
	 */
	private RelativeLayout mPreLoadLayout;
	/**
	 * ������ʾ��ز�
	 */
	private RelativeLayout mNoticeLayout;
	/**
	 * ���¼����Ʋ�
	 */
	private LinearLayout mControlLayout;
	/**
	 * ������ز�
	 */
	private LinearLayout mVocieLayout;

	/**
	 * ��ͣ������
	 */
	private LinearLayout mContinueLayout;

	/**
	 * �������Ų���
	 */
	private String mProd_id;
	private String mProd_name;
	private int mProd_type;
	private String mProd_src;// ��Դ
	private int mDefination = 0; // ������ 6Ϊ���ʣ�7Ϊ���壬8Ϊ����
	private String mProd_sub_name = null;
	private int mEpisodeIndex = -1; // ��ǰ������Ӧ��index
	private long lastTime = 0;

	/**
	 * �ղ�
	 */
	private boolean isShoucang = false;// Ĭ��Ϊû���ղ�

	/**
	 * ��������
	 */
	private int currentPlayIndex;
	private String currentPlayUrl;
	private List<URLS_INDEX> playUrls = new ArrayList<URLS_INDEX>();

	private AQuery aq;
	private MyApp app;

	private long mStartRX = 0;
	private long rxByteslast = 0;
	private long mLoadingPreparedPercent = 0;

	private int mStatue = 0;

	private int mTimeJumpSpeed = 0;
	private int mFastJumpTime = 0;
	int[] mTimes = { 1000, 333, 40 };

	/**
	 * android����VideoView
	 */
	private VideoView mVideoView;

	private AudioManager mAudioManager;

	/** ������� */
	private int mMaxVolume;
	/** ��ǰ���� */
	private int mVolume = -1;
	
	private Animation mAlphaDispear;
	private boolean isSeekBarIntoch = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Log.i(TAG, "onCreate--->");
		setContentView(R.layout.video_player_main);
		aq = new AQuery(this);
		app = (MyApp) getApplication();
		mAlphaDispear = AnimationUtils.loadAnimation(this, R.anim.alpha_disappear);

		
		initViews();
		mSeekBar.setEnabled(false);

		initVedioDate();

		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		winParams.buttonBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
		// winParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		win.setAttributes(winParams);
	}
	
	private void dismissView(View v){
		v.setVisibility(View.GONE);
		v.startAnimation(mAlphaDispear);
	}

	private void initVedioDate() {
		mStatue = STATUE_LOADING;
		mSeekBar.setEnabled(false);
		mPreLoadLayout.setVisibility(View.VISIBLE);
		mContinueLayout.setVisibility(View.GONE);
		mControlLayout.setVisibility(View.GONE);
		mStartRX = TrafficStats.getTotalRxBytes();// ��ȡ�����ٶ�
		if (mStartRX == TrafficStats.UNSUPPORTED) {
			mSpeedTextView
					.setText("Your device does not support traffic stat monitoring.");
		} else {

			mHandler.postDelayed(mLoadingRunnable, 500);
		}
		// ���ĳ��ӰƬ����ʱ����ȫ������CurrentPlayData
		CurrentPlayDetailData playDate = app.getmCurrentPlayDetailData();
		if (playDate == null) {// ��������þͲ�����
			finish();
			return;
		}
		// ��ʼ��������������
		mProd_id = playDate.prod_id;
		mProd_type = playDate.prod_type;
		mProd_name = playDate.prod_name;
		mProd_sub_name = playDate.prod_sub_name;
		currentPlayUrl = playDate.prod_url;
		mDefination = playDate.prod_qua;
		lastTime = (int) playDate.prod_time;
		mProd_src = playDate.prod_src;

		if(mDefination == 0){
			mDefination = 8;
		}
		
		// ���²�����Դ���ϴβ���ʱ��
		updateSourceAndTime();
		updateName();
		if (currentPlayUrl != null && URLUtil.isNetworkUrl(currentPlayUrl)) {
			if (mProd_type<0) {
//				mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
				new Thread(new UrlRedirectTask()).start();
			} 
		} 
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MESSAGE_PALY_URL_OK:
				updateName();
				updateSourceAndTime();
				mVideoView.setVideoURI(Uri.parse(currentPlayUrl));
				if (lastTime > 0) {
					mVideoView.seekTo((int) lastTime);
				}
				mVideoView.start();
				break;
			case MESSAGE_UPDATE_PROGRESS:
				updateSeekBar();
				break;
			case MESSAGE_HIDE_PROGRESSBAR:
				dismissView(mNoticeLayout);
//				mNoticeLayout.setVisibility(View.GONE);
				break;
			case MESSAGE_HIDE_VOICE:
				dismissView(mVocieLayout);
//				mVocieLayout.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};
	
	private void updateName() {
		switch (mProd_type) {
		case -1:
		case 1:
			mVideoNameText.setText(mProd_name);
			break;
		case 2:
		case 131:
			mVideoNameText.setText(mProd_name + " ��" + mProd_sub_name + "��");
			break;
		case 3:
			mVideoNameText.setText(mProd_name + " " + mProd_sub_name);
			break;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "onStart--->");
		
		super.onStart();
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	private void onVolumeSlide(int index) {
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// �������
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
		int mAngle = index * 360 / mMaxVolume;
		// ���������
		if (index == 0)
			mVoiceProgress.setBackgroundResource(R.drawable.player_volume_mute);
		else {
			mVoiceProgress.setBackgroundResource(R.drawable.player_volume);

		}
		mVoiceProgress.SetAngle(mAngle);

	}

	private void initViews() {
		mVideoNameText = (TextView) findViewById(R.id.tv_play_name);
		mDefinationIcon = (ImageView) findViewById(R.id.iv_1080_720);
		mSeekBar = (SeekBar) findViewById(R.id.sb_seekbar);
		mTimeLayout = (RelativeLayout) findViewById(R.id.rl_popup_time);
		mCurrentTimeTextView = (TextView) findViewById(R.id.tv_popup_time_current_time);
		mTotalTimeTextView = (TextView) findViewById(R.id.tv_total_time);
		mFastIcon = (RelativeLayout) findViewById(R.id.rl_popup_time_fast);
		mFastTextView = (TextView) findViewById(R.id.tv_popup_time_fast);

		mLastTimeTextView = (TextView) findViewById(R.id.tv_preload_bofang_record);
		mResourceTextView = (TextView) findViewById(R.id.tv_preload_source_laizi);// ��Ƶ��Դ
		mSpeedTextView = (TextView) findViewById(R.id.tv_preload_network_kb);
		mPercentTextView = (TextView) findViewById(R.id.tv_preload_network_accomplish);

		mPreButton = (ImageButton) findViewById(R.id.ib_control_left);
		mNextButton = (ImageButton) findViewById(R.id.ib_control_right);
		mTopButton = (ImageButton) findViewById(R.id.ib_control_top);
		mBottomButton = (ImageButton) findViewById(R.id.ib_control_bottom);
		mCenterButton = (ImageButton) findViewById(R.id.ib_control_center);
		mContinueButton = (ImageButton) findViewById(R.id.btn_continue);

		mPreButton.setOnClickListener(this);
		mNextButton.setOnClickListener(this);
		mTopButton.setOnClickListener(this);
		mBottomButton.setOnClickListener(this);
		mCenterButton.setOnClickListener(this);
		mContinueButton.setOnClickListener(this);

		mVoiceProgress = (ArcView) findViewById(R.id.av_volume);

		mPreLoadLayout = (RelativeLayout) findViewById(R.id.rl_preload);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		// opt.inPreferredConfig = Bitmap.Config.RGB_565; // Each pixel is
		// stored 2 bytes
		// opt.inPreferredConfig = Bitmap.Config.ARGB_8888; //Each pixel is
		// stored 4 bytes

		opt.inTempStorage = new byte[16 * 1024];
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		try {
			mPreLoadLayout.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(
					getResources(), R.drawable.player_bg, opt)));
		} catch (OutOfMemoryError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mNoticeLayout = (RelativeLayout) findViewById(R.id.rl_titile_seekbar);
		mControlLayout = (LinearLayout) findViewById(R.id.ll_control_buttons);
		mVocieLayout = (LinearLayout) findViewById(R.id.ll_volume);
		mContinueLayout = (LinearLayout) findViewById(R.id.ll_continue);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(mStatue == STATUE_PLAYING){
					mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
					mNoticeLayout.setVisibility(View.VISIBLE);
					mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 2500);	
				}
				return false;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "keycode ---------->" + keyCode);
		Log.d(TAG, "mStatue ---------->" + mStatue);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE:
			switch (mStatue) {
			case STATUE_LOADING:
				finish();
				return true;
			case STATUE_PLAYING:
				if (mProd_type == 2 || mProd_type == 131 || mProd_type == 3) {
					showControlLayout();
					return true;
				} else {
					// mVideoView.stopPlayback();
					finish();
				}
				break;
			case STATUE_PAUSE:
				return true;
			case STATUE_FAST_DRAG:
				mTimeJumpSpeed = 0;
				upDateFastTimeBar();
				mHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
				mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
				mStatue = STATUE_PLAYING;
				mSeekBar.setProgress(mVideoView.getCurrentPosition());
				mSeekBar.setEnabled(true);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			switch (mStatue) {
			case STATUE_PLAYING:
				
				mVocieLayout.setVisibility(View.GONE);
				mHandler.removeMessages(MESSAGE_HIDE_VOICE);
				mStatue = STATUE_PAUSE;
				mSeekBar.setEnabled(false);
				mVideoView.pause();
				mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
				mContinueLayout.setVisibility(View.VISIBLE);
				mNoticeLayout.setVisibility(View.VISIBLE);
				mContinueButton.requestFocus();
				break;
			case STATUE_FAST_DRAG:
				if (mFastJumpTime < mVideoView.getDuration()) {
					mVideoView.seekTo(mFastJumpTime);
					mSeekBar.setProgress(mFastJumpTime);
				}else{
					mSeekBar.setProgress(mVideoView.getCurrentPosition());
				}
				mTimeJumpSpeed = 0;
				upDateFastTimeBar();
				mHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
				mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
				mStatue = STATUE_PLAYING;
				mSeekBar.setEnabled(true);
				break;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (mStatue == STATUE_PLAYING) {
				mHandler.removeMessages(MESSAGE_HIDE_VOICE);
				mVolume = mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (mVolume < 0) {
					mVolume = 0;
				}
				mVocieLayout.setVisibility(View.VISIBLE);
				mVolume++;
				onVolumeSlide(mVolume);
				mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_VOICE, 2500);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (mStatue == STATUE_PLAYING) {
				mHandler.removeMessages(MESSAGE_HIDE_VOICE);
				mVolume = mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (mVolume < 0) {
					mVolume = 0;
				}
				mVocieLayout.setVisibility(View.VISIBLE);
				mVolume++;
				onVolumeSlide(mVolume);
				mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_VOICE, 2500);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (mStatue == STATUE_PLAYING) {
				mHandler.removeMessages(MESSAGE_HIDE_VOICE);
				mVolume = mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (mVolume < 0) {
					mVolume = 0;
				}
				mVocieLayout.setVisibility(View.VISIBLE);
				mVolume--;
				onVolumeSlide(mVolume);
				mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_VOICE, 2500);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (mStatue == STATUE_PLAYING) {
				mHandler.removeMessages(MESSAGE_HIDE_VOICE);
				mVolume = mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (mVolume < 0) {
					mVolume = 0;
				}
				mVocieLayout.setVisibility(View.VISIBLE);
				mVolume--;
				onVolumeSlide(mVolume);
				mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_VOICE, 2500);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (mStatue == STATUE_PLAYING) {
				mStatue = STATUE_FAST_DRAG;
				mSeekBar.setEnabled(false);
				mTimeJumpSpeed = -1;
				mFastJumpTime = (int) mVideoView.getCurrentPosition();
				upDateFastTimeBar();
				return true;
			} else if (mStatue == STATUE_FAST_DRAG) {
				switch (mTimeJumpSpeed) {
				case -1:
				case -2:
					mTimeJumpSpeed -= 1;
					break;
				case 1:
					mTimeJumpSpeed = -1;
					break;
				case 2:
				case 3:
					mTimeJumpSpeed = 1;
				}
				upDateFastTimeBar();
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (mStatue == STATUE_PLAYING) {
				mStatue = STATUE_FAST_DRAG;
				mSeekBar.setEnabled(false);
				mTimeJumpSpeed = 1;
				mFastJumpTime = (int) mVideoView.getCurrentPosition();
				upDateFastTimeBar();
				return true;
			} else if (mStatue == STATUE_FAST_DRAG) {
				switch (mTimeJumpSpeed) {
				case 1:
				case 2:
					mTimeJumpSpeed += 1;
					break;
				case -1:
					mTimeJumpSpeed = 1;
					break;
				case -2:
				case -3:
					mTimeJumpSpeed = -1;
				}
				upDateFastTimeBar();
				return true;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showControlLayout() {
		
		mVocieLayout.setVisibility(View.GONE);
		mHandler.removeMessages(MESSAGE_HIDE_VOICE);
		mStatue = STATUE_PAUSE;
		mSeekBar.setEnabled(false);
		mVideoView.pause();
		mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
		mControlLayout.setVisibility(View.VISIBLE);
		mNoticeLayout.setVisibility(View.VISIBLE);
		mCenterButton.requestFocus();
		// if( getCurrentFocus().getId() != mSeekBar.getId()) {
		//
		// mSeekBar.requestFocus();
		// }
		//
		// Log.d(TAG,"FOUCED ID -->" + getCurrentFocus().getId());
		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// mTopButton.requestFocus();
		// }
		// }, 200);
	}

	private void upDateFastTimeBar() {
		if (mTimeJumpSpeed > 0) {
			mFastIcon.setVisibility(View.VISIBLE);
			mFastIcon.setBackgroundResource(R.drawable.play_time_right);
			mFastTextView.setText("x" + Math.abs(mTimeJumpSpeed));
			mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
			mNoticeLayout.setVisibility(View.VISIBLE);
		} else if (mTimeJumpSpeed < 0) {
			mFastIcon.setVisibility(View.VISIBLE);
			mFastIcon.setBackgroundResource(R.drawable.play_time_left);
			mFastTextView.setText("x" + Math.abs(mTimeJumpSpeed));
			mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
			mNoticeLayout.setVisibility(View.VISIBLE);
		} else if (mTimeJumpSpeed == 0) {
			mFastIcon.setVisibility(View.GONE);
			mFastTextView.setText("");
			mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
			mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 2500);
		}
		// mHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
		// mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 500);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		// ���������� ѡ��һ����ַ
//		mHandler.sendEmptyMessage(MESSAGE_URL_NEXT);
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		// �������
	}
	
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		// ׼������
		mTotalTimeTextView.setText(Utils.formatDuration(mVideoView
				.getDuration()));
		mSeekBar.setMax((int) mVideoView.getDuration());
		mSeekBar.setOnSeekBarChangeListener(VideoPlayerJPActivity.this);
		mSeekBar.setProgress((int) lastTime);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		}, 500);
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		// ������ˣ��϶��� ϵͳ��֧�֣�
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		// �������
	}

	private void updateSeekBar() {
		switch (mStatue) {
		case STATUE_LOADING:
			long current = mVideoView.getCurrentPosition();// ��ǰ����
			long lastProgress = mSeekBar.getProgress();
			Log.d(TAG, "loading --->" + current);
			// updateTimeNoticeView(mSeekBar.getProgress());
			if(current>lastProgress){
				hidePreLoad(); 
			}else{
				mSeekBar.setProgress((int) current);
				mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
			}
			break;
		case STATUE_PLAYING:
			if(!isSeekBarIntoch){
				long current1 = mVideoView.getCurrentPosition();// ��ǰ����
				mSeekBar.setProgress((int) current1);
				// updateTimeNoticeView(mSeekBar.getProgress());
			}
			mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
			break;
		case STATUE_FAST_DRAG:
			if (mTimeJumpSpeed > 0) {
				mFastJumpTime = (int) (mFastJumpTime + (mVideoView
						.getDuration() / 500));
			} else if (mTimeJumpSpeed < 0) {
				mFastJumpTime = (int) (mFastJumpTime - (mVideoView
						.getDuration() / 500));
			}

			if (mFastJumpTime > mVideoView.getDuration()) {
				mFastJumpTime = (int) mVideoView.getDuration();
			}
			if (mFastJumpTime < 0) {
				mFastJumpTime = 0;
			}
			mSeekBar.setProgress(mFastJumpTime);
			// updateTimeNoticeView(mSeekBar.getProgress());
			mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS,
					mTimes[Math.abs(mTimeJumpSpeed) - 1]);
			break;
		default:
			mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
			break;
		}
	}

	private void updateTimeNoticeView(int progress) {
		RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		parms.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

		double mLeft = (double) progress / mVideoView.getDuration()
				* (mSeekBar.getMeasuredWidth() - seekBarWidthOffset) + OFFSET;

		if (progress > 0)
			parms.leftMargin = (int) mLeft;
		else
			parms.leftMargin = OFFSET;
		parms.bottomMargin = 20 + 10;
		mTimeLayout.setLayoutParams(parms);

		mCurrentTimeTextView.setText(Utils.formatDuration(progress));
		mCurrentTimeTextView.setVisibility(View.VISIBLE);
	}

	private final Runnable mLoadingRunnable = new Runnable() {
		long beginTimeMillis, timeTakenMillis, m_bitrate;

		public void run() {

			// long txBytes = TrafficStats.getTotalTxBytes()- mStartTX;
			// TX.setText(Long.toString(txBytes));
			long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;

			timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
			beginTimeMillis = System.currentTimeMillis();
			if(timeTakenMillis!=0){
				m_bitrate = ((rxBytes - rxByteslast) * 8 * 1000 / timeTakenMillis) / 8000;
				rxByteslast = rxBytes;

				mSpeedTextView.setText("��" + Long.toString(m_bitrate) + "kb/s��");
				mLoadingPreparedPercent = mLoadingPreparedPercent + m_bitrate;
				if (mLoadingPreparedPercent >= 100
						&& mLoadingPreparedPercent / 100 < 100)
					mPercentTextView.setText(", �����"
							+ Long.toString(mLoadingPreparedPercent / 100) + "%");

				// Fun_downloadrate();
			}
			mHandler.postDelayed(mLoadingRunnable, 500);
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		updateTimeNoticeView(mSeekBar.getProgress());
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		isSeekBarIntoch = true;
		mHandler.removeMessages(MESSAGE_HIDE_PROGRESSBAR);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		isSeekBarIntoch = false;
		mVideoView.seekTo(mSeekBar.getProgress());
		mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 2500);
//		mHandler.re
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_control_top:
//			mControlLayout.setVisibility(View.GONE);
			dismissView(mControlLayout);
			mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 2500);
			mStatue = STATUE_PLAYING;
			mSeekBar.setEnabled(true);
			mVideoView.requestFocus();
			mVideoView.start();

			break;
		case R.id.btn_continue:
//			mContinueLayout.setVisibility(View.GONE);
			dismissView(mContinueLayout);
			mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 2500);
			mStatue = STATUE_PLAYING;
			mSeekBar.setEnabled(true);
			mVideoView.requestFocus();
			mVideoView.start();
			break;
		case R.id.ib_control_center:
			// mVideoView.stopPlayback();
			finish();
			break;
		case R.id.ib_control_left:
			playPrevious();
			break;
		case R.id.ib_control_right:
			playNext();
			break;
		case R.id.ib_control_bottom:
			break;
		default:
			break;
		}
	}

	private void showLoading() {
		
		mLoadingPreparedPercent = 0;
		rxByteslast = 0;
		mStartRX = TrafficStats.getTotalRxBytes();
		if (mStartRX == TrafficStats.UNSUPPORTED) {
			mSpeedTextView
					.setText("Your device does not support traffic stat monitoring.");
		} else {
			mHandler.postDelayed(mLoadingRunnable, 500);
		}
		mPercentTextView.setText("�����0%");
		mPreLoadLayout.setVisibility(View.VISIBLE);
		mNoticeLayout.setVisibility(View.VISIBLE);
//		mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 2500);
	}

	private void playNext() {
		// TODO Auto-generated method stub
		mStatue = STATUE_LOADING;
		mSeekBar.setProgress(0);
		mSeekBar.setEnabled(false);
		mHandler.removeCallbacksAndMessages(this);
		mControlLayout.setVisibility(View.GONE);
		lastTime = 0;
		mVideoView.stopPlayback();
		showLoading();
		if (mProd_type == 3) {
			mEpisodeIndex -= 1;
		} else {
			mEpisodeIndex += 1;
		}
		mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
	}

	private void playPrevious() {
		// TODO Auto-generated method stub
		mStatue = STATUE_LOADING;
		mSeekBar.setProgress(0);
		mSeekBar.setEnabled(false);
		mHandler.removeCallbacksAndMessages(this);
		mControlLayout.setVisibility(View.GONE);
		lastTime = 0;
		mVideoView.stopPlayback();
		showLoading();
		if (mProd_type == 3) {
			mEpisodeIndex += 1;
		} else {
			mEpisodeIndex -= 1;
		}
		mHandler.sendEmptyMessage(MESSAGE_RETURN_DATE_OK);
	}
	
	private void updateSourceAndTime() {
		Log.d(TAG, " ---- sre = " + mProd_src);
		if (mProd_src == null || mProd_src.length() == 1
				|| "null".equals(mProd_src)) {
			mResourceTextView.setText("");
		} else {
			String strSrc = "";
			if (mProd_src.equalsIgnoreCase("wangpan")) {
				strSrc = "PPTV";
			} else if (mProd_src.equalsIgnoreCase("le_tv_fee")) {
				strSrc = "��  ��";
			} else if (mProd_src.equalsIgnoreCase("letv")) {
				strSrc = "��  ��";
			} else if (mProd_src.equalsIgnoreCase("fengxing")) {
				strSrc = "��  ��";
			} else if (mProd_src.equalsIgnoreCase("qiyi")) {
				strSrc = "��  ��  ��";
			} else if (mProd_src.equalsIgnoreCase("youku")) {
				strSrc = "��  ��";
			} else if (mProd_src.equalsIgnoreCase("sinahd")) {
				strSrc = "��  ��  ��  Ƶ";
			} else if (mProd_src.equalsIgnoreCase("sohu")) {
				strSrc = "��  ��  ��  Ƶ";
			} else if (mProd_src.equalsIgnoreCase("qq")) {
				strSrc = "��  Ѷ  ��  Ƶ";
			} else if (mProd_src.equalsIgnoreCase("pptv")) {
				strSrc = "PPTV";
			} else if (mProd_src.equalsIgnoreCase("m1905")) {
				strSrc = "��  Ӱ  ��";
			} else {
				strSrc = "PPTV";
			}
			mResourceTextView.setText(strSrc);
		}
		if(lastTime>0){
			mLastTimeTextView.setVisibility(View.VISIBLE);
			mLastTimeTextView.setText("�ϴβ���: " + Utils.formatDuration(lastTime));
		}else{
			mLastTimeTextView.setVisibility(View.GONE);
		}
		if(playUrls.size()>0&&currentPlayIndex<=playUrls.size()-1){
			Log.d(TAG, "type---->" + playUrls.get(currentPlayIndex).defination_from_server);
			mDefinationIcon.setVisibility(View.VISIBLE);
			if(Constant.player_quality_index[0].equalsIgnoreCase(playUrls.get(currentPlayIndex).defination_from_server)){
				mDefinationIcon.setImageResource(R.drawable.player_1080p);
			}else if(Constant.player_quality_index[1].equalsIgnoreCase(playUrls.get(currentPlayIndex).defination_from_server)){
				mDefinationIcon.setImageResource(R.drawable.player_720p);
			}else{
				mDefinationIcon.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "onDestroy--->");
		
		if (mVideoView != null) {
			mVideoView.stopPlayback();
		}
		
		Utils.recycleBitmap(((BitmapDrawable)mPreLoadLayout.getBackground()).getBitmap());
		
		super.onDestroy();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		 Dialog alertDialog = new AlertDialog.Builder(this). 
	                setTitle("��ʾ"). 
	                setMessage("����Ƶ�޷�����"). 
	                setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}

					}).
	                create();
		 	alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					finish();
				}
			});
	        alertDialog.show(); 
		return super.onCreateDialog(id);
	}
	
	/**
	 * ��ַ��ת
	 */
	
	class  UrlRedirectTask implements Runnable{

		@Override
		public void run() {
			
			// TODO Auto-generated method stub
			
			Log.i(TAG, "UrlRedirectTask-->" + currentPlayUrl);
			
			if(currentPlayUrl != null && !currentPlayUrl.equals("")) {
				
				if(currentPlayUrl.indexOf(("{now_date}")) != -1) {
					
					currentPlayUrl = currentPlayUrl.replace("{now_date}", System.currentTimeMillis()/1000 + "");
				}
			}
			
			String str = getRedirectUrl();
			
			if(str!=null){
				currentPlayUrl = str;
				mHandler.sendEmptyMessage(MESSAGE_PALY_URL_OK);
			}else{
//				mHandler.sendEmptyMessage(MESSAGE_URL_NEXT);
			}
		}
		
	}
	
	private String getRedirectUrl(){
		String urlStr = null;
//		while(urlStr == null) {
			
		List<String> list = new ArrayList<String>();
		
		try {
			urlRedirect(currentPlayUrl,list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//��ʱ�쳣
		}
		if(list.size() > 0) {
			 urlStr = list.get(list.size() -1);
		}
//		}
		return urlStr;
	}
	
	private void urlRedirect(String urlStr,List<String> list) {
		
		// ģ����ios�������� ʹ��userAgent
		AndroidHttpClient mAndroidHttpClient = AndroidHttpClient
				.newInstance(Constant.USER_AGENT_IOS);

		HttpParams httpParams = mAndroidHttpClient.getParams();
		// ����ʱ���5�룬���Ը���
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000 * 1);
		
		URL url;
		try {
			url = new URL(urlStr);
//			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(),null);
//			HttpGet mHttpGet = new HttpGet(uri);
			HttpGet mHttpGet = new HttpGet(url.toURI());
			HttpResponse response = mAndroidHttpClient.execute(mHttpGet);
			StatusLine statusLine = response.getStatusLine();
			
			int status = statusLine.getStatusCode();
			Log.i(TAG, "HTTP STATUS : " + status);
			
			if (status == HttpStatus.SC_OK) {
				Log.i(TAG, "HttpStatus.SC_OK--->" + urlStr);
				// ��ȷ�Ļ�ֱ�ӷ��أ�����������Ĳ���
				mAndroidHttpClient.close();
				list.add(urlStr);
				
				return;//���治ִ��
			} else {
				
				Log.i(TAG, "NOT HttpStatus.SC_OK--->" + urlStr);
				
				if (status == HttpStatus.SC_MOVED_PERMANENTLY || // ��ַ�������Ƴ�
						status == HttpStatus.SC_MOVED_TEMPORARILY || // ��ַ��ʱ���Ƴ�
						status == HttpStatus.SC_SEE_OTHER || // ���¶�λ��Դ
						status == HttpStatus.SC_TEMPORARY_REDIRECT) {// ��ʱ����
					
					Header header = response.getFirstHeader("Location");// �õ����¶�λ���header
					
					if(header != null) {
						
						String location = header.getValue();// ��header����ȡ����Ϣ
						Log.i(TAG, "Location: " + location);
						if(location != null && !location.equals("")) {
							
							urlRedirect(location, list);
							
							mAndroidHttpClient.close();// �رմ˴�����
							return;//���治ִ��
						}
					}
					
					list.add(null);
					mAndroidHttpClient.close();
					
					return;

				} else {//��ַ��Ĳ�����
					
					mAndroidHttpClient.close();
					list.add(null);
					
					return;//���治ִ��
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	private void hidePreLoad(){
		Log.d(TAG, "hidePreLoad----------->");
		mPreLoadLayout.setVisibility(View.GONE);
		mHandler.removeCallbacks(mLoadingRunnable);
		mStatue = STATUE_PLAYING;
		mSeekBar.setEnabled(true);
		mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
		mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_PROGRESSBAR, 5000);
	}
}

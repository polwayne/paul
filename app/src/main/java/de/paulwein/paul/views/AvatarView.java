package de.paulwein.paul.views;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.paulwein.paul.R;
import de.paulwein.paul.config.Config;

public class AvatarView extends RelativeLayout{
	
	private TextView tv_speechbubble;
	private ImageView iv_avatar;
	private ImageView iv_action;
	private ImageView iv_image_frame;
	private Context mContext;
	private Handler mHandler;
	private boolean mPaul = true;
	
	public AvatarView(Context context){
		super(context);
		init(context);
	}
	
	public AvatarView(Context context,AttributeSet attr){
		super(context,attr);
		init(context);
	}
	
	private void init(Context context){
		this.mContext = context;
		View v = LayoutInflater.from(mContext).inflate(R.layout.avatar_view, null,false);
		tv_speechbubble = (TextView) v.findViewById(R.id.tv_speechbubble);
		iv_avatar = (ImageView) v.findViewById(R.id.iv_avatar);
		iv_action = (ImageView) v.findViewById(R.id.iv_action);
		iv_image_frame = (ImageView) v.findViewById(R.id.iv_image_frame);
		addView(v);
		mPaul = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Config.PREF_AVATAR_GENDER,true);
		iv_avatar.setBackgroundResource(mPaul ? R.drawable.paul_one : R.drawable.paola_one);
		mHandler = new Handler();
		mHandler.postDelayed(show_eyes, 1000);
	}	
	
	private Runnable show_eyes = new Runnable() {
		@Override
		public void run() {
			iv_avatar.setBackgroundResource(mPaul ? R.anim.paul_eyes : R.anim.paola_eyes);
			AnimationDrawable eyesAnimation = (AnimationDrawable) iv_avatar.getBackground();
			eyesAnimation.start();			
		}
	};
    
    public void saySomething(String sentence){
    	tv_speechbubble.setText(sentence);
    	Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
  	    myFadeInAnimation.setAnimationListener(new AnimationListener() {
  			@Override
  			public void onAnimationStart(Animation animation) {
  				tv_speechbubble.setVisibility(View.VISIBLE);
  			}
  			@Override
  			public void onAnimationRepeat(Animation animation) {}
  			@Override
  			public void onAnimationEnd(Animation animation) {
  				tv_speechbubble.setVisibility(View.GONE);
  			}
  		});
  		tv_speechbubble.startAnimation(myFadeInAnimation);		
    }
	
    public void beQuiet(){
    	tv_speechbubble.setVisibility(View.GONE);
    }
    
    public void greet(){
    	saySomething(mContext.getString(mPaul ? R.string.hallo_paul : R.string.hallo_paola));
    }
    
    public void showActionView(int resId){
    	iv_action.setImageResource(resId);
    	Animation slideInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.action_slide_in);
      	slideInAnimation.setAnimationListener(new AnimationListener() {
    			public void onAnimationStart(Animation animation) {
    				iv_action.setVisibility(View.VISIBLE);
    			}
    			public void onAnimationRepeat(Animation animation) {}
    			public void onAnimationEnd(Animation animation) {}
    		});
    	iv_action.startAnimation(slideInAnimation);
    }
	
    public void hideActionView(){
    	Animation slideOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.action_slide_out);
    	slideOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				iv_action.setVisibility(View.GONE);
		    	LayoutParams params = (LayoutParams) iv_action.getLayoutParams();
		    	params.width = LayoutParams.WRAP_CONTENT;
		    	params.height =LayoutParams.WRAP_CONTENT;
				iv_action.setLayoutParams(params);
			}
		});
    	iv_action.startAnimation(slideOutAnimation);
    }
    
    public void showQuickActionView(Bitmap bmp){
    	LayoutParams params = (LayoutParams) iv_action.getLayoutParams();
    	params.width = 150;
    	params.height = 150;
    	iv_action.setLayoutParams(params);
    	iv_action.setImageBitmap(bmp);
    	Animation slideInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.action_slide_in);
      	slideInAnimation.setAnimationListener(new AnimationListener() {
    			public void onAnimationStart(Animation animation) {
    				iv_action.setVisibility(View.VISIBLE);
    			}
    			public void onAnimationRepeat(Animation animation) {}
    			public void onAnimationEnd(Animation animation) {}
    		});
    	iv_action.startAnimation(slideInAnimation);
    	mHandler.postDelayed(new Runnable() {
			public void run() {
				hideActionView();
			}
		}, 2500);
    }
    
    public void showQuickActionView(int resId){
    	showActionView(resId);
		 mHandler.postDelayed(new Runnable() {
			public void run() {
				hideActionView();
			}
		}, 2500);
    }
    
    public void showImageInFrame(Bitmap bmp){
    	iv_image_frame.setImageBitmap(bmp);
    	Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.fadein);
    	myFadeInAnimation.setAnimationListener(new AnimationListener() {
 			@Override
 			public void onAnimationStart(Animation animation) {
 				iv_image_frame.setVisibility(View.VISIBLE);
 			}
 			@Override
 			public void onAnimationRepeat(Animation animation) {}
 			@Override
 			public void onAnimationEnd(Animation animation) {
 				iv_image_frame.setVisibility(View.GONE);
 			}
 		});
 	  iv_image_frame.startAnimation(myFadeInAnimation);	
    }
}

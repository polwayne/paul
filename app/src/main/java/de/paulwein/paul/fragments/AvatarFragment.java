package de.paulwein.paul.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.paulwein.paul.R;
import de.paulwein.paul.views.AvatarView;

public class AvatarFragment extends Fragment {
	
	private AvatarView mAvatar;
	private TextToSpeech mTts;
	
	public AvatarFragment(){
		super();
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View layout = inflater.inflate(R.layout.avatar_fragment, container,false);
		mAvatar = (AvatarView) layout.findViewById(R.id.avatarView);
		mAvatar.greet();
		initTextToSpeech();
		return layout;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mTts != null)
			mTts.shutdown();
	}
	
	public void saySomething(String something){
		mAvatar.saySomething(something);
	}
	
	 public void beQuiet(){
		 mAvatar.beQuiet();
	 }
	 
	 public void showActionView(int resId){
		 mAvatar.showActionView(resId);
	 }
	 
	 public void showQuickActionView(int resId){
		 mAvatar.showQuickActionView(resId);
	 }
	 
	 public void showQuickActionView(Bitmap bmp){
		 mAvatar.showQuickActionView(bmp);
	 }
	 
	 public void hideActionView(){
		 mAvatar.hideActionView();
	 }
	
	 public void showImageInFrame(Bitmap bmp){
		 mAvatar.showImageInFrame(bmp);
	 }
	 
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
    
    private void initTextToSpeech(){
    	mTts = new TextToSpeech(getActivity(), null);
    }
    
    public void speak(String words){
    	mTts.speak(words, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    
	
}

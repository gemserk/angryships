package com.gemserk.games.angryships;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gemserk.commons.adwhirl.AdWhirlAndroidHandler;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.adwhirl.CustomAdViewHandler;
import com.gemserk.commons.adwhirl.PausableAdWhirlLayout;
import com.gemserk.commons.gdx.DensityUtilsImpl;

public class AndroidApplication extends com.badlogic.gdx.backends.android.AndroidApplication implements AdWhirlInterface {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useGL20 = false;
		config.useAccelerometer = true;
		config.useCompass = true;
		config.useWakelock = true;

		Game game = new Game();

		View gameView = initializeForView(game, config);

		AdWhirlManager.setConfigExpireTimeout(1000 * 15);
		AdWhirlTargeting.setAge(23);
		AdWhirlTargeting.setGender(AdWhirlTargeting.Gender.MALE);
		AdWhirlTargeting.setKeywords("online games gaming");
		AdWhirlTargeting.setPostalCode("94123");
		AdWhirlTargeting.setTestMode(false);
		PausableAdWhirlLayout adView = new PausableAdWhirlLayout(this, "5d99c9fc499b41e5be30b22e3b52d799");
		Handler handler = new AdWhirlAndroidHandler(adView);
		AdWhirlViewHandler adWhirlViewHandler = new CustomAdViewHandler(handler);

		// AdWhirlViewHandler adWhirlViewHandler = new AdWhirlViewHandler();

		game.setAdWhirlViewHandler(adWhirlViewHandler);
		game.setDensityUtils(new DensityUtilsImpl());

		int diWidth = 320;
		int diHeight = 52;

		float density = getResources().getDisplayMetrics().density;
		adView.setAdWhirlInterface(this);

		adView.setMaxWidth((int) (diWidth * density));
		adView.setMaxHeight((int) (diHeight * density));
		log(GameInformation.applicationId, "Device max Ads area: (" + (int) (diWidth * density) + ", " + (int) (diHeight * density) + ")");

		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		layout.addView(gameView);
		layout.addView(adView, adParams);

		setContentView(layout);
	}

	@Override
	public void adWhirlGeneric() {
		// TODO Auto-generated function stub

	}

}
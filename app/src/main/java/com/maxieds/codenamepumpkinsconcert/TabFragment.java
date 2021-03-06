package com.maxieds.codenamepumpkinsconcert;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.util.Locale;

/**
 * <h1>Tab Fragment</h1>
 * Implements a Fragment for individual tab data in the application.
 *
 * @author  Maxie D. Schmidt
 * @since   12/31/17
 */
public class TabFragment extends Fragment {

    private static final String TAG = TabFragment.class.getSimpleName();

    /**
     * Definitions of the in-order tab indices.
     */
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int TAB_COVERT_MODE = 0;
    public static final int TAB_LIVE_PANEL = 1;
    public static final int TAB_TOOLS = 2;
    public static final int TAB_SETTINGS = 3;
    public static final int TAB_ABOUT = 4;

    /**
     * Local tab-specific data stored by the class.
     */
    private int tabNumber;
    private int layoutResRef;
    private View inflatedView;

    /**
     * Effectively the default constructor used to obtain a new tab of the specified index.
     * @param page
     * @return
     */
    public static TabFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabFragment fragment = new TabFragment();
        fragment.tabNumber = page;
        fragment.setArguments(args);
        switch(page) {
            case TAB_COVERT_MODE:
                fragment.layoutResRef = R.layout.covert_tab;
                break;
            case TAB_LIVE_PANEL:
                fragment.layoutResRef = R.layout.live_tab;
                break;
            case TAB_TOOLS:
                fragment.layoutResRef = R.layout.tools_tab;
                break;
            case TAB_SETTINGS:
                fragment.layoutResRef = R.layout.settings_tab;
                break;
            case TAB_ABOUT:
                fragment.layoutResRef = R.layout.about_tab;
                break;
            default:
                break;
        }
        return fragment;
    }

    /**
     * Called when the tab is created.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabNumber = getArguments().getInt(ARG_PAGE);
    }

    /**
     * Inflates the layout and sets up the configuration of the widgets associated with each tab index.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View inflated tab
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(layoutResRef, container, false);
        inflatedView = view;
        MainActivity.defaultInflater = inflater;
        if(tabNumber == TAB_COVERT_MODE) {}
        else if(tabNumber == TAB_LIVE_PANEL) {

            AVRecordingService.videoPreviewView = (TextureView) inflatedView.findViewById(R.id.camera_preview);
            AVRecordingService.videoPreview = new SurfaceTexture(0);
            AVRecordingService.videoPreview.detachFromGLContext();
            AVRecordingService.videoPreviewView.setSurfaceTexture(AVRecordingService.videoPreview);
            AVRecordingService.videoPreviewBGOverlay = MainActivity.runningActivity.getResources().getDrawable(R.drawable.kitty256v5); // v6
            //AVRecordingService.videoPreviewView.setBackgroundDrawable(AVRecordingService.videoPreviewBGOverlay);

            TextView tvLoggingMessages = new TextView(MainActivity.runningActivity); //(TextView) inflatedView.findViewById(R.id.textLogging);
            tvLoggingMessages.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
            tvLoggingMessages.setAllCaps(true);
            tvLoggingMessages.setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY);
            tvLoggingMessages.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            tvLoggingMessages.setMaxLines(12);
            tvLoggingMessages.setTextColor(getResources().getColor(R.color.colorAccent));
            tvLoggingMessages.setTextSize(10);
            if(MainActivity.tvLoggingMessages != null)
                tvLoggingMessages.setText(MainActivity.tvLoggingMessages.getText().toString());
            tvLoggingMessages.setCompoundDrawables(MainActivity.runningActivity.getResources().getDrawable(R.drawable.log32), null, null, null);
            MainActivity.tvLoggingMessages = tvLoggingMessages;
        }
        else if(tabNumber == TAB_TOOLS) {
            // we can't easily modify the video settings when the recorder is running, so we don't need peripheral adapters for these,
            // but we do want to make sure that we can find them when we start a recording to set these video options:
            AVRecordingService.videoOptsAntiband = (Spinner) inflatedView.findViewById(R.id.videoOptsAntibandSpinner);
            AVRecordingService.videoOptsEffects = (Spinner) inflatedView.findViewById(R.id.videoOptsEffectsSpinner);
            AVRecordingService.videoOptsCameraFlash = (Spinner) inflatedView.findViewById(R.id.videoOptsCameraFlashSpinner);
            AVRecordingService.videoOptsFocus = (Spinner) inflatedView.findViewById(R.id.videoOptsFocusSpinner);
            AVRecordingService.videoOptsScene = (Spinner) inflatedView.findViewById(R.id.videoOptsSceneSpinner);
            AVRecordingService.videoOptsWhiteBalance = (Spinner) inflatedView.findViewById(R.id.videoOptsWhiteBalanceSpinner);
            AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(AVRecordingService.localService != null) {
                        AVRecordingService.localService.updateVideoFeedParams();
                    }
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView) view).setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                }
                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            };
            AVRecordingService.videoOptsAntiband.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoOptsEffects.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoOptsCameraFlash.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoOptsFocus.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoOptsScene.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoOptsWhiteBalance.setOnItemSelectedListener(spinnerItemSelectedListener);

            spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView) view).setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                }
                public void onNothingSelected(AdapterView<?> adapterView) {}
            };
            AVRecordingService.audioPlaybackOptsEffectType = (Spinner) inflatedView.findViewById(R.id.audioOptsPlaybackEffectsSpinner);
            AVRecordingService.audioPlaybackOptsEffectType.setOnItemSelectedListener(spinnerItemSelectedListener);

        }
        else if(tabNumber == TAB_SETTINGS) {

            AVRecordingService.tvOutputFilePrefix = (TextView) inflatedView.findViewById(R.id.outputFilePrefixSetting);
            AVRecordingService.tvMaxFileSliceSize = (TextView) inflatedView.findViewById(R.id.maxFileSliceSizeSetting);
            FacebookLiveStreamingService.tvPostURL = (TextView) inflatedView.findViewById(R.id.fbLiveStreamingPostURL);
            FacebookLiveStreamingService.tvStreamKey = (TextView) inflatedView.findViewById(R.id.fbLiveStreamingStreamKey);
            YouTubeStreamingService.tvBroadcastTitle = (TextView) inflatedView.findViewById(R.id.liveStreamTitle);
            AVRecordingService.cbDNDWhileRecording = (CheckBox) inflatedView.findViewById(R.id.DNDWhileRecordingCheckBox);

            AVRecordingService.videoOptsRotation = (Spinner) inflatedView.findViewById(R.id.videoOptsRotationSpinner);
            AVRecordingService.videoOptsQuality = (Spinner) inflatedView.findViewById(R.id.videoOptsRecordingQuality);
            AVRecordingService.videoPlaybackOptsContentType = (Spinner) inflatedView.findViewById(R.id.videoOptsContentTypeDesc);
            AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(AVRecordingService.localService != null) {
                        AVRecordingService.localService.updateVideoFeedParams();
                    }
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView) view).setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                }
                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            };
            AVRecordingService.videoOptsRotation.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoOptsQuality.setOnItemSelectedListener(spinnerItemSelectedListener);
            AVRecordingService.videoPlaybackOptsContentType.setOnItemSelectedListener(spinnerItemSelectedListener);

            FacebookLiveStreamingService.streamingMediaTypeSpinner = (Spinner) inflatedView.findViewById(R.id.liveStreamingMediaTypeSpinner);
            AdapterView.OnItemSelectedListener smtypeSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i == 0) {
                        FacebookLiveStreamingService.LOCAL_AVSETTING = AVRecordingService.AVSETTING_AUDIO_VIDEO;
                    }
                    else {
                        FacebookLiveStreamingService.LOCAL_AVSETTING = AVRecordingService.AVSETTING_AUDIO_ONLY;
                    }
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                    ((TextView) view).setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
                }
                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            };
            FacebookLiveStreamingService.streamingMediaTypeSpinner.setOnItemSelectedListener(smtypeSpinnerItemSelectedListener);

            // setup the Facebook login button:
            MainActivity.fbLoginButton = (LoginButton) inflatedView.findViewById(R.id.settingsFBLoginButton);
            MainActivity.fbLoginCallback = CallbackManager.Factory.create();
            MainActivity.fbLoginButton.registerCallback(MainActivity.fbLoginCallback, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    FacebookLiveStreamingService.fbLoginResult = loginResult;
                }
                @Override
                public void onCancel() {}
                @Override
                public void onError(FacebookException e) {}
            });
            MainActivity.fbInitialized = true;

            MainActivity.cameraWhichSpinner = (Spinner) inflatedView.findViewById(R.id.liveStreamingWhichCameraSpinner);
            MainActivity.cameraWhichSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);
            MainActivity.streamingTypeSpinner = (Spinner) inflatedView.findViewById(R.id.liveStreamingMediaHighLevelProtocolSpinner);
            MainActivity.streamingTypeSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);
            YouTubeStreamingService.youtubePrivacySpinner = (Spinner) inflatedView.findViewById(R.id.youtubeStreamPrivacyLevelSpinner);
            YouTubeStreamingService.youtubePrivacySpinner.setOnItemSelectedListener(spinnerItemSelectedListener);
            YouTubeStreamingService.youtubeCDNSettingsSpinner = (Spinner) inflatedView.findViewById(R.id.youtubeStreamCDNQualitySpinner);
            YouTubeStreamingService.youtubeCDNSettingsSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);
            YouTubeStreamingService.youtubeCDNSettingsSpinner.setSelection(3);
            YouTubeStreamingService.youtubeIngestionTypeSpinner = (Spinner) inflatedView.findViewById(R.id.youtubeStreamIngestionProtocolSpinner);
            YouTubeStreamingService.youtubeIngestionTypeSpinner.setOnItemSelectedListener(spinnerItemSelectedListener);

            // restore configuration settings from previous runs of the application:
            MainActivity.runningActivity.restoreConfiguration();

        }
        else if(tabNumber == TAB_ABOUT) {

            String rawAboutStr = MainActivity.runningActivity.getString(R.string.apphtmlheader);
            rawAboutStr += MainActivity.runningActivity.getString(R.string.AboutHTML);
            rawAboutStr += getString(R.string.apphtmlfooter);
            rawAboutStr = rawAboutStr.replace("%%appVersionName%%", MainActivity.runningActivity.getString(R.string.appVersionName));
            rawAboutStr = rawAboutStr.replace("%%appVersionCode%%", MainActivity.runningActivity.getString(R.string.appVersionCode));
            rawAboutStr = rawAboutStr.replace("%%appBuildConfig%%", MainActivity.runningActivity.getString(R.string.appBuildConfig));
            rawAboutStr = rawAboutStr.replace("%%appBuildTimestamp%%", MainActivity.runningActivity.getString(R.string.appBuildTimestamp));
            rawAboutStr = rawAboutStr.replace("%%appBuildPackage%%", BuildConfig.APPLICATION_ID);
            rawAboutStr = rawAboutStr.replace("%%ABOUTBGCOLOR%%", Utils.getHexColorFromResource(R.color.colorPrimary));
            rawAboutStr = rawAboutStr.replace("%%ABOUTTEXTCOLOR%%", Utils.getHexColorFromResource(R.color.colorAccent));
            rawAboutStr = rawAboutStr.replace("%%ABOUTLINKCOLOR%%", Utils.getHexColorFromResource(R.color.colorAccentHighlight));

            WebView wv = (WebView) inflatedView.findViewById(R.id.webViewAbout);
            wv.loadDataWithBaseURL(null, rawAboutStr, "text/html", "UTF-8", "");
            wv.getSettings().setJavaScriptEnabled(false);
            wv.getSettings().setLoadWithOverviewMode(true);
            wv.getSettings().setUseWideViewPort(true);
            wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            wv.setInitialScale(10);

        }
        return inflatedView;

    }

    /**
     * Called when the tab view is destroyed.
     * (Nothing but the default behavior implemented here.)
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}


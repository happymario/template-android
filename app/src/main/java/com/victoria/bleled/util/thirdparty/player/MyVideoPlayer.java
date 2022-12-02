package com.victoria.bleled.util.thirdparty.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.victoria.bleled.R;
import com.victoria.bleled.databinding.ViewPlayerVideoBinding;


public class MyVideoPlayer extends RelativeLayout implements LifecycleOwner {

//    @BindingAdapter("app:url")
//    public static void setVideoUrl(MyVideoPlayer view, String url) {
//        view.setVideoUrl(url);
//    }

    private ViewPlayerVideoBinding binding;
    private final LifecycleRegistry registry = new LifecycleRegistry(this);

    private String videoUrl = null;

    private SimpleExoPlayer player = null;
    private YouTubePlayer youTubePlayer = null;
    private boolean isFullscreen = false;

    private Listener listener = null;

    public MyVideoPlayer(Context context) {
        super(context);
        initLayout(context, null);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context, attrs);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    private void initLayout(Context ctx, AttributeSet attrs) {
        binding = ViewPlayerVideoBinding.inflate(LayoutInflater.from(getContext()), null, false);
        addView(binding.getRoot());

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.MyVideoPlayer);
        String url = a.getString(R.styleable.MyVideoPlayer_url);
        a.recycle();

        binding.playerViewYoutube.setVisibility(View.GONE);
        binding.playerView.setVisibility(View.GONE);
        initPlayer();

        if (url != null && url.isEmpty() == false) {
            setVideoUrl(url);
        }
    }

    private void initPlayer() {
        initExoPlayer();
        initYoutubePlayer();
    }

    private void initExoPlayer() {
        Context context = getContext();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
        player = new SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build();
        binding.playerView.setPlayer(player);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
        //binding.playerView.setUseController(false);

        binding.playerView.findViewById(R.id.exo_fullscreen_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullscreen == false) {
                    if (listener != null) {
                        listener.onFullScreen(true);
                    }
                    setFullScreenMode(true);
                } else {
                    if (listener != null) {
                        listener.onFullScreen(false);
                    }
                    setFullScreenMode(false);
                }
            }
        });
    }

    private void initYoutubePlayer() {
        Context context = getContext();
        getLifecycle().addObserver(binding.playerViewYoutube);

        View customPlayerUi = binding.playerViewYoutube.inflateCustomPlayerUi(R.layout.view_player_youbube_custom_controls);
        customPlayerUi.findViewById(R.id.enter_exit_fullscreen_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullscreen == false) {
                    if (listener != null) {
                        listener.onFullScreen(true);
                    }
                    setFullScreenMode(true);
                } else {
                    if (listener != null) {
                        listener.onFullScreen(false);
                    }
                    setFullScreenMode(false);
                }
            }
        });

        binding.playerViewYoutube.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                CustomYoutubeUiController customPlayerUiController = new CustomYoutubeUiController(context, customPlayerUi, youTubePlayer, binding.playerViewYoutube);
                youTubePlayer.addListener(customPlayerUiController);
                binding.playerViewYoutube.addFullScreenListener(customPlayerUiController);

                MyVideoPlayer.this.youTubePlayer = youTubePlayer;

                if (videoUrl != null && isYoutubeUrl(videoUrl)) {
                    setYoutubeUrl(videoUrl);
                }
            }
        });
    }

    private boolean isYoutubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        if (!youTubeURl.isEmpty() && youTubeURl.matches(pattern)) {
            success = true;
        } else {
            // Not Valid youtube URL
            success = false;
        }
        return success;
    }

    private void setCommonUrl(String url) {
        if (player == null) {
            return;
        }

        Context context = getContext();
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        String userAgent = Util.getUserAgent(context, context.getApplicationInfo().name);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

        videoUrl = url;
        binding.playerView.setVisibility(View.VISIBLE);
        binding.playerViewYoutube.setVisibility(View.GONE);
    }

    private void setYoutubeUrl(String url) {
        if (youTubePlayer == null) {
            videoUrl = url;
            return;
        }

        Uri uri = Uri.parse(url);
        String videoId = uri.getQueryParameter("v");
        if (videoId == null) {
            videoId = uri.getLastPathSegment();
        }
        youTubePlayer.cueVideo(videoId, 0);
        youTubePlayer.play();

        videoUrl = url;
        binding.playerView.setVisibility(View.GONE);
        binding.playerViewYoutube.setVisibility(View.VISIBLE);
    }


    public void setVideoUrl(String url) {
        if (url == null || URLUtil.isValidUrl(url) == false) {
            return;
        }
        if (isYoutubeUrl(url)) {
            setYoutubeUrl(url);
        } else {
            setCommonUrl(url);
        }
    }

    public void play() {
        if (isYoutubeUrl(videoUrl)) {
            if (youTubePlayer == null) {
                return;
            }
            youTubePlayer.play();
        } else {
            if (player == null) {
                return;
            }
            player.play();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setFullScreenMode(boolean isFull) {
        isFullscreen = isFull;

        ImageView exoFullIcon = binding.playerView.findViewById(R.id.exo_fullscreen_icon);
        ImageView youtubeFullIcon = binding.playerViewYoutube.findViewById(R.id.fullscreen_icon);
        if (isFull) {
            if (exoFullIcon != null) {
                exoFullIcon.setImageResource(R.drawable.ic_fullscreen_close);
            }
            if (youtubeFullIcon != null) {
                youtubeFullIcon.setImageResource(R.drawable.ic_fullscreen_close);
            }
        } else {
            if (exoFullIcon != null) {
                exoFullIcon.setImageResource(R.drawable.ic_fullscreen_open);
            }
            if (youtubeFullIcon != null) {
                youtubeFullIcon.setImageResource(R.drawable.ic_fullscreen_open);
            }
        }
    }

    public void setPlayWhenReady(boolean ready) {
        if (player != null) {
            player.setPlayWhenReady(ready);
        }
    }

    public void release() {
        if (player != null) {
            player.release();
        }
    }

    public interface Listener {
        void onFullScreen(boolean isFullScreen);
    }
}

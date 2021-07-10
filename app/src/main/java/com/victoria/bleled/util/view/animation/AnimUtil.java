package com.victoria.bleled.util.view.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by HappyMario on 2/10/2018.
 */

public class AnimUtil {
    private static final String TAG = "AnimationUtils";
    private static final boolean DEBUG = false;

    public enum Type {
        ALPHA, SCALE_AND_ALPHA
    }

    public static void animateView(View view, boolean enterOrExit, long duration) {
        animateView(view, Type.ALPHA, enterOrExit, duration, 0, null);
    }

    public static void animateView(View view, boolean enterOrExit, long duration, long delay) {
        animateView(view, Type.ALPHA, enterOrExit, duration, delay, null);
    }

    public static void animateView(View view, boolean enterOrExit, long duration, long delay, Runnable execOnEnd) {
        animateView(view, Type.ALPHA, enterOrExit, duration, delay, execOnEnd);
    }

    public static void animateView(View view, Type animationType, boolean enterOrExit, long duration) {
        animateView(view, animationType, enterOrExit, duration, 0, null);
    }

    public static void animateView(View view, Type animationType, boolean enterOrExit, long duration, long delay) {
        animateView(view, animationType, enterOrExit, duration, delay, null);
    }

    /**
     * Animate the view
     *
     * @param view          view that will be animated
     * @param animationType {@link Type} of the animation
     * @param enterOrExit   true to enter, false to exit
     * @param duration      how long the animation will take, in milliseconds
     * @param delay         how long the animation will wait to start, in milliseconds
     * @param execOnEnd     runnable that will be executed when the animation ends
     */
    public static void animateView(final View view, Type animationType, boolean enterOrExit, long duration, long delay, Runnable execOnEnd) {
        if (DEBUG) {
            Log.d(TAG, "animateView() called with: view = [" + view + "], animationType = [" + animationType + "], enterOrExit = [" + enterOrExit + "], duration = [" + duration + "], delay = [" + delay + "], execOnEnd = [" + execOnEnd + "]");
        }

        if (view.getVisibility() == View.VISIBLE && enterOrExit) {
            if (DEBUG) Log.d(TAG, "animateView() view was already visible > view = [" + view + "]");
            view.animate().setListener(null).cancel();
            view.setVisibility(View.VISIBLE);
            view.setAlpha(1f);
            if (execOnEnd != null) execOnEnd.run();
            return;
        } else if ((view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) && !enterOrExit) {
            if (DEBUG) Log.d(TAG, "animateView() view was already gone > view = [" + view + "]");
            view.animate().setListener(null).cancel();
            view.setVisibility(View.GONE);
            view.setAlpha(0f);
            if (execOnEnd != null) execOnEnd.run();
            return;
        }

        view.animate().setListener(null).cancel();
        view.setVisibility(View.VISIBLE);

        switch (animationType) {
            case ALPHA:
                animateAlpha(view, enterOrExit, duration, delay, execOnEnd);
                break;
            case SCALE_AND_ALPHA:
                animateScaleAndAlpha(view, enterOrExit, duration, delay, execOnEnd);
                break;
        }
    }

    private static void animateScaleAndAlpha(final View view, boolean enterOrExit, long duration, long delay, final Runnable execOnEnd) {
        if (enterOrExit) {
            view.setAlpha(0f);
            view.setScaleX(.8f);
            view.setScaleY(.8f);
            view.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(duration).setStartDelay(delay).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (execOnEnd != null) execOnEnd.run();
                }
            }).start();
        } else {
            view.setAlpha(1f);
            view.setScaleX(1f);
            view.setScaleY(1f);
            view.animate().alpha(0f).scaleX(.8f).scaleY(.8f).setDuration(duration).setStartDelay(delay).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    if (execOnEnd != null) execOnEnd.run();
                }
            }).start();
        }
    }


    private static void animateAlpha(final View view, boolean enterOrExit, long duration, long delay, final Runnable execOnEnd) {
        if (enterOrExit) {
            view.animate().alpha(1f).setDuration(duration).setStartDelay(delay).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (execOnEnd != null) execOnEnd.run();
                }
            }).start();
        } else {
            view.animate().alpha(0f).setDuration(duration).setStartDelay(delay).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    if (execOnEnd != null) execOnEnd.run();
                }
            }).start();
        }
    }

    public static Animation loadAnimation(Context context, int id)
            throws Resources.NotFoundException {

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            return createAnimationFromXml(context, parser);
        } catch (XmlPullParserException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } finally {
            if (parser != null) parser.close();
        }
    }

    private static Animation createAnimationFromXml(Context c, XmlPullParser parser)
            throws XmlPullParserException, IOException {

        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
    }

    private static Animation createAnimationFromXml(Context c, XmlPullParser parser,
                                                    AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {

        Animation anim = null;

        // Make sure we are on a start tag.
        int type;
        int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
                && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("set")) {
                anim = new AnimationSet(c, attrs);
                createAnimationFromXml(c, parser, (AnimationSet) anim, attrs);
            } else if (name.equals("alpha")) {
                anim = new AlphaAnimation(c, attrs);
            } else if (name.equals("scale")) {
                anim = new ScaleAnimation(c, attrs);
            } else if (name.equals("rotate")) {
                anim = new RotateAnimation(c, attrs);
            } else if (name.equals("translate")) {
                anim = new TranslateAnimation(c, attrs);
            } else {
                try {
                    anim = (Animation) Class.forName(name).getConstructor(Context.class, AttributeSet.class).newInstance(c, attrs);
                } catch (Exception te) {
                    throw new RuntimeException("Unknown animation name: " + parser.getName() + " error:" + te.getMessage());
                }
            }

            if (parent != null) {
                parent.addAnimation(anim);
            }
        }

        return anim;

    }
}

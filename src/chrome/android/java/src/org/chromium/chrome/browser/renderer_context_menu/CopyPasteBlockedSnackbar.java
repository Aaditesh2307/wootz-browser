package org.chromium.chrome.browser.renderer_context_menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.jni_zero.CalledByNative;
import org.jni_zero.JNINamespace;
import org.chromium.ui.base.WindowAndroid;

/**
 * Native Android Snackbar implementation for copy-paste blocking.
 * Matches the download blocking styling with warning icon, title, message, and orange progress strip.
 */
@JNINamespace("chrome")
public class CopyPasteBlockedSnackbar {
    private static final int SNACKBAR_DURATION_MS = 4000; // 4 seconds
    private static final int SNACKBAR_WIDTH_DP = 400;
    private static final int SNACKBAR_CORNER_RADIUS_DP = 8;
    private static final int SNACKBAR_ELEVATION_DP = 6;
    private static final int CONTENT_PADDING_HORIZONTAL_DP = 24;
    private static final int CONTENT_PADDING_VERTICAL_DP = 20;
    private static final int ICON_CIRCLE_SIZE_DP = 36;
    private static final int ICON_CIRCLE_MARGIN_END_DP = 16;
    private static final int TITLE_TEXT_SIZE_SP = 18;
    private static final int TITLE_PADDING_BOTTOM_DP = 6;
    private static final int MESSAGE_TEXT_SIZE_SP = 15;
    private static final int PROGRESS_STRIP_HEIGHT_DP = 3;
    private static final int CONTAINER_MARGIN_HORIZONTAL_DP = 16;
    private static final int CONTAINER_MARGIN_BOTTOM_DP = 40;
    private static final String TAG = "CopyPasteBlockedSnackbar";

    /**
     * Shows a custom Snackbar with the copy-paste blocked message matching download blocking styling.
     * 
     * @param windowAndroid The WindowAndroid instance
     * @param message The message to display
     */
    @CalledByNative
    public static void show(@NonNull WindowAndroid windowAndroid, @NonNull String message) {
        Context context = windowAndroid.getContext().get();
        if (context == null) return;

        View rootView = findRootView(context);
        if (rootView == null) {
            showFallbackToast(context, message);
            return;
        }

        View snackbarView = createCustomSnackbarView(context, message);
        if (rootView instanceof ViewGroup) {
            ViewGroup rootGroup = (ViewGroup) rootView;
            rootGroup.addView(snackbarView);
            // Remove snackbar after duration
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                rootGroup.removeView(snackbarView);
            }, SNACKBAR_DURATION_MS);
        }
    }

    /**
     * Creates a custom snackbar view matching the download blocking styling.
     */
    private static View createCustomSnackbarView(Context context, String message) {
        float density = context.getResources().getDisplayMetrics().density;

        // Main snackbar container
        LinearLayout snackbarContainer = new LinearLayout(context);
        snackbarContainer.setOrientation(LinearLayout.VERTICAL);

        // White background with rounded corners
        android.graphics.drawable.GradientDrawable background = new android.graphics.drawable.GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(SNACKBAR_CORNER_RADIUS_DP * density);
        snackbarContainer.setBackground(background);
        snackbarContainer.setElevation(SNACKBAR_ELEVATION_DP * density);

        // Content container (icon + text)
        LinearLayout contentContainer = new LinearLayout(context);
        contentContainer.setOrientation(LinearLayout.HORIZONTAL);
        contentContainer.setPadding(
                (int) (CONTENT_PADDING_HORIZONTAL_DP * density),
                (int) (CONTENT_PADDING_VERTICAL_DP * density),
                (int) (CONTENT_PADDING_HORIZONTAL_DP * density),
                (int) (CONTENT_PADDING_VERTICAL_DP * density));
        contentContainer.setGravity(Gravity.CENTER_VERTICAL);

        // Icon container with orange circle
        LinearLayout iconContainer = new LinearLayout(context);
        iconContainer.setOrientation(LinearLayout.VERTICAL);
        iconContainer.setGravity(Gravity.CENTER);
        android.graphics.drawable.GradientDrawable circleBackground = new android.graphics.drawable.GradientDrawable();
        circleBackground.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        circleBackground.setColor(0xFFE67E22); // Orange
        iconContainer.setBackground(circleBackground);
        int circleSize = (int) (ICON_CIRCLE_SIZE_DP * density);
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(circleSize, circleSize);
        circleParams.setMargins(0, 0, (int) (ICON_CIRCLE_MARGIN_END_DP * density), 0);
        iconContainer.setLayoutParams(circleParams);
        TextView warningIcon = new TextView(context);
        warningIcon.setText("⚠");
        warningIcon.setTextSize(TITLE_TEXT_SIZE_SP);
        warningIcon.setTextColor(Color.WHITE);
        warningIcon.setGravity(Gravity.CENTER);
        warningIcon.setTypeface(null, Typeface.BOLD);
        iconContainer.addView(warningIcon);
        contentContainer.addView(iconContainer);

        // Message container (title + message)
        LinearLayout messageContainer = new LinearLayout(context);
        messageContainer.setOrientation(LinearLayout.VERTICAL);
        TextView titleText = new TextView(context);
        titleText.setText("Copy-Paste Blocked");
        titleText.setTextSize(TITLE_TEXT_SIZE_SP);
        titleText.setTextColor(0xFF1A1A1A);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setPadding(0, 0, 0, (int) (TITLE_PADDING_BOTTOM_DP * density));
        TextView messageText = new TextView(context);
        messageText.setText(message);
        messageText.setTextSize(MESSAGE_TEXT_SIZE_SP);
        messageText.setTextColor(0xFF666666);
        messageText.setLineSpacing(0, 1.3f);
        messageContainer.addView(titleText);
        messageContainer.addView(messageText);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        messageContainer.setLayoutParams(messageParams);
        contentContainer.addView(messageContainer);

        snackbarContainer.addView(contentContainer);

        // Orange progress strip
        View progressStrip = new View(context);
        android.graphics.drawable.GradientDrawable progressBackground = new android.graphics.drawable.GradientDrawable();
        progressBackground.setColor(0xFFE67E22);
        progressBackground.setCornerRadii(new float[]{0, 0, 0, 0, SNACKBAR_CORNER_RADIUS_DP * density, SNACKBAR_CORNER_RADIUS_DP * density, SNACKBAR_CORNER_RADIUS_DP * density, SNACKBAR_CORNER_RADIUS_DP * density});
        progressStrip.setBackground(progressBackground);
        LinearLayout.LayoutParams stripParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (PROGRESS_STRIP_HEIGHT_DP * density)
        );
        progressStrip.setLayoutParams(stripParams);
        snackbarContainer.addView(progressStrip);

        // Set snackbar container dimensions and position
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                (int) (SNACKBAR_WIDTH_DP * density),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        containerParams.setMargins(
                (int) (CONTAINER_MARGIN_HORIZONTAL_DP * density),
                0,
                (int) (CONTAINER_MARGIN_HORIZONTAL_DP * density),
                (int) (CONTAINER_MARGIN_BOTTOM_DP * density));
        snackbarContainer.setLayoutParams(containerParams);

        // Animate the progress strip shrinking from right to left
        android.animation.ValueAnimator progressAnimator = android.animation.ValueAnimator.ofFloat(1.0f, 0.0f);
        progressAnimator.setDuration(SNACKBAR_DURATION_MS);
        progressAnimator.setInterpolator(new android.view.animation.LinearInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            float progress = (Float) animation.getAnimatedValue();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressStrip.getLayoutParams();
            int containerWidth = snackbarContainer.getWidth();
            if (containerWidth > 0) {
                params.width = (int) (containerWidth * progress);
                progressStrip.setLayoutParams(params);
            }
        });
        snackbarContainer.post(progressAnimator::start);
        return snackbarContainer;
    }

    /**
     * Finds the root view for the Snackbar.
     */
    private static View findRootView(Context context) {
        // Try to find the main activity's root view
        if (context instanceof android.app.Activity) {
            android.app.Activity activity = (android.app.Activity) context;
            View rootView = activity.findViewById(android.R.id.content);
            if (rootView != null) {
                // Look for CoordinatorLayout or suitable parent
                ViewGroup parent = (ViewGroup) rootView;
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (child instanceof CoordinatorLayout) {
                        return child;
                    }
                }
                return rootView;
            }
        }
        return null;
    }

    /**
     * Fallback to Toast if Snackbar cannot be shown.
     */
    private static void showFallbackToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        });
    }
} 
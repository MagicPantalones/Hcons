package io.magicpants.hkonsapp.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.magicpants.hkonsapp.MainActivity;
import io.magicpants.hkonsapp.R;

/**
 * Created by Erik on 10.01.2018.
 */

public class NotificationUtils {

    private static final int NEW_MESSAGE_PENDING_INTENT_ID = 1000;



    private static PendingIntent contentIntent(Context context){
        return PendingIntent.getActivity(context, NEW_MESSAGE_PENDING_INTENT_ID,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Bitmap notificationAppIcon(Context context){
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_h_app_icon);
    }
}

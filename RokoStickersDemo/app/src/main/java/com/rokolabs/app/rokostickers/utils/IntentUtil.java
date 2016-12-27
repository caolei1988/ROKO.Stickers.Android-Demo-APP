package com.rokolabs.app.rokostickers.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IntentUtil
{
    private static final String PKG_GOOGLE_PLUS = "com.google.android.apps.plus";
    private static final String PKG_INSTAGRAM = "com.instagram.android";
    private static final String PKG_GMAIL     = "com.google.android.gm";
    private static final String PKG_K9MAIL    = "com.fsck.k9";
    private static final String PKG_TOUCHDOWN = "com.nitrodesk";
    private static final String PKG_FACEBOOK  = "com.facebook.katana";
    private static final String PKG_TWITTER   = "com.twitter.android";
    private static final String PKG_MMS       = "com.android.mms";
    private static final String PKG_HANGOUT   = "com.google.android.talk";
    
    public static void shareViaInstagram(Context context, String title, Intent baseIntent)
    {
        shareVia(context, new AppMatcher()
        {

            @Override
            public boolean match(ActivityInfo appInfo)
            {
                // TODO Auto-generated method stub
                return PKG_INSTAGRAM.equalsIgnoreCase(appInfo.packageName);
            }

        }, "image/jpeg", title, baseIntent, false);
    }
    
    public static void shareViaFaceBook(Context context, String shareBody, Intent baseIntent, String mime) {
        shareVia(context, new AppMatcher()
        {

            @Override
            public boolean match(ActivityInfo appInfo)
            {
                // TODO Auto-generated method stub
                return PKG_FACEBOOK.equalsIgnoreCase(appInfo.packageName);
            }

        }, mime, shareBody, baseIntent, false);
    }
    
    public static void shareViaTwitter(Context context, String shareBody, Intent baseIntent) {
        shareVia(context, new AppMatcher()
        {

            @Override
            public boolean match(ActivityInfo appInfo)
            {
                // TODO Auto-generated method stub
                return PKG_TWITTER.equalsIgnoreCase(appInfo.packageName);
            }

        }, "text/plain", shareBody, baseIntent, false);
    }

    public static void shareViaEmail(Context context, String title, Intent baseIntent)
    {
        shareVia(context, new AppMatcher()
        {

            @Override
            public boolean match(ActivityInfo appInfo)
            {
                if (PKG_GMAIL.equalsIgnoreCase(appInfo.packageName) || PKG_K9MAIL.equalsIgnoreCase(appInfo.packageName) || appInfo.packageName.toLowerCase().startsWith(PKG_TOUCHDOWN) || appInfo.packageName.toLowerCase().contains("mail") || appInfo.name.toLowerCase().contains("mail"))
                    return true;
                return false;
            }

        }, "message/rfc822", title, baseIntent, true);
    }

    public static void shareViaSms(Context context, String title, Intent baseIntent)
    {
        Intent htc = new Intent(baseIntent);
        htc.setAction("android.intent.action.SEND_MSG");
        htc.setType("image/jpeg");
        
        List<Intent> iList = getIntentList(context, htc);
        
        if(iList.size()>0){
            Intent chooserIntent = Intent.createChooser(iList.remove(iList.size()-1), title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, iList.toArray(new Parcelable[] {}));
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooserIntent);
            return;
        }
        
        htc.setAction("com.htc.intent.action.SEND_MSG");
        iList = getIntentList(context, htc);

        if (iList.size() > 0)
        {
            Intent chooserIntent = Intent.createChooser(iList.remove(iList.size() - 1), title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, iList.toArray(new Parcelable[] {}));
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooserIntent);
            return;
        }
        
        shareVia(context, new AppMatcher()
        {

            @Override
            public boolean match(ActivityInfo appInfo)
            {
                if (PKG_MMS.equalsIgnoreCase(appInfo.packageName) || PKG_HANGOUT.equalsIgnoreCase(appInfo.packageName) || appInfo.packageName.toLowerCase().contains("sms") || appInfo.packageName.toLowerCase().contains("mms"))
                    return true;
                return false;
            }

        }, "image/jpeg", title, baseIntent, false);
    }
    
    public static boolean hasInstagram(Context context)
    {
        return hasPackage(context, PKG_INSTAGRAM);
    }

    public static boolean hasPackage(Context context, String name)
    {
        try
        {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(name, 0);
            if (ai != null)
                return true;
        } catch (Exception e)
        {

        }
        return false;
    }
    
    public static final List<String> APP_SHARE_ORDER = Arrays.asList(new String[] { PKG_FACEBOOK, PKG_TWITTER, PKG_GOOGLE_PLUS, PKG_GMAIL, PKG_MMS});
    
    public static void shareApp(Context context){
        Intent baseIntent = new Intent();
        String mime = "text/plain";
        String title = "Share Snaps!";
        String shareBody = "Snaps! is my favorite app. Download now to follow me:http://get.snapsapp.com";
        baseIntent.putExtra(Intent.EXTRA_SUBJECT, "Tell your friends about snaps!");
        baseIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        
        shareVia(context, new AppMatcher(){

            @Override
            public boolean match(ActivityInfo appInfo)
            {
                
                return APP_SHARE_ORDER.contains(appInfo.packageName);
            }
            
        }, new Comparator<Intent>(){

            @Override
            public int compare(Intent lhs, Intent rhs)
            {
                int lv = -1;
                int rv = -1;
                String lp = lhs.getPackage();
                String rp = rhs.getPackage();
                if(lp != null)
                    lv = APP_SHARE_ORDER.indexOf(lp);
                if(rp != null)
                    rv = APP_SHARE_ORDER.indexOf(rp);
                if(lv == -1)
                    lv = Integer.MAX_VALUE;
                if(rv == -1)
                    rv = Integer.MAX_VALUE;
                
                return lv - rv;
            }
            
        },mime,title,baseIntent, true);
    }

    public static List<Intent> getIntentList(Context context, Intent baseIntent)
    {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = new Intent(baseIntent);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty())
        {
            for (ResolveInfo info : resInfo)
            {
                Intent targetedShare = new Intent(baseIntent);
                targetedShare.setPackage(info.activityInfo.packageName);
                targetedShareIntents.add(targetedShare);

            }

        } else
        {
            Constants.d("No app found to handle: " + baseIntent.getAction());
        }
        return targetedShareIntents;

    }
    
    public static void shareVia(Context context, AppMatcher matcher, String mime, String title, Intent baseIntent, boolean fallbackToRawIntent){
        shareVia(context, matcher, null, mime, title, baseIntent, fallbackToRawIntent);
    }
    
    public static void shareVia(Context context, AppMatcher matcher, Comparator<Intent> sortComparator, String mime, String title, Intent baseIntent, boolean fallbackToRawIntent)
    {
    	Constants.d("Checking app match mime: " + mime);
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent share = new Intent(baseIntent);
        share.setAction(Intent.ACTION_SEND);
        share.setType(mime);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty())
        {
            for (ResolveInfo info : resInfo)
            {
                Intent targetedShare = new Intent(baseIntent);
                targetedShare.setAction(Intent.ACTION_SEND);
                targetedShare.setType(mime);
                if (matcher.match(info.activityInfo))
                {
                	Constants.d("Found matching app: " + info.activityInfo.packageName);
                    //bundle.
                    targetedShare.setPackage(info.activityInfo.packageName);
                    targetedShare.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    targetedShareIntents.add(targetedShare);
                } else {
                	Constants.d("Match failed: " + info.activityInfo.packageName);
                }
            }

            if (targetedShareIntents.size() > 0)
            {
                if(sortComparator != null)
                    Collections.sort(targetedShareIntents, sortComparator);
                for(Intent it : targetedShareIntents){
                	Constants.d(it.getPackage());
                }
                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size()-1), title);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
                //chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(chooserIntent);
            } else if (fallbackToRawIntent)
            {
            	Constants.d("No mail app found fall back to default intent");
                context.startActivity(Intent.createChooser(share, title));
            } else if(targetedShareIntents.size() == 0) {
                Toast.makeText(context, "No app found to handle this message!", Toast.LENGTH_LONG).show();
            }
        } else
        {
        	Constants.d("No app found to handle: " + mime);
        }

    }

    public interface AppMatcher
    {
        public boolean match(ActivityInfo appInfo);
    }
    
}

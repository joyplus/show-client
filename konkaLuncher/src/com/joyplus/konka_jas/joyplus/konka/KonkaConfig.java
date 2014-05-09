package com.joyplus.konka_jas.joyplus.konka;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.joyplus.konka.luncher.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class KonkaConfig {

    private static final String  TAG   = "KonkaConfig";
    private static final boolean DEBUG = true;

    private static boolean DEBUGMODE        = false;

    private static String  CacheDir         = "";
    private static String  DebugCacheDir    = "";
    private static String  ResourceDir      = "";
    private static String  DebugResourceDir = "";
    private static int     RequestFrq       = 30*1000;
    private static AD[]    mAD              = null;

    public  static boolean GetDebugMode(){
        return DEBUGMODE;
    }
    public  static AD[]    GetADList(){return mAD;}



    public  static String GetCacheDir(){
        if(DEBUGMODE)return DebugCacheDir;
        return CacheDir;
    }
    public static String GetResourceDir(){
        if(DEBUGMODE)return DebugResourceDir;
        return ResourceDir;
    }

    public static int GetRequstFrq(){
        return RequestFrq;
    }

    public static void Init(Context context) {
        loadKonkaSettings(context); 
    } 

    private static void loadKonkaSettings(Context context) {
        Resources mResources = context.getResources();
        DEBUGMODE        = mResources.getBoolean(R.bool.DEBUG);
        CacheDir         = mResources.getString(R.string.CacheDir);
        DebugCacheDir    = mResources.getString(R.string.DebugCacheDir);
        ResourceDir      = mResources.getString(R.string.ResourceDir);
        DebugResourceDir = mResources.getString(R.string.DebugResourceDir);
        RequestFrq       = Integer.parseInt(mResources.getString(R.string.requestFrq));
        String[] AD = context.getResources().getStringArray(R.array.ADMessage);
        if(AD !=null && AD.length>0) {
            mAD = new AD[AD.length];
            for (String config : AD){
                AD ad = new AD(config);
                if(!ad.EN || mAD[ad.Priority] != null)continue;
                mAD[ad.Priority] = ad;
            }
        }
    }

    public static String ToString(){
        StringBuffer ap = new StringBuffer();
        ap.append("KonkaConfig{")
          .append("DEBUGMODE="+GetDebugMode())
          .append(" ,CacheDir="+GetCacheDir())
          .append(" ,ResourceDir="+GetResourceDir())
          .append(" ,Frq="+GetRequstFrq())
          .append(" ,ADList="+AdListString())
          .append("}");
        return ap.toString();
    }
    public static String AdListString(){
        AD[] ads = GetADList();
        if(ads==null)return"null";
        StringBuffer ap = new StringBuffer();
        ap.append("{");
        for(AD ad : ads){
            if(ad == null)continue;
            ap.append(ad.toString());
        }
        ap.append("}");
        return ap.toString();
    }

}

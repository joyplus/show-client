package com.joyplus.konka_jas.joyplus.konka;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.joyplus.adkey.Ad;
import com.joyplus.adkey.BannerAd;
import com.joyplus.adkey.downloads.AdFileManager;
import com.joyplus.adkey.downloads.DownLoadManager;
import com.joyplus.adkey.downloads.Download;
import com.joyplus.adkey.downloads.FileUtils;
import com.joyplus.adkey.request.Report;
import com.joyplus.adkey.request.Request;
import com.joyplus.adkey.video.RichMediaAd;
import com.joyplus.adkey.widget.SerializeManager;
import com.joyplus.konka.jas.Konka;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ADRequest {
    public  final static String RESPONCENAME = "AD";
	private Context mContext;
	private static ADRequest mADRequest;
	public  static void Init(Context context){
		mADRequest = new ADRequest(context);
	}
	public  static ADRequest GetInstance(){
		return mADRequest;
	}
	public ADRequest(Context context){
		mContext = context;
		//mRequest = new Request(mContext,KonkaConfig.GetPublisherId());
	}

	private Thread  mRequestThread = null;
//	private Request mRequest       = null;
	public void request(){
		Log.d("Jas","request ");
		if(mRequestThread != null)return;
		Konka p = Konka.GetInstance();
		if(p!=null){//makesure it was run.
			p.SetClock();
		}
		mRequestThread  = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				GetAD();
				mRequestThread = null;
		}
		 });
		mRequestThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(){
				@Override
				public void uncaughtException(final Thread thread,
						final Throwable ex){
					mRequestThread = null;
				}
		 });
		mRequestThread.start();
	}



	private void GetAD() {
		Log.d("Jas","GetAD start ");
		// TODO Auto-generated method stub
		if(AdFileManager.getInstance().GetBasePath() == null){
		     //make sure set cache dir. it only for download cache, it use for "Download mode" so mast set it.
             AdFileManager.getInstance().SetBasePath(KonkaConfig.GetCacheDir());
		}
        if(KonkaConfig.GetADList() !=null){
            for(AD ad : KonkaConfig.GetADList()){
                GetAD(ad);
            }
        }
		Log.d("Jas","GetAD finish ");
	}
    public void GetAD(AD ad){
    	Log.d("Jas","GetAD start step2 AD = " + ((ad == null)?"":ad.toString()));
        if(ad == null || !ad.EN)return;
        if(!ad.USEBanner){
            BannerAd Bad = (new Request(mContext,ad.PublishID)).getBannerAd(ad.ReportSDK);
            //Log.d("Jas", "Bad-->" + (Bad == null ? "null" : Bad.toString()));
            if (ad == null || (Bad.GetCreative_res_url() == null || "".equals(Bad.GetCreative_res_url()))){
            	RemoveFile(ad);
                return;
            }
            AddDown(ad,Bad);
        }else{
            RichMediaAd Rad = (new Request(mContext,ad.PublishID)).getRichMediaAd(ad.ReportSDK);
            //Log.d("Jas", "Rad-->" + (Rad == null ? "null" : Rad.toString()));
            if(ad == null || (Rad.GetCreative_res_url() == null || "".equals(Rad.GetCreative_res_url()))){
            	RemoveFile(ad);
                return;
            }
            AddDown(ad,Rad);
        }
    }
    
    private void RemoveFile(AD ad){
    	Log.d("Jas","RemoveFile-->"+ad.PublishID);
    	if(ad==null || "".equals(ad.PublishID))return;
    	Ad responce = getResponce(ad);
    	if(responce == null)return;
    	Log.d("Jas","RemoveFile-->"+responce.toString());
    	File resource = null;
    	if(responce instanceof BannerAd){
    		resource = new File(KonkaConfig.GetResourceDir()+ad.PublishID+File.separator+GetName(((BannerAd) responce).GetCreative_res_url(),true));
    	}else if(responce instanceof RichMediaAd){
    		resource = new File(KonkaConfig.GetResourceDir()+ad.PublishID+File.separator+GetName(((RichMediaAd) responce).GetCreative_res_url(),true));
    	}
    	if(resource !=null && resource.exists())resource.delete();
    	writeResponce(ad,null);//for remove response.
    }
    /*Get file name
    * Param : file     -- file url
    *       : exename  -- return which file exename*/
	private String GetName(String file,boolean exename){
		if(file != null && file.length()>0){
			int index = file.lastIndexOf("/");
			if(index>-1 && index<(file.length()-1)){
                String Name = file.substring(index+1);
                if(!exename && Name !=null && Name.length()>0){
                    int INDEX = Name.lastIndexOf(".");
                    if(INDEX>-1 && INDEX<(file.length()-1)){
                        Name = Name.substring(0,INDEX-1);
                    }
                }
                return Name;
			}
		}
		return file;
	}

    private synchronized void AddDown(AD mad,Ad ad){
        if(mad ==null || !mad.EN)return;
        String adURL = "";
        if(ad instanceof BannerAd){
            adURL = ((BannerAd) ad).GetCreative_res_url();
        }else if(ad instanceof  RichMediaAd){
            adURL = ((RichMediaAd)ad).GetCreative_res_url();
        }else return;
        new File(KonkaConfig.GetResourceDir()+mad.PublishID+File.separator).mkdirs();
        Download m   = new Download();
        m.URL = adURL;
        m.LocalFile  = KonkaConfig.GetCacheDir();
        m.TargetFile = KonkaConfig.GetResourceDir()+mad.PublishID+File.separator+GetName(adURL,true);
		if(CheckLocation(mad,m))DownLoadManager.getInstance().AddDownload(m);
        //if(!mad.ReportSDK){//save response in cache.//save it all.
            //客户想自己上报数据，所以需要保存返回的广告请求结果，以便上报
		    writeResponce(mad,ad);
        //}
    }


	private boolean CheckLocation(AD ad,Download down){
        if(ad == null || !ad.EN || !ad.LocationSave || ad.LocationSize<=0)return false;
		String Location   = KonkaConfig.GetResourceDir()+ad.PublishID;
		File LocationFile = new File(Location);
		if(LocationFile.exists()){
			File[] mFile = LocationFile.listFiles();
			for(File m : mFile){
				if(m.isDirectory()){
					m.delete();
					continue;
				}
				if(down.TargetFile.toString().equals(m.toString())){
					Log.d("Jas","Location file exist !!!");
                    //本地存在相同的素材，所以不需要再次下载广告素材
					return false;
				}
			}
			mFile = LocationFile.listFiles();
			if(mFile != null && mFile.length>=ad.LocationSize){
                //del pic file
                int Index = (new Random()).nextInt(ad.LocationSize);
				mFile[Index%ad.LocationSize].delete();
                if(!ad.ReportSDK) {//del response when user report by self.
                    //同时删除服务器返回并保存下来了的广告请求结果
                    File response = new File(KonkaConfig.GetCacheDir() + GetName(mFile[Index].toString(), false));
                    if (response.exists()) response.delete();
                }
			}
		}
		return true;
	}


    //add by Jas for report data by self.
    //Param : filename -- pic name.
    //如果客户想要自己上报数据，需要使用这个函数，该函数读取保存的广告请求结果并上报。
    public void Report(String filename){
//        if(KonkaConfig.GetREPORTBYSDK())return;
        if(filename==null || "".equals(filename))return;
        SerializeManager mSerializeManager = new SerializeManager();
        BannerAd ad = (BannerAd) mSerializeManager.readSerializableData(KonkaConfig.GetCacheDir()+GetName(filename,false));
        if(ad!=null)new Report(mContext).report(ad);
    }

    public static List<Drawable> getPicturesDrawble(int index){
    	AD[] ads = KonkaConfig.GetADList();
    	if(ads != null && index>=0 && index<ads.length){
    		return getPicturesDrawble(ads[index]);
    	}
    	return  new ArrayList<Drawable>();
    }
    
    private static List<Drawable> getPicturesDrawble(AD ad){
    	List<Drawable> drawables =  new ArrayList<Drawable>();
    	if(ad==null || !ad.EN){
    		return drawables;
    	}
		try {
			File dir_debug = new File(KonkaConfig.GetResourceDir()+"/"+ad.PublishID);
			if(!dir_debug.exists()){
				dir_debug.mkdirs();
			}
			if(dir_debug.exists()){
				File[] pictures = dir_debug.listFiles();
				if(pictures!=null && pictures.length>0){
					for(File f : pictures){
						Drawable d = Drawable.createFromPath(f.getAbsolutePath());
						if(d!=null){
							drawables.add(d);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			drawables =  new ArrayList<Drawable>();
		}
		return drawables;
	}
	
    
    
    public static Ad getResponce(int index){
    	AD[] ads = KonkaConfig.GetADList();
    	if(ads != null && index>=0 && index<ads.length){
    		return getResponce(ads[index]);
    	}
    	return  null;
    }
    public static Ad getResponce(AD ad){
    	if(ad == null || !ad.EN)return null;
    	SerializeManager mSerializeManager = new SerializeManager();
    	return (Ad) mSerializeManager.readSerializableData(KonkaConfig.GetCacheDir()+File.separator+ad.PublishID+File.separator+RESPONCENAME);
    }
    
    private void writeResponce(AD ad,Object o){
    	Log.d("Jas","writeResponce-->"+ad.PublishID);
    	if(ad ==null || !ad.EN)return;
    	(new File(KonkaConfig.GetCacheDir()+File.separator+ad.PublishID+File.separator)).mkdirs();
    	writeResponce(KonkaConfig.GetCacheDir()+File.separator+ad.PublishID+File.separator+RESPONCENAME,o);
    }
    
    private void writeResponce(String path,Object o){
    	SerializeManager mSerializeManager = new SerializeManager();
    	mSerializeManager.writeSerializableData(path, o);
    }
}

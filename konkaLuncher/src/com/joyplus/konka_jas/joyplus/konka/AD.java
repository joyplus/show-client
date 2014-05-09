package com.joyplus.konka_jas.joyplus.konka;

/**
 * Created by JasWorkSpace on 14-5-8.
 */
public class AD {
    /*!-- 使能（true使用），ID,请求条形横幅（false）或者多媒体（true）, SDK请求时上报数据（true）,优先级别,
    是否保存本地（true是）,本地同时保存的张数-->*/
    public boolean  EN = false;

    public String   PublishID;

    public boolean  USEBanner;

    public boolean  ReportSDK;

    public int      Priority;

    public boolean  LocationSave = false;

    public int      LocationSize = 0;

    public AD(String ad){
        String AD[]  = ad.split(",");
        EN           = Boolean.parseBoolean(AD[0]);
        PublishID    = AD[1].trim();
        USEBanner    = Boolean.parseBoolean(AD[2]);
        ReportSDK    = Boolean.parseBoolean(AD[3]);
        Priority     = Integer.parseInt(AD[4]);
        LocationSave = Boolean.parseBoolean(AD[5]);
        LocationSize = Integer.parseInt(AD[6]);
    }
    public String toString(){
        StringBuffer ap = new StringBuffer();
        ap.append("AD{")
          .append("EN="+EN)
          .append(" ,PublishID="+PublishID)
          .append(" ,USEBanner="+USEBanner)
          .append(" ,ReportSDK="+ReportSDK)
          .append(" ,Priority="+Priority)
          .append(" ,LocationSave="+LocationSave)
          .append(" ,LocationSize="+LocationSize)
          .append("}");
        return ap.toString();
    }
}

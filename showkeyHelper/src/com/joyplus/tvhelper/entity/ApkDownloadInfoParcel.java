package com.joyplus.tvhelper.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class ApkDownloadInfoParcel implements Parcelable {
	
	private String md5;
	private String app_name;//名称
	private String package_name;//包名
	private String icon_url;//图片地址
	private String version;//版本号
	private String apk_url;//APK资源下载地址
	



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeString(md5);
		dest.writeString(app_name);
		dest.writeString(package_name);
		dest.writeString(icon_url);
		dest.writeString(version);
		dest.writeString(apk_url);
	}

	public static final Parcelable.Creator<ApkDownloadInfoParcel> CREATOR = new Parcelable.Creator<ApkDownloadInfoParcel>() {

		@Override
		public ApkDownloadInfoParcel createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			ApkDownloadInfoParcel info = new ApkDownloadInfoParcel();
			info.md5 = source.readString();
			info.app_name = source.readString();
			info.icon_url = source.readString();
			info.version = source.readString();
			info.apk_url = source.readString();
			return info;
		}

		@Override
		public ApkDownloadInfoParcel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ApkDownloadInfoParcel[size];
		}
	};
	
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApk_url() {
		return apk_url;
	}

	public void setApk_url(String apk_url) {
		this.apk_url = apk_url;
	}

	public static Parcelable.Creator<ApkDownloadInfoParcel> getCreator() {
		return CREATOR;
	}

}

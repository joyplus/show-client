package com.joyplus.entity.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * 1������ǵ��Ӿ�

 {
 tv: {
 name: [STRING], // ��Ŀ�����֣�255�ֽ����ڵ��ַ���
 summary: [STRING], // ��Ŀ�ļ�飬255�ֽ����ڵ��ַ���
 poster: [URL], // ��Ŀ�ĺ���ͼƬ��ַ���ַ�����ʽ����httpЭ���ַ��ʽ
 closed: [BOOL], // ��ʾ��Ŀ�Ƿ��Ѿ���ᣬtrue����false
 episodes_count: [NUM], // ��Ŀ���ܼ�����������
 sources: [STRING] // ��Ŀ�Ŀ�����Դ��վ���Զ��ŷָ���ַ��������磺�ſ�,����,...
 like_num: int //ϲ�������Ŀ���û���
 watch_num: int //�ۿ��������Ŀ���û���
 favority_num: int �ղ������Ŀ���û���,
 score:  float ���������
 episodes: [
 {
 name: [STRING],
 video_urls: [
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,����
 url: [URL]
 },
 ...
 ]
 down_urls: [  //��Ƶ��ַ
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� 
 urls: [
 {
 "type": "mp4", mp4:���壬flv�����壬hd2������
 "url": [URL]
 }
 ]
 }
 .........
 ]
 },
 ...
 ]
 },
 comments: [
 {
 owner_id: int �������۵��û�id
 owner_name: string ���������û���
 owner_pic_url: string ���������û���ͷ��
 id: int ����id
 content: string ���۵�����
 create_date: date ����ʱ��
 }
 ......
 ]
 }
 2����������ս�Ŀ

 {
 show: {
 name: [STRING], // ��Ŀ�����֣�255�ֽ����ڵ��ַ���
 summary: [STRING], // ��Ŀ�ļ�飬255�ֽ����ڵ��ַ���
 poster: [URL], // ��Ŀ�ĺ���ͼƬ��ַ���ַ�����ʽ����httpЭ���ַ��ʽ
 closed: [BOOL], // ��ʾ��Ŀ�Ƿ��Ѿ���ᣬtrue����false
 episodes_count: [NUM], // ��Ŀ���ܼ�����������
 sources: [STRING] // ��Ŀ�Ŀ�����Դ��վ���Զ��ŷָ���ַ��������磺�ſ�,����,...
 like_num: int //ϲ�������Ŀ���û���
 watch_num: int //�ۿ��������Ŀ���û���
 favority_num: int �ղ������Ŀ���û���,
 score:  float ���������
 episodes: [
 {
 name: [STRING],
 video_urls: [
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,����
 url: [URL]
 },
 ...
 ]
 down_urls: [  //��Ƶ��ַ
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� 
 urls: [
 {
 "type": "mp4", mp4:���壬flv�����壬hd2������
 "url": [URL]
 }
 ]
 }
 .........
 ]
 },
 ...
 ]
 },
 comments: [
 {
 owner_id: int �������۵��û�id
 owner_name: string ���������û���
 owner_pic_url: string ���������û���ͷ��
 id: int ����id
 content: string ���۵�����
 create_date: date ����ʱ��
 }
 ......
 ]
 }
 3������ǵ�Ӱ

 {
 movie: {
 name: [STRING], // ��Ŀ�����֣�255�ֽ����ڵ��ַ���
 summary: [STRING], // ��Ŀ�ļ�飬255�ֽ����ڵ��ַ���
 poster: [URL], // ��Ŀ�ĺ���ͼƬ��ַ���ַ�����ʽ����httpЭ���ַ��ʽ
 like_num: int //ϲ�������Ŀ���û���
 watch_num: int //�ۿ��������Ŀ���û���
 favority_num: int �ղ������Ŀ���û���,
 score:  float ���������
 video_urls: [
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� 
 url: [URL]
 }
 .........
 ]
 down_urls: [  //��Ƶ��ַ
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� 
 urls: [
 {
 "type": "mp4", mp4:���壬flv�����壬hd2������
 "url": [URL]
 }
 ]
 }
 .........
 ]
 },
 comments: [
 {
 owner_id: int �������۵��û�id
 owner_name: string ���������û���
 owner_pic_url: string ���������û���ͷ��
 id: int ����id
 content: string ���۵�����
 create_date: date ����ʱ��
 }
 ......
 ]
 }   
 4���������Ƶ

 {
 video: {
 name: [STRING], // ��Ŀ�����֣�255�ֽ����ڵ��ַ���
 summary: [STRING], // ��Ŀ�ļ�飬255�ֽ����ڵ��ַ���
 poster: [URL], // ��Ŀ�ĺ���ͼƬ��ַ���ַ�����ʽ����httpЭ���ַ��ʽ
 like_num: int //ϲ�������Ŀ���û���
 watch_num: int //�ۿ��������Ŀ���û���
 favority_num: int �ղ������Ŀ���û���,
 score:  float ���������
 video_urls: [
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� 
 url: [URL]
 }
 .........
 ]
 down_urls: [  //��Ƶ��ַ
 {
 source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� 
 urls: [
 {
 "type": "mp4", mp4:���壬flv�����壬hd2������
 "url": [URL]
 }
 ]
 }
 .........
 ]
 },
 comments: [
 {
 owner_id: int �������۵��û�id
 owner_name: string ���������û���
 owner_pic_url: string ���������û���ͷ��
 id: int ����id
 content: string ���۵�����
 create_date: date ����ʱ��
 }
 ......
 ]
 }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnProgramView {

	public TV tv;
	public Show show;
	public Movie movie;
//	public Video video;
	public TOPICS[] topics;
	public COMMENTS[] comments;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class VIDEO_URLS {
		public String source;
		public String url;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TOPICS {
		public String t_name;
		public String t_id;
	}

	/*
	 * down_urls: [ //��Ƶ��ַ { source: [STRING], // ��Ŀ�Ŀ�����Դ��վ�����磺�ſ�,���� urls: [ {
	 * "type": "mp4", mp4:���壬flv�����壬hd2������ "url": [URL] } ] }
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DOWN_URLS {
		public String source;
		public URLS[] urls;
		public int index;

		public static class URLS {
			public String type;
			public String url;
			public String file;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EPISODES {

		public String name;
		public VIDEO_URLS[] video_urls;
		public DOWN_URLS[] down_urls;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class COMMENTS {
		public String owner_id;
		public String owner_name;
		public String owner_pic_url;
		public String id;
		public String content;
		public String create_date;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TV {
		public String name;
		public String summary;
		public String poster;
		public String like_num;
		public String watch_num;
		public String favority_num;
		public String score;
		public String ipad_poster;
		public String support_num;
		public String publish_date;
		public String directors;
		public String episodes_count;
		public String cur_episode;
		public String max_episode;
		public String stars;
		public String id;
		public String area;
		public String total_comment_number;
		public String definition;
		public int current_play;

		public EPISODES[] episodes;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Show {
		public String name;
		public String summary;
		public String poster;
		public String like_num;
		public String watch_num;
		public String favority_num;
		public String score;
		public String ipad_poster;
		public String support_num;
		public String publish_date;
		public String cur_episode;
		public String max_episode;
		public String directors;
		public String stars;
		public String id;
		public String area;
		public String total_comment_number;
		public String definition;
		public EPISODES[] episodes;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Movie {
		public String name;
		public String summary;
		public String poster;
		public String like_num;
		public String watch_num;
		public String favority_num;
		public String score;
		public String ipad_poster;
		public String support_num;
		public String publish_date;
		public String directors;
		public String stars;
		public String id;
		public String area;
		public String total_comment_number;
		public String definition;
		public String duration;
		public EPISODES[] episodes;

	}

//	@JsonIgnoreProperties(ignoreUnknown = true)
//	public static class Video {
//		public String name;
//		public String summary;
//		public String poster;
//		public String sources;
//		public String like_num;
//		public String watch_num;
//		public String favority_num;
//		public String score;
//
//		public VIDEO_URLS[] video_urls;
//		public DOWN_URLS[] down_urls;
//
//	}
}


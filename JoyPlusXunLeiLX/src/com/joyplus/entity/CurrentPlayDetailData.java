package com.joyplus.entity;

public class CurrentPlayDetailData {

	public String prod_id = "-1";// ���δ֪��Ϊ-1
	public String prod_name;// ��Ƶ����
	public String prod_sub_name;// ��ǰ������Ӧ��name
	public String prod_url;// ���ŵ�ַ
	public String prod_src;// ��Դ
	public long prod_time = 0;// ��ʼ����ʱ��, ��*1000 ,��λ�Ǻ���
	public int prod_qua = 8;// ������ 6Ϊ���ʣ�7Ϊ���壬8Ϊ����
	public boolean prod_favority = false;// �Ƿ��ղ�
	public int prod_type = 1;// ��Ƶ��� 1����Ӱ��2�����Ӿ磬3�����գ�131����
	public boolean hasReturnData = false;// �Ƿ���������Ϣ
	
	public Object obj;
}

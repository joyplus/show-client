package com.joyplus.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum SharpnessEnum {

	SD("标准",0),BIAO("标清", 1), HD("高清", 2), SUPER("超清", 3), BLUE("蓝光", 4), _3D("3d", 5);

	private int index;
	private String name;

	private SharpnessEnum(String name, int index) {

		this.name = name;
		this.index = index;
	}

	public static String getName(int paramInt) {
		
		SharpnessEnum[] arrayOfSharpnessEnum = values();
		
		for(int i=0;i<arrayOfSharpnessEnum.length;i++) {
			
			if (arrayOfSharpnessEnum[i].getIndex() == paramInt) {
				
				return arrayOfSharpnessEnum[i].name;
			}
		}
		
		return "";
	}

	public static SharpnessEnum getSharp(int paramInt) {
		
		switch (paramInt) {
		case 0:
			return SD;
		case 1:
			return BIAO;
		case 2:
			return HD;
		case 3:
			return SUPER;
		case 4:
			return BLUE;
		case 5:
			return _3D;
		default:
			return null;
		}
		
	}

	public static SharpnessEnum getSuitSharp(SharpnessEnum paramSharpnessEnum,
			List<SharpnessEnum> list) {
		
		if(list != null && list.size() > 0) {
			
			if(list.contains(paramSharpnessEnum)) {
				
				return paramSharpnessEnum;
			}
			
			ArrayList<Integer> integerList = new ArrayList<Integer>();
			for(int i=0;i<list.size();i++) {
				
				int differ = Math.abs(((SharpnessEnum) list.get(i)).index
						- paramSharpnessEnum.index);
				
				integerList.add(Integer.valueOf(differ));
			}
			
			List<Integer> cloneList = (List<Integer>) integerList.clone();
			Collections.sort(cloneList);
			
			return list.get(integerList.indexOf(cloneList.get(0).intValue()));
		}
		
		return null;
	}

	public int getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public void setIndex(int paramInt) {
		this.index = paramInt;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}
}

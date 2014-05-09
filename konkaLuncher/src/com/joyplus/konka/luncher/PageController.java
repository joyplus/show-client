package com.joyplus.konka.luncher;

public interface PageController {
//	public void showSkyworthPage(boolean isLeftSideFocus);
//	public void showKonkaPage(boolean isLeftSideFocus);
//	public void showHaierPage(boolean isLeftSideFocus);
	
	public static final int PAGE_SKYWORTH = 0;
	public static final int PAGE_KONKA = PAGE_SKYWORTH + 1;
	public static final int PAGE_HAIER = PAGE_KONKA + 1;
	public static final int PAGE_TCL = PAGE_HAIER + 1;
	
	public void showPage(int page, boolean isLeftSideFocus);
}

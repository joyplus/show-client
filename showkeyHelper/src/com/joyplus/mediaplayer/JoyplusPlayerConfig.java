package com.joyplus.mediaplayer;

public class JoyplusPlayerConfig {

	/*flog of name like mediaplayer,vitma*/
	public String NAME;
	
	/*flog of MediaPlayer 
	 * @link{#JoyplusMediaPlayerManager}
	 * @see# TYPE_MEDIAPLAYER
	 * @see# TYPE_VITAMIO*/
	public int TYPE;
	
	/*flog of useable*/
	public boolean EN;
	
	/*flog of support decode HW*/
	public boolean DECODE_HW;
	
	/*flog of support decode SW*/
	public boolean DECODE_SW;
	
	/*flog of prority decode HW
	 * this should be level 1-4 */
	public int PRIORITY_HW;
	
	/*flog of prority decode SW
	 * this should be level 1-4 */
	public int PRIORITY_SW;
	
	/*this should be init from a XML file */
	public JoyplusPlayerConfig(String init){
		String fragments[] = init.split(",");
        NAME      = fragments[0].trim();
        TYPE      = Integer.parseInt(fragments[1]);
        EN        = Boolean.parseBoolean(fragments[2]);
        DECODE_HW = Boolean.parseBoolean(fragments[3]);
        DECODE_SW = Boolean.parseBoolean(fragments[4]);
        PRIORITY_HW  = Integer.parseInt(fragments[5]);
        PRIORITY_SW  = Integer.parseInt(fragments[6]);
	}
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
        sb.append("JoyplusPlayerConfig{ NAME: ").append(NAME).
           append(", TYPE: ").append(TYPE).
           append(", EN: ").append(EN ).
           append(", DECODE_HW: ").append(DECODE_HW ).
           append(", DECODE_SW: ").append(DECODE_SW ).
           append(", PRIORITY_HW: ").append(PRIORITY_HW ).
           append(", PRIORITY_SW: ").append(PRIORITY_SW ).
           append("} ");
        return sb.toString();
	}
}

package org.anaguma.pasmoreader.pasmo.data;

import android.util.SparseArray;


/**
 * History for PASMO use
 * see `http://sourceforge.jp/projects/felicalib/wiki/suica`
 */
public class History {
    public int termId;
    public int procId;
    public int year;
    public int month;
    public int day;
    public String kind;
    public int remain;
    public int seqNo;
    public int reasion;


    public static final SparseArray<String> DEVICE__LIST = new SparseArray<String>();
    public static final SparseArray<String> ACTION_LIST = new SparseArray<String>();

    public History(){
    }

    public static History parse(byte[] res, int off) {
        History self = new History();
        self.init(res, off);
        return self;
    }

    private void init(byte[] res, int off) {
        this.termId = res[off+0]; //0: ’[––Ží
        this.procId = res[off+1]; //1: ˆ—
        //2-3: ??
        int mixInt = toInt(res, off, 4,5);
        this.year  = (mixInt >> 9) & 0x07f;
        this.month = (mixInt >> 5) & 0x00f;
        this.day   = mixInt & 0x01f;

        if (isShopping(this.procId)) {
            this.kind = "•¨”Ì";
        } else if (isBus(this.procId)) {
            this.kind = "ƒoƒX";
        } else {
            this.kind = res[off+6] < 0x80 ? "JR" : "Œö‰c/Ž„“S" ;
        }
        this.remain  = toInt(res, off, 11,10); //10-11: Žc‚ (little endian)
        this.seqNo   = toInt(res, off, 12,13,14); //12-14: ˜A”Ô
        this.reasion = res[off+15]; //15: ƒŠ[ƒWƒ‡ƒ“
    }

    private int toInt(byte[] res, int off, int... idx) {
        int num = 0;
        for (int i=0; i<idx.length; i++) {
            num = num << 8;
            num += ((int)res[off+idx[i]]) & 0x0ff;
        }
        return num;
    }
    private boolean isShopping(int procId) {
        return procId == 70 || procId == 73 || procId == 74
                || procId == 75 || procId == 198 || procId == 203;
    }
    private boolean isBus(int procId) {
        return procId == 13|| procId == 15|| procId ==  31|| procId == 35;
    }

    public String toString() {
        String str = seqNo
                +","+DEVICE__LIST.get(termId)
                +","+ ACTION_LIST.get(procId)
                +","+kind
                +","+year+"/"+month+"/"+day
                +",ŽcF"+remain+"‰~";
        return str;
    }


    static {
        DEVICE__LIST.put(3 , "¸ŽZ‹@");
        DEVICE__LIST.put(4 , "Œg‘ÑŒ^’[––");
        DEVICE__LIST.put(5 , "ŽÔÚ’[––");
        DEVICE__LIST.put(7 , "Œ””„‹@");
        DEVICE__LIST.put(8 , "Œ””„‹@");
        DEVICE__LIST.put(9 , "“ü‹à‹@");
        DEVICE__LIST.put(18 , "Œ””„‹@");
        DEVICE__LIST.put(20 , "Œ””„‹@“™");
        DEVICE__LIST.put(21 , "Œ””„‹@“™");
        DEVICE__LIST.put(22 , "‰üŽD‹@");
        DEVICE__LIST.put(23 , "ŠÈˆÕ‰üŽD‹@");
        DEVICE__LIST.put(24 , "‘‹Œû’[––");
        DEVICE__LIST.put(25 , "‘‹Œû’[––");
        DEVICE__LIST.put(26 , "‰üŽD’[––");
        DEVICE__LIST.put(27 , "Œg‘Ñ“d˜b");
        DEVICE__LIST.put(28 , "æŒp¸ŽZ‹@");
        DEVICE__LIST.put(29 , "˜A—‰üŽD‹@");
        DEVICE__LIST.put(31 , "ŠÈˆÕ“ü‹à‹@");
        DEVICE__LIST.put(70 , "VIEW ALTTE");
        DEVICE__LIST.put(72 , "VIEW ALTTE");
        DEVICE__LIST.put(199 , "•¨”Ì’[––");
        DEVICE__LIST.put(200 , "Ž©”Ì‹@");

        ACTION_LIST.put(1 , "‰^’ÀŽx•¥(‰üŽDoê)");
        ACTION_LIST.put(2 , "ƒ`ƒƒ[ƒW");
        ACTION_LIST.put(3 , "Œ”w(Ž¥‹CŒ”w“ü)");
        ACTION_LIST.put(4 , "¸ŽZ");
        ACTION_LIST.put(5 , "¸ŽZ (“üê¸ŽZ)");
        ACTION_LIST.put(6 , "‘‹o (‰üŽD‘‹Œûˆ—)");
        ACTION_LIST.put(7 , "V‹K (V‹K”­s)");
        ACTION_LIST.put(8 , "Tœ (‘‹ŒûTœ)");
        ACTION_LIST.put(13 , "ƒoƒX (PiTaPaŒn)");
        ACTION_LIST.put(15 , "ƒoƒX (IruCaŒn)");
        ACTION_LIST.put(17 , "Ä”­ (Ä”­sˆ—)");
        ACTION_LIST.put(19 , "Žx•¥ (VŠ²ü—˜—p)");
        ACTION_LIST.put(20 , "“üA (“üêŽžƒI[ƒgƒ`ƒƒ[ƒW)");
        ACTION_LIST.put(21 , "oA (oêŽžƒI[ƒgƒ`ƒƒ[ƒW)");
        ACTION_LIST.put(31 , "“ü‹à (ƒoƒXƒ`ƒƒ[ƒW)");
        ACTION_LIST.put(35 , "Œ”w (ƒoƒX˜H–Ê“dŽÔŠé‰æŒ”w“ü)");
        ACTION_LIST.put(70 , "•¨”Ì");
        ACTION_LIST.put(72 , "“Á“T (“Á“Tƒ`ƒƒ[ƒW)");
        ACTION_LIST.put(73 , "“ü‹à (ƒŒƒW“ü‹à)");
        ACTION_LIST.put(74 , "•¨”ÌŽæÁ");
        ACTION_LIST.put(75 , "“ü•¨ (“üê•¨”Ì)");
        ACTION_LIST.put(198 , "•¨Œ» (Œ»‹à•¹—p•¨”Ì)");
        ACTION_LIST.put(203 , "“ü•¨ (“üêŒ»‹à•¹—p•¨”Ì)");
        ACTION_LIST.put(132 , "¸ŽZ (‘¼ŽÐ¸ŽZ)");
        ACTION_LIST.put(133 , "¸ŽZ (‘¼ŽÐ“üê¸ŽZ)");
    }
}

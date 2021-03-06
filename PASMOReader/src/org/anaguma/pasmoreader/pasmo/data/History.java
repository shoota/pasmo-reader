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
        this.termId = res[off+0]; //0: [ν
        this.procId = res[off+1]; //1: 
        //2-3: ??
        int mixInt = toInt(res, off, 4,5);
        this.year  = (mixInt >> 9) & 0x07f;
        this.month = (mixInt >> 5) & 0x00f;
        this.day   = mixInt & 0x01f;

        if (isShopping(this.procId)) {
            this.kind = "¨Μ";
        } else if (isBus(this.procId)) {
            this.kind = "oX";
        } else {
            this.kind = res[off+6] < 0x80 ? "JR" : "φc/S" ;
        }
        this.remain  = toInt(res, off, 11,10); //10-11: c (little endian)
        this.seqNo   = toInt(res, off, 12,13,14); //12-14: AΤ
        this.reasion = res[off+15]; //15: [W
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
                +",cF"+remain+"~";
        return str;
    }


    static {
        DEVICE__LIST.put(3 , "ΈZ@");
        DEVICE__LIST.put(4 , "gΡ^[");
        DEVICE__LIST.put(5 , "ΤΪ[");
        DEVICE__LIST.put(7 , "@");
        DEVICE__LIST.put(8 , "@");
        DEVICE__LIST.put(9 , "όΰ@");
        DEVICE__LIST.put(18 , "@");
        DEVICE__LIST.put(20 , "@");
        DEVICE__LIST.put(21 , "@");
        DEVICE__LIST.put(22 , "όD@");
        DEVICE__LIST.put(23 , "ΘΥόD@");
        DEVICE__LIST.put(24 , "ϋ[");
        DEVICE__LIST.put(25 , "ϋ[");
        DEVICE__LIST.put(26 , "όD[");
        DEVICE__LIST.put(27 , "gΡdb");
        DEVICE__LIST.put(28 , "ζpΈZ@");
        DEVICE__LIST.put(29 , "AόD@");
        DEVICE__LIST.put(31 , "ΘΥόΰ@");
        DEVICE__LIST.put(70 , "VIEW ALTTE");
        DEVICE__LIST.put(72 , "VIEW ALTTE");
        DEVICE__LIST.put(199 , "¨Μ[");
        DEVICE__LIST.put(200 , "©Μ@");

        ACTION_LIST.put(1 , "^ΐx₯(όDoκ)");
        ACTION_LIST.put(2 , "`[W");
        ACTION_LIST.put(3 , "w(₯Cwό)");
        ACTION_LIST.put(4 , "ΈZ");
        ACTION_LIST.put(5 , "ΈZ (όκΈZ)");
        ACTION_LIST.put(6 , "o (όDϋ)");
        ACTION_LIST.put(7 , "VK (VK­s)");
        ACTION_LIST.put(8 , "T (ϋT)");
        ACTION_LIST.put(13 , "oX (PiTaPan)");
        ACTION_LIST.put(15 , "oX (IruCan)");
        ACTION_LIST.put(17 , "Δ­ (Δ­s)");
        ACTION_LIST.put(19 , "x₯ (V²όp)");
        ACTION_LIST.put(20 , "όA (όκI[g`[W)");
        ACTION_LIST.put(21 , "oA (oκI[g`[W)");
        ACTION_LIST.put(31 , "όΰ (oX`[W)");
        ACTION_LIST.put(35 , "w (oXHΚdΤιζwό)");
        ACTION_LIST.put(70 , "¨Μ");
        ACTION_LIST.put(72 , "ΑT (ΑT`[W)");
        ACTION_LIST.put(73 , "όΰ (Wόΰ)");
        ACTION_LIST.put(74 , "¨ΜζΑ");
        ACTION_LIST.put(75 , "ό¨ (όκ¨Μ)");
        ACTION_LIST.put(198 , "¨» (»ΰΉp¨Μ)");
        ACTION_LIST.put(203 , "ό¨ (όκ»ΰΉp¨Μ)");
        ACTION_LIST.put(132 , "ΈZ (ΌΠΈZ)");
        ACTION_LIST.put(133 , "ΈZ (ΌΠόκΈZ)");
    }
}

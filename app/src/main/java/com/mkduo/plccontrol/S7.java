package com.mkduo.plccontrol;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class S7 {
    public static final int S7AreaPE = 129;
    public static final int S7AreaPA = 130;
    public static final int S7AreaMK = 131;
    public static final int S7AreaDB = 132;
    public static final int S7AreaCT = 28;
    public static final int S7AreaTM = 29;
    public static final byte PG = 1;
    public static final byte OP = 2;
    public static final byte S7_BASIC = 3;
    public static final int Block_OB = 56;
    public static final int Block_DB = 65;
    public static final int Block_SDB = 66;
    public static final int Block_FC = 67;
    public static final int Block_SFC = 68;
    public static final int Block_FB = 69;
    public static final int Block_SFB = 70;
    public static final int SubBlk_OB = 8;
    public static final int SubBlk_DB = 10;
    public static final int SubBlk_SDB = 11;
    public static final int SubBlk_FC = 12;
    public static final int SubBlk_SFC = 13;
    public static final int SubBlk_FB = 14;
    public static final int SubBlk_SFB = 15;
    public static final int BlockLangAWL = 1;
    public static final int BlockLangKOP = 2;
    public static final int BlockLangFUP = 3;
    public static final int BlockLangSCL = 4;
    public static final int BlockLangDB = 5;
    public static final int BlockLangGRAPH = 6;
    public static final int S7CpuStatusUnknown = 0;
    public static final int S7CpuStatusRun = 8;
    public static final int S7CpuStatusStop = 4;
    public static final int S7TypeBool = 1;
    public static final int S7TypeInt = 1;

    public S7() {
    }

    public static boolean GetBitAt(byte[] Buffer, int Pos, int Bit) {
        int Value = Buffer[Pos] & 255;
        byte[] Mask = new byte[]{1, 2, 4, 8, 16, 16, 64, -128};
        if (Bit < 0) {
            Bit = 0;
        }

        if (Bit > 7) {
            Bit = 7;
        }

        return (Value & Mask[Bit]) != 0;
    }

    public static int GetWordAt(byte[] Buffer, int Pos) {
        int hi = Buffer[Pos] & 255;
        int lo = Buffer[Pos + 1] & 255;
        return (hi << 8) + lo;
    }

    public static int GetShortAt(byte[] Buffer, int Pos) {
        int hi = Buffer[Pos];
        int lo = Buffer[Pos + 1] & 255;
        return (hi << 8) + lo;
    }

    public static long GetDWordAt(byte[] Buffer, int Pos) {
        long Result = (long)(Buffer[Pos] & 255);
        Result <<= 8;
        Result += (long)(Buffer[Pos + 1] & 255);
        Result <<= 8;
        Result += (long)(Buffer[Pos + 2] & 255);
        Result <<= 8;
        Result += (long)(Buffer[Pos + 3] & 255);
        return Result;
    }

    public static int GetDIntAt(byte[] Buffer, int Pos) {
        int Result = Buffer[Pos];
        Result <<= 8;
        Result += Buffer[Pos + 1] & 255;
        Result <<= 8;
        Result += Buffer[Pos + 2] & 255;
        Result <<= 8;
        Result += Buffer[Pos + 3] & 255;
        return Result;
    }

    public static float GetFloatAt(byte[] Buffer, int Pos) {
        int IntFloat = GetDIntAt(Buffer, Pos);
        return Float.intBitsToFloat(IntFloat);
    }

    public static String GetStringAt(byte[] Buffer, int Pos, int MaxLen) {
        byte[] StrBuffer = new byte[MaxLen];
        System.arraycopy(Buffer, Pos, StrBuffer, 0, MaxLen);

        String S;
        try {
            S = new String(StrBuffer, "UTF-8");
        } catch (UnsupportedEncodingException var6) {
            S = "";
        }

        return S;
    }

    public static String GetPrintableStringAt(byte[] Buffer, int Pos, int MaxLen) {
        byte[] StrBuffer = new byte[MaxLen];
        System.arraycopy(Buffer, Pos, StrBuffer, 0, MaxLen);

        for(int c = 0; c < MaxLen; ++c) {
            if (StrBuffer[c] < 31 || StrBuffer[c] > 126) {
                StrBuffer[c] = 46;
            }
        }

        String S;
        try {
            S = new String(StrBuffer, "UTF-8");
        } catch (UnsupportedEncodingException var6) {
            S = "";
        }

        return S;
    }

    public static Date GetDateAt(byte[] Buffer, int Pos) {
        Calendar S7Date = Calendar.getInstance();
        int Year = BCDtoByte(Buffer[Pos]);
        if (Year < 90) {
            Year += 2000;
        } else {
            Year += 1900;
        }

        int Month = BCDtoByte(Buffer[Pos + 1]) - 1;
        int Day = BCDtoByte(Buffer[Pos + 2]);
        int Hour = BCDtoByte(Buffer[Pos + 3]);
        int Min = BCDtoByte(Buffer[Pos + 4]);
        int Sec = BCDtoByte(Buffer[Pos + 5]);
        S7Date.set(Year, Month, Day, Hour, Min, Sec);
        return S7Date.getTime();
    }

    public static void SetBitAt(byte[] Buffer, int Pos, int Bit, boolean Value) {
        byte[] Mask = new byte[]{1, 2, 4, 8, 16, 32, 64, -128};
        if (Bit < 0) {
            Bit = 0;
        }

        if (Bit > 7) {
            Bit = 7;
        }

        if (Value) {
            Buffer[Pos] |= Mask[Bit];
        } else {
            Buffer[Pos] = (byte)(Buffer[Pos] & ~Mask[Bit]);
        }

    }

    public static void SetWordAt(byte[] Buffer, int Pos, int Value) {
        int Word = Value & '\uffff';
        Buffer[Pos] = (byte)(Word >> 8);
        Buffer[Pos + 1] = (byte)(Word & 255);
    }

    public static void SetShortAt(byte[] Buffer, int Pos, int Value) {
        Buffer[Pos] = (byte)(Value >> 8);
        Buffer[Pos + 1] = (byte)(Value & 255);
    }

    public static void SetDWordAt(byte[] Buffer, int Pos, long Value) {
        long DWord = Value & -1L;
        Buffer[Pos + 3] = (byte)((int)(DWord & 255L));
        Buffer[Pos + 2] = (byte)((int)(DWord >> 8 & 255L));
        Buffer[Pos + 1] = (byte)((int)(DWord >> 16 & 255L));
        Buffer[Pos] = (byte)((int)(DWord >> 24 & 255L));
    }

    public static void SetDIntAt(byte[] Buffer, int Pos, int Value) {
        Buffer[Pos + 3] = (byte)(Value & 255);
        Buffer[Pos + 2] = (byte)(Value >> 8 & 255);
        Buffer[Pos + 1] = (byte)(Value >> 16 & 255);
        Buffer[Pos] = (byte)(Value >> 24 & 255);
    }

    public static void SetFloatAt(byte[] Buffer, int Pos, float Value) {
        int DInt = Float.floatToIntBits(Value);
        SetDIntAt(Buffer, Pos, DInt);
    }

    public static void SetDateAt(byte[] Buffer, int Pos, Date DateTime) {
        Calendar S7Date = Calendar.getInstance();
        S7Date.setTime(DateTime);
        int Year = S7Date.get(1);
        int Month = S7Date.get(2) + 1;
        int Day = S7Date.get(5);
        int Hour = S7Date.get(11);
        int Min = S7Date.get(12);
        int Sec = S7Date.get(13);
        int Dow = S7Date.get(7);
        if (Year > 1999) {
            Year -= 2000;
        }

        Buffer[Pos] = ByteToBCD(Year);
        Buffer[Pos + 1] = ByteToBCD(Month);
        Buffer[Pos + 2] = ByteToBCD(Day);
        Buffer[Pos + 3] = ByteToBCD(Hour);
        Buffer[Pos + 4] = ByteToBCD(Min);
        Buffer[Pos + 5] = ByteToBCD(Sec);
        Buffer[Pos + 6] = 0;
        Buffer[Pos + 7] = ByteToBCD(Dow);
    }

    public static int BCDtoByte(byte B) {
        return (B >> 4) * 10 + (B & 15);
    }

    public static byte ByteToBCD(int Value) {
        return (byte)(Value / 10 << 4 | Value % 10);
    }
}

package com.mkduo.plccontrol;

import java.util.Date;

public class S7BlockInfo {
    private final int BufSize = 96;
    private final long DeltaMilliSecs = 441763200000L;
    protected byte[] Buffer = new byte[96];

    public S7BlockInfo() {
    }

    protected void Update(byte[] Src, int Pos) {
        System.arraycopy(Src, Pos, this.Buffer, 0, 96);
    }

    public int BlkType() {
        return this.Buffer[2];
    }

    public int BlkNumber() {
        return S7.GetWordAt(this.Buffer, 3);
    }

    public int BlkLang() {
        return this.Buffer[1];
    }

    public int BlkFlags() {
        return this.Buffer[0];
    }

    public int MC7Size() {
        return S7.GetWordAt(this.Buffer, 31);
    }

    public int LoadSize() {
        return S7.GetDIntAt(this.Buffer, 5);
    }

    public int LocalData() {
        return S7.GetWordAt(this.Buffer, 29);
    }

    public int SBBLength() {
        return S7.GetWordAt(this.Buffer, 25);
    }

    public int Checksum() {
        return S7.GetWordAt(this.Buffer, 59);
    }

    public int Version() {
        return this.Buffer[57];
    }

    public Date CodeDate() {
        long BlockDate = (long)S7.GetWordAt(this.Buffer, 17) * 86400000L + 441763200000L;
        return new Date(BlockDate);
    }

    public Date IntfDate() {
        long BlockDate = (long)S7.GetWordAt(this.Buffer, 23) * 86400000L + 441763200000L;
        return new Date(BlockDate);
    }

    public String Author() {
        return S7.GetStringAt(this.Buffer, 33, 8);
    }

    public String Family() {
        return S7.GetStringAt(this.Buffer, 41, 8);
    }

    public String Header() {
        return S7.GetStringAt(this.Buffer, 49, 8);
    }
}
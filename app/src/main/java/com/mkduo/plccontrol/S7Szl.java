package com.mkduo.plccontrol;

public class S7Szl {
    public int LENTHDR;
    public int N_DR;
    public int DataSize;
    public byte[] Data;

    public S7Szl(int BufferSize) {
        this.Data = new byte[BufferSize];
    }

    protected void Copy(byte[] Src, int SrcPos, int DestPos, int Size) {
        System.arraycopy(Src, SrcPos, this.Data, DestPos, Size);
    }
}
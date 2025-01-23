package com.mkduo.plccontrol;

public class S7OrderCode {
    public int V1;
    public int V2;
    public int V3;
    protected byte[] Buffer = new byte[1024];

    public S7OrderCode() {
    }

    protected void Update(byte[] Src, int Pos, int Size) {
        System.arraycopy(Src, Pos, this.Buffer, 0, Size);
        this.V1 = Src[Size - 3];
        this.V2 = Src[Size - 2];
        this.V3 = Src[Size - 1];
    }

    public String Code() {
        return S7.GetStringAt(this.Buffer, 2, 20);
    }
}
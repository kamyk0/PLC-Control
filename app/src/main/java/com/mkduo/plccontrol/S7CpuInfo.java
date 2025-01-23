package com.mkduo.plccontrol;

public class S7CpuInfo {
    private final int BufSize = 256;
    protected byte[] Buffer = new byte[256];

    public S7CpuInfo() {
    }

    protected void Update(byte[] Src, int Pos) {
        System.arraycopy(Src, Pos, this.Buffer, 0, 256);
    }

    public String ModuleTypeName() {
        return S7.GetStringAt(this.Buffer, 172, 32);
    }

    public String SerialNumber() {
        return S7.GetStringAt(this.Buffer, 138, 24);
    }

    public String ASName() {
        return S7.GetStringAt(this.Buffer, 2, 24);
    }

    public String Copyright() {
        return S7.GetStringAt(this.Buffer, 104, 26);
    }

    public String ModuleName() {
        return S7.GetStringAt(this.Buffer, 36, 24);
    }
}

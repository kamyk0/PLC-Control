package com.mkduo.plccontrol;

public class S7CpInfo {
    public int MaxPduLength;
    public int MaxConnections;
    public int MaxMpiRate;
    public int MaxBusRate;

    public S7CpInfo() {
    }

    protected void Update(byte[] Src, int Pos) {
        this.MaxPduLength = S7.GetShortAt(Src, 2);
        this.MaxConnections = S7.GetShortAt(Src, 4);
        this.MaxMpiRate = S7.GetDIntAt(Src, 6);
        this.MaxBusRate = S7.GetDIntAt(Src, 10);
    }
}
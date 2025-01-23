package com.mkduo.plccontrol;

public class S7Protection {
    public int sch_schal;
    public int sch_par;
    public int sch_rel;
    public int bart_sch;
    public int anl_sch;

    public S7Protection() {
    }

    protected void Update(byte[] Src) {
        this.sch_schal = S7.GetWordAt(Src, 2);
        this.sch_par = S7.GetWordAt(Src, 4);
        this.sch_rel = S7.GetWordAt(Src, 6);
        this.bart_sch = S7.GetWordAt(Src, 8);
        this.anl_sch = S7.GetWordAt(Src, 10);
    }
}
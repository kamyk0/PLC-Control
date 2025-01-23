package com.mkduo.plccontrol;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;


public class S7Client {
    private static final byte S7WLByte = 2;
    private static final byte S7WLCounter = 28;
    private static final byte S7WLTimer = 29;
    public static final int errTCPConnectionFailed = 1;
    public static final int errTCPDataSend = 2;
    public static final int errTCPDataRecv = 3;
    public static final int errTCPDataRecvTout = 4;
    public static final int errTCPConnectionReset = 5;
    public static final int errISOInvalidPDU = 6;
    public static final int errISOConnectionFailed = 7;
    public static final int errISONegotiatingPDU = 8;
    public static final int errS7InvalidPDU = 9;
    public static final int errS7DataRead = 10;
    public static final int errS7DataWrite = 11;
    public static final int errS7BufferTooSmall = 12;
    public static final int errS7FunctionError = 13;
    public static final int errS7InvalidParams = 14;
    public boolean Connected = false;
    public int LastError = 0;
    public int RecvTimeout = 2000;
    private static final int ISOTCP = 102;
    private static final int MinPduSize = 16;
    private static final int DefaultPduSizeRequested = 480;
    private static final int IsoHSize = 7;
    private static final int MaxPduSize = 487;
    private Socket TCPSocket;
    private final byte[] PDU = new byte[2048];
    private DataInputStream InStream = null;
    private DataOutputStream OutStream = null;
    private String IPAddress;
    private byte LocalTSAP_HI;
    private byte LocalTSAP_LO;
    private byte RemoteTSAP_HI;
    private byte RemoteTSAP_LO;
    private byte LastPDUType;
    private short ConnType = 1;
    private int _PDULength = 0;
    private static final byte[] ISO_CR = new byte[]{3, 0, 0, 22, 17, -32, 0, 0, 0, 1, 0, -64, 1, 10, -63, 2, 1, 0, -62, 2, 1, 2};
    private static final byte[] S7_PN = new byte[]{3, 0, 0, 25, 2, -16, -128, 50, 1, 0, 0, 4, 0, 0, 8, 0, 0, -16, 0, 0, 1, 0, 1, 0, 30};
    private static final byte[] S7_RW = new byte[]{3, 0, 0, 31, 2, -16, -128, 50, 1, 0, 0, 5, 0, 0, 14, 0, 0, 4, 1, 18, 10, 16, 2, 0, 0, 0, 0, -124, 0, 0, 0, 0, 4, 0, 0};
    private static final int Size_RD = 31;
    private static final int Size_WR = 35;
    private static final byte[] S7_BI = new byte[]{3, 0, 0, 37, 2, -16, -128, 50, 7, 0, 0, 5, 0, 0, 8, 0, 12, 0, 1, 18, 4, 17, 67, 3, 0, -1, 9, 0, 8, 48, 65, 48, 48, 48, 48, 48, 65};
    private static final byte[] S7_SZL_FIRST = new byte[]{3, 0, 0, 33, 2, -16, -128, 50, 7, 0, 0, 5, 0, 0, 8, 0, 8, 0, 1, 18, 4, 17, 68, 1, 0, -1, 9, 0, 4, 0, 0, 0, 0};
    private static final byte[] S7_SZL_NEXT = new byte[]{3, 0, 0, 33, 2, -16, -128, 50, 7, 0, 0, 6, 0, 0, 12, 0, 4, 0, 1, 18, 8, 18, 68, 1, 1, 0, 0, 0, 0, 10, 0, 0, 0};
    private static final byte[] S7_GET_DT = new byte[]{3, 0, 0, 29, 2, -16, -128, 50, 7, 0, 0, 56, 0, 0, 8, 0, 4, 0, 1, 18, 4, 17, 71, 1, 0, 10, 0, 0, 0};
    private static final byte[] S7_SET_DT = new byte[]{3, 0, 0, 39, 2, -16, -128, 50, 7, 0, 0, -119, 3, 0, 8, 0, 14, 0, 1, 18, 4, 17, 71, 2, 0, -1, 9, 0, 10, 0, 25, 19, 18, 6, 23, 55, 19, 0, 1};
    private static final byte[] S7_STOP = new byte[]{3, 0, 0, 33, 2, -16, -128, 50, 1, 0, 0, 14, 0, 0, 16, 0, 0, 41, 0, 0, 0, 0, 0, 9, 80, 95, 80, 82, 79, 71, 82, 65, 77};
    private static final byte[] S7_HOT_START = new byte[]{3, 0, 0, 37, 2, -16, -128, 50, 1, 0, 0, 12, 0, 0, 20, 0, 0, 40, 0, 0, 0, 0, 0, 0, -3, 0, 0, 9, 80, 95, 80, 82, 79, 71, 82, 65, 77};
    private static final byte[] S7_COLD_START = new byte[]{3, 0, 0, 39, 2, -16, -128, 50, 1, 0, 0, 15, 0, 0, 22, 0, 0, 40, 0, 0, 0, 0, 0, 0, -3, 0, 2, 67, 32, 9, 80, 95, 80, 82, 79, 71, 82, 65, 77};
    private static final byte[] S7_GET_STAT = new byte[]{3, 0, 0, 33, 2, -16, -128, 50, 7, 0, 0, 44, 0, 0, 8, 0, 8, 0, 1, 18, 4, 17, 68, 1, 0, -1, 9, 0, 4, 4, 36, 0, 0};
    private static final byte[] S7_SET_PWD = new byte[]{3, 0, 0, 37, 2, -16, -128, 50, 7, 0, 0, 39, 0, 0, 8, 0, 12, 0, 1, 18, 4, 17, 69, 1, 0, -1, 9, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] S7_CLR_PWD = new byte[]{3, 0, 0, 29, 2, -16, -128, 50, 7, 0, 0, 41, 0, 0, 8, 0, 4, 0, 1, 18, 4, 17, 69, 2, 0, 10, 0, 0, 0};

    public S7Client() {
    }

    public static String ErrorText(int Error) {
        switch (Error) {
            case 1:
                return "TCP Connection failed.";
            case 2:
                return "TCP Sending error.";
            case 3:
                return "TCP Receiving error.";
            case 4:
                return "Data Receiving timeout.";
            case 5:
                return "Connection reset by the peer.";
            case 6:
                return "Invalid ISO PDU received.";
            case 7:
                return "ISO connection refused by the CPU.";
            case 8:
                return "ISO error negotiating the PDU length.";
            case 9:
                return "Invalid S7 PDU received.";
            case 10:
                return "S7 Error reading data from the CPU.";
            case 11:
                return "S7 Error writing data to the CPU.";
            case 12:
                return "The Buffer supplied to the function is too small.";
            case 13:
                return "S7 function refused by the CPU.";
            case 14:
                return "Invalid parameters supplied to the function.";
            default:
                return "Unknown error : 0x" + Integer.toHexString(Error);
        }
    }

    private int TCPConnect() {
        SocketAddress sockaddr = new InetSocketAddress(this.IPAddress, 102);
        this.LastError = 0;

        try {
            this.TCPSocket = new Socket();
            this.TCPSocket.connect(sockaddr, 5000);
            this.TCPSocket.setTcpNoDelay(true);
            this.InStream = new DataInputStream(this.TCPSocket.getInputStream());
            this.OutStream = new DataOutputStream(this.TCPSocket.getOutputStream());
        } catch (IOException var3) {
            this.LastError = 1;
        }

        return this.LastError;
    }

    private int WaitForData(int Size, int Timeout) {
        int cnt = 0;
        this.LastError = 0;
        boolean Expired = false;

        try {
            int SizeAvail = this.InStream.available();

            while(SizeAvail < Size && !Expired && this.LastError == 0) {
                ++cnt;

                try {
                    Thread.sleep(1L);
                } catch (InterruptedException var7) {
                    this.LastError = 4;
                }

                SizeAvail = this.InStream.available();
                Expired = cnt > Timeout;
                if (Expired && SizeAvail > 0 && this.LastError == 0) {
                    this.InStream.read(this.PDU, 0, SizeAvail);
                }
            }
        } catch (IOException var8) {
            this.LastError = 4;
        }

        if (cnt >= Timeout) {
            this.LastError = 4;
        }

        return this.LastError;
    }

    private int RecvPacket(byte[] Buffer, int Start, int Size) {
        int BytesRead = 0;
        this.LastError = this.WaitForData(Size, this.RecvTimeout);
        if (this.LastError == 0) {
            try {
                BytesRead = this.InStream.read(Buffer, Start, Size);
            } catch (IOException var6) {
                this.LastError = 3;
            }

            if (BytesRead == 0) {
                this.LastError = 5;
            }
        }

        return this.LastError;
    }

    private void SendPacket(byte[] Buffer, int Len) {
        this.LastError = 0;

        try {
            this.OutStream.write(Buffer, 0, Len);
            this.OutStream.flush();
        } catch (IOException var4) {
            this.LastError = 2;
        }

    }

    private void SendPacket(byte[] Buffer) {
        this.SendPacket(Buffer, Buffer.length);
    }

    private int RecvIsoPacket() {
        Boolean Done = false;
        int Size = 0;

        while(this.LastError == 0 && !Done) {
            this.RecvPacket(this.PDU, 0, 4);
            if (this.LastError == 0) {
                Size = S7.GetWordAt(this.PDU, 2);
                if (Size == 7) {
                    this.RecvPacket(this.PDU, 4, 3);
                } else if (Size <= 487 && Size >= 16) {
                    Done = true;
                } else {
                    this.LastError = 6;
                }
            }
        }

        if (this.LastError == 0) {
            this.RecvPacket(this.PDU, 4, 3);
            this.LastPDUType = this.PDU[5];
            this.RecvPacket(this.PDU, 7, Size - 7);
        }

        return this.LastError == 0 ? Size : 0;
    }

    private int ISOConnect() {
        ISO_CR[16] = this.LocalTSAP_HI;
        ISO_CR[17] = this.LocalTSAP_LO;
        ISO_CR[20] = this.RemoteTSAP_HI;
        ISO_CR[21] = this.RemoteTSAP_LO;
        this.SendPacket(ISO_CR);
        if (this.LastError == 0) {
            int Size = this.RecvIsoPacket();
            if (this.LastError == 0) {
                if (Size == 22) {
                    if (this.LastPDUType != -48) {
                        this.LastError = 7;
                    }
                } else {
                    this.LastError = 6;
                }
            }
        }

        return this.LastError;
    }

    private int NegotiatePduLength() {
        S7.SetWordAt(S7_PN, 23, 480);
        this.SendPacket(S7_PN);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (this.LastError == 0) {
                if (Length == 27 && this.PDU[17] == 0 && this.PDU[18] == 0) {
                    this._PDULength = S7.GetWordAt(this.PDU, 25);
                    if (this._PDULength > 0) {
                        return 0;
                    }

                    this.LastError = 8;
                } else {
                    this.LastError = 8;
                }
            }
        }

        return this.LastError;
    }

    public void SetConnectionType(short ConnectionType) {
        this.ConnType = ConnectionType;
    }

    public int Connect() {
        this.LastError = 0;
        if (!this.Connected) {
            this.TCPConnect();
            if (this.LastError == 0) {
                this.ISOConnect();
                if (this.LastError == 0) {
                    this.LastError = this.NegotiatePduLength();
                }
            }
        }

        this.Connected = this.LastError == 0;
        return this.LastError;
    }

    public void Disconnect() {
        if (this.Connected) {
            try {
                this.OutStream.close();
                this.InStream.close();
                this.TCPSocket.close();
                this._PDULength = 0;
            } catch (IOException var2) {
            }

            this.Connected = false;
        }

    }

    public int ConnectTo(String Address, int Rack, int Slot) {
        int RemoteTSAP = (this.ConnType << 8) + Rack * 32 + Slot;
        this.SetConnectionParams(Address, 256, RemoteTSAP);
        return this.Connect();
    }

    public int PDULength() {
        return this._PDULength;
    }

    public void SetConnectionParams(String Address, int LocalTSAP, int RemoteTSAP) {
        int LocTSAP = LocalTSAP & '\uffff';
        int RemTSAP = RemoteTSAP & '\uffff';
        this.IPAddress = Address;
        this.LocalTSAP_HI = (byte)(LocTSAP >> 8);
        this.LocalTSAP_LO = (byte)(LocTSAP & 255);
        this.RemoteTSAP_HI = (byte)(RemTSAP >> 8);
        this.RemoteTSAP_LO = (byte)(RemTSAP & 255);
    }

    public int ReadArea(int Area, int DBNumber, int Start, int Amount, byte[] Data) {
        int Offset = 0;
        int WordSize = 1;
        this.LastError = 0;
        if (Area == 28 || Area == 29) {
            WordSize = 2;
        }

        int MaxElements = (this._PDULength - 18) / WordSize;

        int NumElements;
        for(int TotElements = Amount; TotElements > 0 && this.LastError == 0; Start += NumElements * WordSize) {
            NumElements = TotElements;
            if (NumElements > MaxElements) {
                NumElements = MaxElements;
            }

            int SizeRequested = NumElements * WordSize;
            System.arraycopy(S7_RW, 0, this.PDU, 0, 31);
            this.PDU[27] = (byte)Area;
            if (Area == 132) {
                S7.SetWordAt(this.PDU, 25, DBNumber);
            }

            int Address;
            if (Area != 28 && Area != 29) {
                Address = Start << 3;
            } else {
                Address = Start;
                if (Area == 28) {
                    this.PDU[22] = 28;
                } else {
                    this.PDU[22] = 29;
                }
            }

            S7.SetWordAt(this.PDU, 23, NumElements);
            this.PDU[30] = (byte)(Address & 255);
            Address >>= 8;
            this.PDU[29] = (byte)(Address & 255);
            Address >>= 8;
            this.PDU[28] = (byte)(Address & 255);
            this.SendPacket(this.PDU, 31);
            if (this.LastError == 0) {
                int Length = this.RecvIsoPacket();
                if (this.LastError == 0) {
                    if (Length >= 25) {
                        if (Length - 25 == SizeRequested && this.PDU[21] == -1) {
                            System.arraycopy(this.PDU, 25, Data, Offset, SizeRequested);
                            Offset += SizeRequested;
                        } else {
                            this.LastError = 10;
                        }
                    } else {
                        this.LastError = 9;
                    }
                }
            }

            TotElements -= NumElements;
        }

        return this.LastError;
    }

    public int WriteArea(int Area, int DBNumber, int Start, int Amount, byte[] Data) {
        int Offset = 0;
        int WordSize = 1;
        this.LastError = 0;
        if (Area == 28 || Area == 29) {
            WordSize = 2;
        }

        int MaxElements = (this._PDULength - 35) / WordSize;

        int NumElements;
        for(int TotElements = Amount; TotElements > 0 && this.LastError == 0; Start += NumElements * WordSize) {
            NumElements = TotElements;
            if (NumElements > MaxElements) {
                NumElements = MaxElements;
            }

            int DataSize = NumElements * WordSize;
            int IsoSize = 35 + DataSize;
            System.arraycopy(S7_RW, 0, this.PDU, 0, 35);
            S7.SetWordAt(this.PDU, 2, IsoSize);
            int Length = DataSize + 4;
            S7.SetWordAt(this.PDU, 15, Length);
            this.PDU[17] = 5;
            this.PDU[27] = (byte)Area;
            if (Area == 132) {
                S7.SetWordAt(this.PDU, 25, DBNumber);
            }

            int Address;
            if (Area != 28 && Area != 29) {
                Address = Start << 3;
                Length = DataSize << 3;
            } else {
                Address = Start;
                Length = DataSize;
                if (Area == 28) {
                    this.PDU[22] = 28;
                } else {
                    this.PDU[22] = 29;
                }
            }

            S7.SetWordAt(this.PDU, 23, NumElements);
            this.PDU[30] = (byte)(Address & 255);
            Address >>= 8;
            this.PDU[29] = (byte)(Address & 255);
            Address >>= 8;
            this.PDU[28] = (byte)(Address & 255);
            S7.SetWordAt(this.PDU, 33, Length);
            System.arraycopy(Data, Offset, this.PDU, 35, DataSize);
            this.SendPacket(this.PDU, IsoSize);
            if (this.LastError == 0) {
                Length = this.RecvIsoPacket();
                if (this.LastError == 0) {
                    if (Length == 22) {
                        if (S7.GetWordAt(this.PDU, 17) != 0 || this.PDU[21] != -1) {
                            this.LastError = 11;
                        }
                    } else {
                        this.LastError = 9;
                    }
                }
            }

            Offset += DataSize;
            TotElements -= NumElements;
        }

        return this.LastError;
    }

    public int GetAgBlockInfo(int BlockType, int BlockNumber, S7BlockInfo Block) {
        this.LastError = 0;
        S7_BI[30] = (byte)BlockType;
        S7_BI[31] = (byte)(BlockNumber / 10000 + 48);
        BlockNumber %= 10000;
        S7_BI[32] = (byte)(BlockNumber / 1000 + 48);
        BlockNumber %= 1000;
        S7_BI[33] = (byte)(BlockNumber / 100 + 48);
        BlockNumber %= 100;
        S7_BI[34] = (byte)(BlockNumber / 10 + 48);
        BlockNumber %= 10;
        S7_BI[35] = (byte)(BlockNumber / 1 + 48);
        this.SendPacket(S7_BI);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 32) {
                if (S7.GetWordAt(this.PDU, 27) == 0 && this.PDU[29] == -1) {
                    Block.Update(this.PDU, 42);
                } else {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int DBGet(int DBNumber, byte[] Buffer, IntByRef SizeRead) {
        S7BlockInfo Block = new S7BlockInfo();
        this.LastError = this.GetAgBlockInfo(65, DBNumber, Block);
        if (this.LastError == 0) {
            int SizeToRead = Block.MC7Size();
            if (SizeToRead <= Buffer.length) {
                this.LastError = this.ReadArea(132, DBNumber, 0, SizeToRead, Buffer);
                if (this.LastError == 0) {
                    SizeRead.Value = SizeToRead;
                }
            } else {
                this.LastError = 12;
            }
        }

        return this.LastError;
    }

    public int ReadSZL(int ID, int Index, S7Szl SZL) {
        int Offset = 0;
        boolean Done = false;
        boolean First = true;
        byte Seq_in = 0;
        int Seq_out = 0;
        this.LastError = 0;
        SZL.DataSize = 0;

        do {
            if (First) {
                ++Seq_out;
                S7.SetWordAt(S7_SZL_FIRST, 11, Seq_out);
                S7.SetWordAt(S7_SZL_FIRST, 29, ID);
                S7.SetWordAt(S7_SZL_FIRST, 31, Index);
                this.SendPacket(S7_SZL_FIRST);
            } else {
                ++Seq_out;
                S7.SetWordAt(S7_SZL_NEXT, 11, Seq_out);
                this.PDU[24] = Seq_in;
                this.SendPacket(S7_SZL_NEXT);
            }

            if (this.LastError != 0) {
                return this.LastError;
            }

            int Length = this.RecvIsoPacket();
            if (this.LastError == 0) {
                int DataSZL;
                if (First) {
                    if (Length > 32) {
                        if (S7.GetWordAt(this.PDU, 27) == 0 && this.PDU[29] == -1) {
                            DataSZL = S7.GetWordAt(this.PDU, 31) - 8;
                            Done = this.PDU[26] == 0;
                            Seq_in = this.PDU[24];
                            SZL.LENTHDR = S7.GetWordAt(this.PDU, 37);
                            SZL.N_DR = S7.GetWordAt(this.PDU, 39);
                            SZL.Copy(this.PDU, 41, Offset, DataSZL);
                            Offset += DataSZL;
                            SZL.DataSize += DataSZL;
                        } else {
                            this.LastError = 13;
                        }
                    } else {
                        this.LastError = 9;
                    }
                } else if (Length > 32) {
                    if (S7.GetWordAt(this.PDU, 27) == 0 && this.PDU[29] == -1) {
                        DataSZL = S7.GetWordAt(this.PDU, 31);
                        Done = this.PDU[26] == 0;
                        Seq_in = this.PDU[24];
                        SZL.Copy(this.PDU, 37, Offset, DataSZL);
                        Offset += DataSZL;
                        SZL.DataSize += DataSZL;
                    } else {
                        this.LastError = 13;
                    }
                } else {
                    this.LastError = 9;
                }
            }

            First = false;
        } while(!Done && this.LastError == 0);

        return this.LastError;
    }

    public int GetCpuInfo(S7CpuInfo Info) {
        S7Szl SZL = new S7Szl(1024);
        this.LastError = this.ReadSZL(28, 0, SZL);
        if (this.LastError == 0) {
            Info.Update(SZL.Data, 0);
        }

        return this.LastError;
    }

    public int GetCpInfo(S7CpInfo Info) {
        S7Szl SZL = new S7Szl(1024);
        this.LastError = this.ReadSZL(305, 1, SZL);
        if (this.LastError == 0) {
            Info.Update(SZL.Data, 0);
        }

        return this.LastError;
    }

    public int GetOrderCode(S7OrderCode Code) {
        S7Szl SZL = new S7Szl(1024);
        this.LastError = this.ReadSZL(17, 0, SZL);
        if (this.LastError == 0) {
            Code.Update(SZL.Data, 0, SZL.DataSize);
        }

        return this.LastError;
    }

    public int GetPlcDateTime(Date DateTime) {
        this.LastError = 0;
        this.SendPacket(S7_GET_DT);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 30) {
                if (S7.GetWordAt(this.PDU, 27) == 0 && this.PDU[29] == -1) {
                    DateTime = S7.GetDateAt(this.PDU, 34);
                } else {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int SetPlcDateTime(Date DateTime) {
        this.LastError = 0;
        S7.SetDateAt(S7_SET_DT, 31, DateTime);
        this.SendPacket(S7_SET_DT);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 30) {
                if (S7.GetWordAt(this.PDU, 27) != 0) {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int SetPlcSystemDateTime() {
        return this.SetPlcDateTime(new Date());
    }

    public int PlcStop() {
        this.LastError = 0;
        this.SendPacket(S7_STOP);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 18) {
                if (S7.GetWordAt(this.PDU, 17) != 0) {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int PlcHotStart() {
        this.LastError = 0;
        this.SendPacket(S7_HOT_START);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 18) {
                if (S7.GetWordAt(this.PDU, 17) != 0) {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int PlcColdStart() {
        this.LastError = 0;
        this.SendPacket(S7_COLD_START);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 18) {
                if (S7.GetWordAt(this.PDU, 17) != 0) {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int GetPlcStatus(IntByRef Status) {
        this.LastError = 0;
        this.SendPacket(S7_GET_STAT);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 30) {
                if (S7.GetWordAt(this.PDU, 27) == 0) {
                    switch (this.PDU[44]) {
                        case 0:
                        case 4:
                        case 8:
                            Status.Value = this.PDU[44];
                            break;
                        default:
                            Status.Value = 4;
                    }
                } else {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int SetSessionPassword(String Password) {
        byte[] pwd = new byte[]{32, 32, 32, 32, 32, 32, 32, 32};
        this.LastError = 0;
        if (Password.length() > 8) {
            Password = Password.substring(0, 8);
        } else {
            while(Password.length() < 8) {
                Password = Password + " ";
            }
        }

        try {
            pwd = Password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException var5) {
            this.LastError = 14;
        }

        if (this.LastError == 0) {
            pwd[0] = (byte)(pwd[0] ^ 85);
            pwd[1] = (byte)(pwd[1] ^ 85);

            for(int c = 2; c < 8; ++c) {
                pwd[c] = (byte)(pwd[c] ^ 85 ^ pwd[c - 2]);
            }

            System.arraycopy(pwd, 0, S7_SET_PWD, 29, 8);
            this.SendPacket(S7_SET_PWD);
            if (this.LastError == 0) {
                int Length = this.RecvIsoPacket();
                if (Length > 32) {
                    if (S7.GetWordAt(this.PDU, 27) != 0) {
                        this.LastError = 13;
                    }
                } else {
                    this.LastError = 9;
                }
            }
        }

        return this.LastError;
    }

    public int ClearSessionPassword() {
        this.LastError = 0;
        this.SendPacket(S7_CLR_PWD);
        if (this.LastError == 0) {
            int Length = this.RecvIsoPacket();
            if (Length > 30) {
                if (S7.GetWordAt(this.PDU, 27) != 0) {
                    this.LastError = 13;
                }
            } else {
                this.LastError = 9;
            }
        }

        return this.LastError;
    }

    public int GetProtection(S7Protection Protection) {
        S7Szl SZL = new S7Szl(256);
        this.LastError = this.ReadSZL(562, 4, SZL);
        if (this.LastError == 0) {
            Protection.Update(SZL.Data);
        }

        return this.LastError;
    }
}
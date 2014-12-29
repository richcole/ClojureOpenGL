package game.nwn.readers;

import java.util.Map;

import com.google.common.collect.Maps;

public enum ResourceType {
  RES(0x0000),
  BMP(0x0001),
  MVE(0x0002),
  TGA(0x0003),
  WAV(0x0004),
  PLT(0x0006),
  INI(0x0007),
  BMU(0x0008),
  MPG(0x0009),
  TXT(0x000A),
  PLH(0x07D0),
  TEX(0x07D1),
  MDL(0x07D2),
  THG(0x07D3),
  FNT(0x07D5),
  LUA(0x07D7),
  SLT(0x07D8),
  NSS(0x07D9),
  NCS(0x07DA),
  MOD(0x07DB),
  ARE(0x07DC),
  SET(0x07DD),
  IFO(0x07DE),
  BIC(0x07DF),
  WOK(0x07E0),
  TDA(0x07E1),
  TLK(0x07E2),
  TXI(0x07E6),
  GIT(0x07E7),
  BTI(0x07E8),
  UTI(0x07E9),
  BTC(0x07EA),
  UTC(0x07EB),
  DLG(0x07ED),
  ITP(0x07EE),
  BTT(0x07EF),
  UTT(0x07F0),
  DDS(0x07F1),
  UTS(0x07F3),
  LTR(0x07F4),
  GFF(0x07F5),
  FAC(0x07F6),
  BTE(0x07F7),
  UTE(0x07F8),
  BTD(0x07F9),
  UTD(0x07FA),
  BTP(0x07FB),
  UTP(0x07FC),
  DTF(0x07FD),
  GIC(0x07FE),
  GUI(0x07FF),
  CSS(0x0800),
  CCS(0x0801),
  BTM(0x0802),
  UTM(0x0803),
  DWK(0x0804),
  PWK(0x0805),
  BTG(0x0806),
  UTG(0x0807),
  JRL(0x0808),
  SAV(0x0809),
  UTW(0x080A),
  FPC(0x080B),
  SSF(0x080C),
  HAK(0x080D),
  NWM(0x080E),
  BIK(0x080F),
  NDB(0x0810),
  PTM(0x0811),
  PTT(0x0812),
  ERF(0x270D),
  BIF(0x270E),
  KEY(0x270F),
  END(-1);
  
  int id;

  static Map<Integer, ResourceType> idMap = Maps.newHashMap();

  static {
    for(ResourceType t: values()) {
      idMap.put(t.id, t);
    }
  }
  
  public static ResourceType getType(int id) {
    return idMap.get(id);
  }

  ResourceType(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}

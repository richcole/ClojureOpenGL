package game.nwn.readers;

import java.util.Map;

import com.google.common.collect.Maps;

public class Resources {
  
  Map<Integer, String> extensions = Maps.newHashMap();
  
  Resources() {
    addResources();
  }
  
  public String getExtension(int type) {
    return extensions.get(type);
    
  }
  
  public void add(String res, int type) {
    extensions.put(type, res);
  }

  void addResources() {
    add("res", 0);
    add("bmp", 1);
    add("mve", 2);
    add("tga", 3);
    add("wav", 4);
    add("wfx", 5);
    add("plt", 6);
    add("ini", 7);
    add("mp3", 8);
    add("mpg", 9);
    add("txt", 10);
    add("plh", 2000);
    add("tex", 2001);
    add("mdl", 2002);
    add("thg", 2003);
    add("fnt", 2005);
    add("lua", 2007);
    add("slt", 2008);
    add("nss", 2009);
    add("ncs", 2010);
    add("mod", 2011);
    add("are", 2012);
    add("set", 2013);
    add("ifo", 2014);
    add("bic", 2015);
    add("wok", 2016);
    add("2da", 2017);
    add("tlk", 2018);
    add("txi", 2022);
    add("git", 2023);
    add("bti", 2024);
    add("uti", 2025);
    add("btc", 2026);
    add("utc", 2027);
    add("dlg", 2029);
    add("itp", 2030);
    add("btt", 2031);
    add("utt", 2032);
    add("dds", 2033);
    add("bts", 2034);
    add("uts", 2035);
    add("ltr", 2036);
    add("gff", 2037);
    add("fac", 2038);
    add("bte", 2039);
    add("ute", 2040);
    add("btd", 2041);
    add("utd", 2042);
    add("btp", 2043);
    add("utp", 2044);
    add("dft", 2045);
    add("gic", 2046);
    add("gui", 2047);
    add("css", 2048);
    add("ccs", 2049);
    add("btm", 2050);
    add("utm", 2051);
    add("dwk", 2052);
    add("pwk", 2053);
    add("btg", 2054);
    add("utg", 2055);
    add("jrl", 2056);
    add("sav", 2057);
    add("utw", 2058);
    add("4pc", 2059);
    add("ssf", 2060);
    add("hak", 2061);
    add("nwm", 2062);
    add("bik", 2063);
    add("ndb", 2064);
    add("ptm", 2065);
    add("ptt", 2066);
    add("bak", 2067);
    add("osc", 3000);
    add("usc", 3001);
    add("trn", 3002);
    add("utr", 3003);
    add("uen", 3004);
    add("ult", 3005);
    add("sef", 3006);
    add("pfx", 3007);
    add("cam", 3008);
    add("lfx", 3009);
    add("bfx", 3010);
    add("upe", 3011);
    add("ros", 3012);
    add("rst", 3013);
    add("ifx", 3014);
    add("pfb", 3015);
    add("zip", 3016);
    add("wmp", 3017);
    add("bbx", 3018);
    add("tfx", 3019);
    add("wlk", 3020);
    add("xml", 3021);
    add("scc", 3022);
    add("ptx", 3033);
    add("ltx", 3034);
    add("trx", 3035);
    add("mdb", 4000);
    add("mda", 4001);
    add("spt", 4002);
    add("gr2", 4003);
    add("fxa", 4004);
    add("fxe", 4005);
    add("jpg", 4007);
    add("pwc", 4008);
    add("ids", 9996);
    add("erf", 9997);
    add("bif", 9998);
    add("key", 9999);
  }
}

package flyproject.weaponsupgrade;

public class Test {
    public static void main(String[] args) {
        System.out.println(getNowPoint("进度: 1/100","进度: ","/",100));
    }
	public static int s2i(String s){
        return Integer.parseInt(s);
    }

    public static String i2s(int i){
        return String.valueOf(i);
    }
    public static int getNowPoint(String eqlore, String suffix, String symbol, int maxpoint){
        String ret = eqlore;
        ret = ret.replace(suffix,"");
        ret = ret.replace(symbol,"");
        ret = ret.replace(i2s(maxpoint),"");
        return s2i(ret);
    }
}

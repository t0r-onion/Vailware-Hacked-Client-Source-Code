
package toniqx.AltManager;

import io.netty.util.internal.ThreadLocalRandom;
import toniqx.AltManager.GuiAddAlt.AddAltThread;
import toniqx.vailware.main.files.FileManager;

public final class Alt {
    private String mask = "";
    private final String username;
    private String password;
    public static String genname = "Vailware";
    private static AltLoginThread thread;
    

    public Alt(String username, String password) {
        this(username, password, "");
    }

    public Alt(String username, String password, String mask) {
        this.username = username;
        this.password = password;
        this.mask = mask;
    }
    
    public static void genCracked() {
		int random = ThreadLocalRandom.current().nextInt(100, 998 + 1);
		int random2 = ThreadLocalRandom.current().nextInt(1, 100 + 1);
        String[] names = {"Cautious","Charming","Cheerful","Clever","Clumsy","Cloudy","Colorful","Combative","Concerned","Crazy","Dark", "Dull","Drab","Dizzy","Different","Difficult","Distinct","Cautious","Colorful","Careful","Grumpy","Graceful","Homeless","Helpless","Happy","Innocent","Horrible","Perfect","Pleasant","Open","Proud","Repulsive","Real","Spotless","Splendid","Smiling","Shy","Shiny","Sleepy","Tame","Thoughtless","Victorious","Weary","Worried","Zany","Witty","Stocky","Skinny","Short","Shapely","Scruffy","Quaint","Plump","Plain","Muscular","Long","Handsome","Unkempt","Unsightly","Attractive","Bald","Chubby","Elegant","Fit","Glamorous"};
        String name = names[(int) (Math.random() * names.length)];
        String[] names2 = {"Apple","Stomach","Drawer","Cushion","Hammer","Window","Trousers","Umbrella","Stocking","Receipt","Orange","Lock","Leg","Fish","Finger","Ear","Cow","Cloud","Chess","Card","Bee","Bed","Year","Word","Weather","War","View","Vessel","Thought","Sky","Shade","Sand","River","Request","Reaction","Plant","Part","Owner","Order","Motion","Middle","Meal","Liquid","Leather","Laugh","Judge","Jelly","Industry","Humor","Growth","Gold","Grain","Fall","Flower","Flame","Error","Existence","Education","Doubt","Discussion","Debt","Crime","Comfort","Business","Building","Brother","Act","Account"};
        String name2 = names2[(int) (Math.random() * names2.length)];
    	String acc = name + name2 + random;
        thread = new AltLoginThread(acc, "");
        thread.start();
    }

    public String getMask() {
        return this.mask;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


package toniqx.vailware;

import java.util.List;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import toniqx.AltManager.GuiAltManager;
import toniqx.discord.VailRPC;
import toniqx.vailware.font.GlyphPageFontRenderer;
import toniqx.vailware.main.command.CommandManager;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventChat;
import toniqx.vailware.main.event.listeners.EventKey;
import toniqx.vailware.main.event.listeners.EventUpdate;
import toniqx.vailware.main.event.listeners.PreMotionEvent;
import toniqx.vailware.main.notification.Notification;
import toniqx.vailware.main.notification.impl.Color;
import toniqx.vailware.main.notification.impl.NotificationManager;
import toniqx.vailware.main.notification.impl.Type;
import toniqx.vailware.main.util.impl.ScaffoldRenderUtils;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.Module.Category;
import toniqx.vailware.modules.combat.*;
import toniqx.vailware.modules.exploit.ClickTP;
import toniqx.vailware.modules.exploit.Disabler;
import toniqx.vailware.modules.exploit.FastEat;
import toniqx.vailware.modules.exploit.NoPacketKick;
import toniqx.vailware.modules.exploit.Regen;
import toniqx.vailware.modules.ghost.AimAssist;
import toniqx.vailware.modules.ghost.AutoClicker;
import toniqx.vailware.modules.ghost.Eagle;
import toniqx.vailware.modules.ghost.FakeLag;
import toniqx.vailware.modules.ghost.KeepSprint;
import toniqx.vailware.modules.ghost.LeftClickTimer;
import toniqx.vailware.modules.ghost.Parkour;
import toniqx.vailware.modules.ghost.SelfDestruct;
import toniqx.vailware.modules.misc.*;
import toniqx.vailware.modules.movement.*;
import toniqx.vailware.modules.player.*;
import toniqx.vailware.modules.render.*;
import toniqx.vailware.modules.world.NoRotate;
import toniqx.vailware.ui.hud.HUD;
import toniqx.vailware.ui.hud.TabGUI;
import viamcp.ViaMCP;

public class Client {

	public static boolean premium = false;
	
	public static String name = "Vailware";
	public static String version = "7.0";
	public static int color = 0xffffa6f9;
	public static String username;
	
	//ClickguiAnimationStuff
	
	public static float boolTranslation;
	public static float categoryTranslation;
	public static float settingsTranslation;
	
	public static float changelogOffset;
	
	public static float chatTranslationOffset;
	public static float hotbarYawTranslation;
	public static float posX, posY;
	public static String selectedCategory;
	public static Client instance;
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();
	public static HUD hud = new HUD();
	public static CommandManager commandManager = new CommandManager();
	public static ClickGUI clickGui;
	public static KillAura killAura;
	public static TargetStrafe targetStrafe;
	public static Fly fly;
	public static AutoArmor autoArmor;
	public static InvManager invManager;

	public static String originalUsername = "Not Set";
	public static Boolean originalAccountOnline;
	public static boolean musicPlayerFailedToStart;
	
	public static boolean lightmode;
	public static boolean gradient;
	public static boolean boolean1;
	public static boolean boolean2;
	
    public static File vailDir = new File(Minecraft.getMinecraft().mcDataDir, "Vailware");

	public static boolean destruct;
	
	public static final Client INSTANCE = new Client();
	public static final Client getInstance() {
		return INSTANCE;
	}
	
	private VailRPC vailRPC = new VailRPC();
	
	public void init() {
		vailRPC.start();
	}
	
	public void shutdown() {
		vailRPC.shutdown();
	}
	
	public VailRPC getVailRPC() {
		return vailRPC;
	}
	
	public static String verServer = Client.version;
	
	static Minecraft mc = Minecraft.getMinecraft();
	
	public static void startup() throws IOException{
		
    	mc.FontRendererMiniture = GlyphPageFontRenderer.create("Product Sans", 11, false, false, false);
    	mc.FontRendererArray = GlyphPageFontRenderer.create("Product Sans", 19, false, false, false);
    	mc.FontRendererClean = GlyphPageFontRenderer.create("Product Sans", 17, false, false, false);
    	mc.FontRendererTiny = GlyphPageFontRenderer.create("Product Sans", 14, false, false, false);
    	mc.FontRendererSmall = GlyphPageFontRenderer.create("Product Sans", 17, false, false, false);
    	mc.FontRendererNormal = GlyphPageFontRenderer.create("Product Sans", 21, false, false, false);
    	mc.FontRendererLarge = GlyphPageFontRenderer.create("Product Sans", 25, false, false, false);
    	mc.FontRendererHuge = GlyphPageFontRenderer.create("Comfortaa", 35, false, false, false);
    	mc.FontRendererArrayBold = GlyphPageFontRenderer.create("Product Sans", 19, true, false, false);
    	mc.FontRendererTinyBold = GlyphPageFontRenderer.create("Product Sans", 14, true, false, false);
    	mc.FontRendererSmallBold = GlyphPageFontRenderer.create("Product Sans", 17, true, false, false);
    	mc.FontRendererNormalBold = GlyphPageFontRenderer.create("Product Sans", 21, true, false, false);
    	mc.FontRendererLargeBold = GlyphPageFontRenderer.create("Product Sans", 25, true, false, false);
    	mc.FontRendererBig = GlyphPageFontRenderer.create("Product Sans", 30, true, false, false);
    	mc.FontJello = GlyphPageFontRenderer.create("San Francisco", 50, false, true, false);
		
		username = "";
		
		try {
		  ViaMCP.getInstance().start();
		}
		catch (Exception e){
		}
		
		try {
			verServer = requestURLSRC("https://pastebin.com/raw/HjhvBWvx").toString();
		}catch(Exception e) {
		}
		
		System.out.println("Starting " + name + " " + version);
		Display.setTitle(name + " b" + version);
		
		System.out.println(verServer);
		
		//Combat
		modules.add(new Aimbot());
		modules.add(new AntiBot());
		modules.add(new AutoPot());
		modules.add(new Criticals());
		modules.add(killAura = new KillAura());
		modules.add(new NoYaw());
		modules.add(new Reach());
		modules.add(new AutoGapple());
		modules.add(new TPAura());
		
		//Misc
		modules.add(new Autoplay());
		modules.add(new BlockReach());
		modules.add(new FastBreak());
		modules.add(new FreeLook());
		modules.add(new FastPlace());
		modules.add(new Panic());
		modules.add(new SlowMotion());
		modules.add(new Targets());
		modules.add(new toniqx.vailware.modules.misc.Timer());
		modules.add(new BedBreaker());
		modules.add(new Spammer());
		modules.add(new KillSults());
		modules.add(new toniqx.vailware.modules.misc.ArrayList());
		modules.add(new AntiLixo());
		modules.add(new AutoRegister());
		
		//Movement
		modules.add(new AirJump());
		modules.add(new AutoWalk());
		modules.add(fly = new Fly());
		modules.add(new Glide());
		modules.add(new LongJump());
		modules.add(new SafeWalk());
		modules.add(new Speed());
		modules.add(new Spider());
		modules.add(new Sprint());
		modules.add(new Step());
		modules.add(new AntiKB());
		modules.add(new HighJump());
		modules.add(new Scaffold());
		modules.add(targetStrafe = new TargetStrafe());
		modules.add(new Blink());
		
		//Player
		modules.add(autoArmor = new AutoArmor());
		modules.add(new ChestSteal());
		modules.add(invManager = new InvManager());
		modules.add(new InvMove());
		modules.add(new Jesus());
		modules.add(new NoFall());
		modules.add(new NoSlow());
		modules.add(new NoRotate());
		modules.add(new NameProtect());
		modules.add(new AntiVoid());
		
		//Render
		modules.add(new OldHitting());
		modules.add(new RotateItem());
		modules.add(new Chams());
		modules.add(new ChestESP());
		modules.add(new Cape());
		modules.add(new ESP());
		modules.add(new Fullbright());
		modules.add(new Hotbar());
		modules.add(new TargetHUD());
		modules.add(new Tracers());
		modules.add(new TabGUI());
		modules.add(new Nametags());
        modules.add(clickGui = new ClickGUI());
        modules.add(new BlockOverlay());
        modules.add(new Notifications());
        modules.add(new ItemESP());
        modules.add(new DiscordGUI());
        modules.add(new ItemPhysics());
        modules.add(new Trajectories());
        modules.add(new Keystrokes());
        
        //Ghost
        modules.add(new Eagle());
		modules.add(new AimAssist());
		modules.add(new LeftClickTimer());
		modules.add(new FakeLag());
		modules.add(new SelfDestruct());
		modules.add(new AutoClicker());
		modules.add(new Parkour());
		modules.add(new KeepSprint());
		
		//Exploit
		modules.add(new ClickTP());
		modules.add(new Disabler());
		modules.add(new FastEat());
		modules.add(new Regen());
		modules.add(new NoPacketKick());
		
		//World
		
		//Hidden
		
	}
	
	public static Module getModule(String name) {
		for(Module module : modules) {
			if(module.name.equalsIgnoreCase(name)) {
				return module;
			}
		}
		return null;
    }
	
	public static void onPreMotion(PreMotionEvent event) {

	}
	
	
	public static String requestURLSRC(String starter) throws IOException {
		URL urlObject = new URL(starter);
		URLConnection urlConnection = urlObject.openConnection();
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	
		return APK(urlConnection.getInputStream());
	}
	
	private static String APK(InputStream connection) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection, "UTF-8"))) {
			String inputLine;
			StringBuilder stringBuilder = new StringBuilder();
			while ((inputLine = bufferedReader.readLine()) != null) {
				stringBuilder.append(inputLine);
			}
	
			return stringBuilder.toString();
		}
	}
	
	public static void onEvent(Event e){
		Client.destruct = false;
		if(Client.destruct) {
			for(Module m : Client.getModules()){
				if(m.isEnabled()) {
					m.toggle();
				}
			}
		}
		try {
			if(!Client.destruct) {
				if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
					if (e instanceof EventChat) {
						commandManager.handleChat((EventChat)e);
					}
					
					if (e instanceof EventUpdate && e.isPre()) {
					
						ScaffoldRenderUtils.resetPlayerYaw();
						ScaffoldRenderUtils.resetPlayerPitch();
					}
						
					for(Module m : modules){
						if(!m.toggled)
							continue;
						
						m.onEvent(e);
					}
				}
			}
		}catch(Exception e1){
		}
	}
	
	public static String getName() {
		return name;
	}
	
	public static void noPremiumMSG(String name, String mode) {
		NotificationManager.getNotificationManager().createNotification("Error", name + " " + mode + " is" + ChatFormatting.YELLOW +  " Premium!", true, 1500, Type.WARNING, Color.GREEN);
	}
	
	public static void setName(String name) {
		Client.name = name;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		Client.version = version;
	}

	public static CopyOnWriteArrayList<Module> getModules() {
		return modules;
	}

	public static void setModules(CopyOnWriteArrayList<Module> modules) {
		Client.modules = modules;
	}

	public static HUD getHud() {
		return hud;
	}

	public static void setHud(HUD hud) {
		Client.hud = hud;
	}

	public static Robot robot;
	
	public static void keyPress(int key){
		if(!Client.destruct) {
			Client.onEvent(new EventKey(key));
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
			
			if(key == Keyboard.KEY_PERIOD) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiChat());
				robot.keyPress(KeyEvent.VK_PERIOD);
				robot.keyRelease(KeyEvent.VK_PERIOD);
			}
			
			for(Module m : modules){
				if(m.getKey() == key){
					m.toggle();
				}
			}
		}
	}
		
	public static List<Module> getModulesByCategory(Category c){
		List<Module> modules = new ArrayList<Module>();
		
		for(Module m : Client.modules) {
			if(m.category == c)
				modules.add(m);
		}
		
	return modules;
	}
	
	public static void addChatMessage(String message) {
		if(!Client.destruct) {
			try {
				message = "\2479" + name + "\2477 >> " + message;
			
				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
			}catch(NullPointerException e1){
			}
		}
	}

	public static void onRender() {
		if(!Client.destruct) {
			try {
				for(Module m : getModules()) {
					m.onRender();
				}
			}catch(NullPointerException e1){
				e1.printStackTrace();
			}
		}
	}

    private static final Timer inventoryTimer = new Timer();
	
    public static boolean invCooldownElapsed(long time) {
        return inventoryTimer.hasTimeElapsed(time, true);
    }
}

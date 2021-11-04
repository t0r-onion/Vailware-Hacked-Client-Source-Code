package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import static net.minecraft.client.gui.GuiMainMenu.Handle.*;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import toniqx.AltManager.Alt;
import toniqx.AltManager.GuiAltManager;
import toniqx.vailware.Client;
import toniqx.vailware.font.GlyphPageFontRenderer;
import toniqx.vailware.main.authentication.Translate;
import toniqx.vailware.main.music.radio.gui.RadioGUI;
import toniqx.vailware.main.util.impl.ColorUtil;
import toniqx.vailware.main.util.impl.RenderUtils;
import toniqx.vailware.main.util.impl.render.Draw;
import toniqx.vailware.main.util.impl.render.ParticleUtil;
import toniqx.vailware.main.util.impl.render.RenderUtil;
import toniqx.vailware.modules.Module;
import viamcp.gui.GuiProtocolSelector;

import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback
{
	private final List<String> log;
	
    private static final AtomicInteger field_175373_f = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final Random RANDOM = new Random();
    /** Counts the number of screen updates. */
    private float updateCounter;

    /** The splash message. */
    private String splashText;
    private GuiButton buttonResetDemo;

    /** Timer used to rotate the panorama, increases every tick. */
    private int panoramaTimer;

    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;
    private boolean field_175375_v = true;

    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */
    private final Object threadLock = new Object();

    /** OpenGL graphics card warning. */
    private String openGLWarning1;

    /** OpenGL graphics card warning. */
    private String openGLWarning2;

    /** Link to the Mojang Support about minimum requirements */
    private String openGLWarningLink;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");

    /** An array of all the paths to the panorama pictures. */
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;

    /** Minecraft Realms button. */
    private GuiButton realmsButton;
    
    public GuiMainMenu()
    {
        this.openGLWarning2 = field_96138_a;
        this.splashText = "missingno";
        this.log = new ArrayList<String>();
        BufferedReader bufferedreader = null;
        
        try
        {
            List<String> list = Lists.<String>newArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                s = s.trim();

                if (!s.isEmpty())
                {
                    list.add(s);
                }
            }

            if (!list.isEmpty())
            {
                while (true)
                {
                    this.splashText = (String)list.get(RANDOM.nextInt(list.size()));

                    if (this.splashText.hashCode() != 125780783)
                    {
                        break;
                    }
                }
            }
        }
        catch (IOException var12)
        {
            ;
        }
        finally
        {
            if (bufferedreader != null)
            {
                try
                {
                    bufferedreader.close();
                }
                catch (IOException var11)
                {
                    ;
                }
            }
        }

        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported())
        {
            this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
            this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.panoramaTimer;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    
    public void initGui()
    {
    	//changelog begin

    	//build 7.0
    	this.log.add("§b---Build b7.0---");
        this.log.add(" ");
        this.log.add("§a+ §7Fixed vailware directory location");
        this.log.add("§a+ §7Added pearl fly for hypixel (thanks to tofq)");
        this.log.add("§c- §7Removed radio for now");
    	this.log.add(" ");
    	
    	//build 6.0
    	this.log.add("§b---Build 6.0---");
        this.log.add(" ");
        this.log.add("§a+ §7New MainMenu");
        this.log.add("§a+ §7More ClickGUI Styles");
        this.log.add("§a+ §7Scaffold Bypasses");
        this.log.add("§a+ §7and much more");
        this.log.add("§c- §7Removed Some Stuff");
    	this.log.add(" ");
    	
        
    	//changelog end
    	
    	
    	this.buttonList.add(new GuiButton(0, 14, GuiScreen.height / 4 - 24, 130, 25, "Singleplayer"));
    	this.buttonList.add(new GuiButton(1, 14, GuiScreen.height / 4, 130, 25, "Multiplayer"));
    	this.buttonList.add(new GuiButton(2, 14, GuiScreen.height / 4 + 24, 130, 25, "Accounts"));
    	this.buttonList.add(new GuiButton(3, 14, GuiScreen.height / 4 + 48, 130, 25, "Options"));
    	this.buttonList.add(new GuiButton(4, 14, GuiScreen.height / 4 + 72, 130, 25, "Version"));
    	this.buttonList.add(new GuiButton(5, 14, GuiScreen.height / 4 + 96, 130, 25, "Quit"));
    	Client.getInstance().getVailRPC().update("", "MainMenu");
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
        {
            this.splashText = "Merry X-mas!";
        }
        else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
        {
            this.splashText = "Happy new year!";
        }
        else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31)
        {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int i = 24;
        int j = this.height / 4 + 48;

        if (this.mc.isDemo())
        {
            this.addDemoButtons(j, 24);
        }
        else
        {
            this.addSingleplayerMultiplayerButtons(j, 24);
        }
        
        synchronized (this.threadLock)
        {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int k = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - k) / 2;
            this.field_92020_v = this.field_92022_t + k;
            this.field_92019_w = this.field_92021_u + 24;
        }

        this.mc.func_181537_a(false);
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_)
    {
        
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int p_73972_1_, int p_73972_2_)
    {
    	mc.shutdown();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (button.id == 1)
        {
        	mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 2)
        {
        	mc.displayGuiScreen(new GuiAltManager());
        }
        if (button.id == 3)
        {
        	mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if (button.id == 4)
        {
        	mc.displayGuiScreen(new GuiProtocolSelector(this));
        }
        if (button.id == 5)
        {
        	mc.shutdown();
        }
    }

    private void switchToRealms()
    {
        RealmsBridge realmsbridge = new RealmsBridge();
        realmsbridge.switchToRealms(this);
    }

    public void confirmClicked(boolean result, int id)
    {
        if (result && id == 12)
        {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        }
        else if (id == 13)
        {
            if (result)
            {
                try
                {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {new URI(this.openGLWarningLink)});
                }
                catch (Throwable throwable)
                {
                    logger.error("Couldn\'t open link", throwable);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    
    public static double roundToDecimalPlace(double value, double inc) {
        final double halfOfInc = inc / 2.0D;
        final double floored = Math.floor(value / inc) * inc;
        if (value >= floored + halfOfInc)
            return new BigDecimal(Math.ceil(value / inc) * inc, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
        else
            return new BigDecimal(floored, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }
    
    public Translate translate = new Translate(0f, 0f);
    
    ScaledResolution sr = new ScaledResolution(mc);
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glColor4f(1, 1, 1, 1);
        
    	int offset = 16;
    	int ButtonColor = Client.color;
    	int ButtonHighlightColor = 0x20FFFFFF;
    	int BackgroundColor = 0xF9222326;
    	int OutlineColor = ColorUtil.getRainbow(8, 0.75f, 1);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("vail/mainmenu.png"));
    	Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, GuiScreen.width,  GuiScreen.height,  GuiScreen.width, GuiScreen.height,  GuiScreen.width,  GuiScreen.height);
        Gui.drawRect(0, 0, 160, GuiScreen.height, 0x70000000);

    	Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String strDate = dateFormat.format(date);

        RenderUtils.glColor(0xAD000000);
        Draw.drawRoundedRect(GuiScreen.width / 2 + 40, GuiScreen.height / 4 - 40, 240, 350, 3);
        
    	mc.FontRendererNormal.drawStringWithShadow(strDate, GuiScreen.width - mc.FontRendererNormal.getStringWidth(strDate) - 10, 2, -1);
    	mc.FontRendererLarge.drawStringWithShadow("Build - " + Client.version, GuiScreen.width - mc.FontRendererLarge.getStringWidth("Build - " + Client.version) - 6, GuiScreen.height - 20, -1);
        
        RenderUtils.glColor(0x30000000);
        Draw.drawRoundedRect(GuiScreen.width / 2 + 40, GuiScreen.height / 4 + 6, 240, 304, 3);
        
    	mc.FontRendererHuge.drawCenteredString("Welcome " + Client.username, GuiScreen.width / 2 + 157, GuiScreen.height / 4 - 38, -1);
    	mc.FontRendererLargeBold.drawCenteredString(ChatFormatting.BOLD + "Changelog", GuiScreen.width / 2 + 152, GuiScreen.height / 4 - 18, -1);
    	
    	int logY = 140;
    	GL11.glEnable(GL11.GL_SCISSOR_TEST);
    	
        for (final String text : this.log) {
        	RenderUtils.scissor(GuiScreen.width / 2 + 40, GuiScreen.height / 4 + 6, 240, 304);
       		
        	mc.FontRendererNormal.drawString(text, GuiScreen.width / 2 + 50, logY + Client.changelogOffset, -1, false);
            logY += mc.FontRendererArray.FONT_HEIGHT;
        }
        
    	GL11.glDisable(GL11.GL_SCISSOR_TEST);
    	
    	handle(mouseX, mouseY, -1, DRAW);
    	ScaledResolution scaledRes = new ScaledResolution(this.mc);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 274;
        int j = this.width / 2 - i / 2;
        int k = 30;

        if (this.mc.isDemo())
        {
        	System.out.println("LLLLLLL.... You thought!");
            this.mc.shutdown();
        }
        
        mc.FontRendererHuge.drawCenteredString(Client.name, 74, GuiScreen.width / 92, -1);
        
		String clientName = Client.name;
		String splitText = clientName.charAt(0) + "\247f" + clientName.substring(1);
        
        if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0)
        {
            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.field_92024_r) / 2, ((GuiButton)this.buttonList.get(0)).yPosition - 12, -1);
        }
        
        int width = (int) mc.FontRendererLarge.getStringWidth("Welcome " + Client.username);

        translate.animate(GuiScreen.width / 2 - width - (130), 1);

        mc.FontRendererLarge.drawCenteredString("Welcome " + Client.username, (float) (GuiScreen.width / 4 + translate.getX() - 390), GuiScreen.height / 4 + 360, -1);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    	
    }
    
    int TextColor = 0xFFFFFFFF;
    
    public void handle(int mouseX, int mouseY, int button, Handle type) {

    	boolean hoveringChangelogBox = isHovered(GuiScreen.width / 2 + 40, GuiScreen.height / 4, GuiScreen.width / 2 + 280, GuiScreen.height / 2 + 178, mouseX, mouseY);

    	if (hoveringChangelogBox && Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (wheel < 0) {
            	Client.changelogOffset -= 18;
            } else if (wheel > 0) {
            	if(Client.changelogOffset < 0) {
            		Client.changelogOffset += 18;
            	}
            }
    	}
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	handle(mouseX, mouseY, mouseButton, CLICK);
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized (this.threadLock)
        {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w)
            {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }
    
    public void mouseReleased(int mouseX, int mouseY, int button) {
        handle(mouseX, mouseY, button, RELEASE);
    }
    
    public boolean isHovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
        return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
    }
    
    public enum Handle {
        DRAW,
        CLICK,
        RELEASE
    }
}

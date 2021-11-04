package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.IChatComponent;
import toniqx.AltManager.Alt;
import toniqx.vailware.Client;
import toniqx.vailware.main.music.radio.gui.RadioGUI;
import toniqx.vailware.main.util.impl.TimerUtils;
import toniqx.vailware.modules.Module;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
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
    
    
    public static int reconnectTimer;
    
    public void initGui()
    {
    	reconTimer = 30;
        int i = 0;
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 24, I18n.format("Generate Cracked Account", new Object[0])));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 48, I18n.format("Reconnect", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (button.id == 4)
        {
            Alt.genCracked();
        }
        if (button.id == 5)
        {
        	reconnect();
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    
    public static void reconnect() {
    	mc.displayGuiScreen(new GuiConnecting(null, mc, mc.clientLastServer));
    }
    
    
    public int reconTimer;
    public TimerUtils timer = new TimerUtils();
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	
    	if(timer.hasReached(1000) && reconTimer > 0) {
    		reconTimer--;
    		timer.reset();
    	}
    	if(reconTimer == 0) {
    		reconnect();
    	}
    	
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null)
        {
            for (String s : this.multilineMessage)
            {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    	mc.FontRendererLarge.drawCenteredString("Auto-Reconnect in : " + reconTimer + "", GuiScreen.width / 2, 20, -1);
    	mc.FontRendererTiny.drawString("Logged into " + ChatFormatting.GREEN + this.mc.session.getUsername(), 3, 3, -1, false);
    }
}

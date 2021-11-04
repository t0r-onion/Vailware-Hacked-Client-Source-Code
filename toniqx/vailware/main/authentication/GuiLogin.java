package toniqx.vailware.main.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import toniqx.vailware.Client;

public class GuiLogin extends GuiScreen {

	public static GuiTextField hwid;
    private long initTime = System.currentTimeMillis();

    public String testString;
    public String testString2;
    private boolean switchmac = false;
	
	@Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);
        Keyboard.enableRepeatEvents(true);
        this.hwid = new GuiTextField(123123, this.mc.fontRendererObj, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 - 20, 200, 20);
        hwid.setMaxStringLength(100);
        this.buttonList.add(new GuiButton(1001, sr.getScaledWidth() / 2 - 100, sr.getScaledHeight() / 2 + 20, "Login"));
    }
	
	@Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
        case 1001:
        	try {
        		String[] strings = this.requestURLSRC("https://pastebin.com/raw/gZM092FH").split("!");
        		String[] strings2 = this.requestURLSRC("https://pastebin.com/raw/BkiLDi7Z").split("!");
        		
        		boolean contains = false;
        		
        		testString = strings.toString();

        		for (String s : strings) {
        			if (s.equalsIgnoreCase(Hwid.getHWID() + ":" + hwid.getText())) {

        				Client.username = hwid.getText();
        				
        				mc.displayGuiScreen(new GuiMainMenu());
        				
        				
        				
        			} else {
        				
        				Client.username = hwid.getText();
        				
        				mc.displayGuiScreen(new GuiMainMenu());
        				
        				
        			}
        		}
        		
        		testString2 = strings2.toString();

        		for (String s : strings2) {
        			if (s.equalsIgnoreCase(Hwid.getHWID() + ":" + Client.username + ":" + "premium")) {

        				Client.premium = true;
        			
        			}
        		}

        		
        	} catch (Exception e) {
        		e.printStackTrace();
        		mc.shutdownMinecraftApplet();


        	}
        	break;
        }
	}
	
	@Override
    public void drawScreen(int x2, int y2, float z2) {
        GL11.glColor4f(1, 1, 1, 1);

        ScaledResolution sr = new ScaledResolution(mc);

        this.mc.getTextureManager().bindTexture(new ResourceLocation("vail/mainmenu.png"));
        Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, GuiScreen.width,  GuiScreen.height,  GuiScreen.width, GuiScreen.height,  GuiScreen.width,  GuiScreen.height);

        hwid.drawTextBox();
        
        mc.FontRendererHuge.drawCenteredString("Welcome " + hwid.getText() + "|", GuiScreen.width / 2, GuiScreen.height / 4 - 50, Client.color);
        
        super.drawScreen(x2, y2, z2);
    }

    @Override
    public void mouseClicked(int x2, int y2, int z2) {
        try {
            super.mouseClicked(x2, y2, z2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        hwid.mouseClicked(x2, y2, z2);
    }

    @Override
    protected void keyTyped(char character, int key) {
        hwid.textboxKeyTyped(character, key);
        if (character == '\r') {
            this.actionPerformed((GuiButton) this.buttonList.get(0));
        }
    }
    
    protected String requestURLSRC(String starter) throws IOException {
		URL urlObject = new URL(starter);
		URLConnection urlConnection = urlObject.openConnection();
		urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	
		return APK(urlConnection.getInputStream());
	}
	
    protected String APK(InputStream connection) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection, "UTF-8"))) {
			String inputLine;
			StringBuilder stringBuilder = new StringBuilder();
			while ((inputLine = bufferedReader.readLine()) != null) {
				stringBuilder.append(inputLine);
			}
	
			return stringBuilder.toString();
		}
	}
}

package toniqx.vailware.main.music.radio.gui;

import java.io.IOException;
import java.net.URI;

import org.lwjgl.input.Keyboard;

import javazoom.jl.player.Player;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import toniqx.vailware.main.command.commands.Radio;
import toniqx.vailware.main.music.radio.RadioManager;

public class RadioGUI extends GuiScreen {
	
	private GuiScreen previous;
	private GuiTextField nameOrId;
	private GuiTextField name;
    private Player player;
    public static RadioManager radio = new RadioManager();
	public static String URL = "http://www.cedarhome.org/mp3/songs/no%20one%20can%20stand.mp3";
	
	public RadioGUI(GuiScreen previous) {
		this.previous = previous;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(12, this.width / 2, this.height / 5 + 60, 136, 22, "Stop"));
		this.buttonList.add(new GuiButton(11, this.width / 2 - 138, this.height / 5 + 60, 136, 22, "Play"));
		
		this.buttonList.add(new GuiButton(0, GuiScreen.width - 136, 0, 136, 22, "MONSTERCAT"));
		this.buttonList.add(new GuiButton(1, GuiScreen.width - 136, 21, 136, 22, "CHILLHOP"));
		this.buttonList.add(new GuiButton(8, GuiScreen.width - 136, 42, 136, 22, "HIP HOP"));
		this.buttonList.add(new GuiButton(3, GuiScreen.width - 136, 63, 136, 22, "DANCE"));
		this.buttonList.add(new GuiButton(4, GuiScreen.width - 136, 84, 136, 22, "CHILL"));
		this.buttonList.add(new GuiButton(5, GuiScreen.width - 136, 105, 136, 22, "XMAS"));
		this.buttonList.add(new GuiButton(6, GuiScreen.width - 136, 126, 136, 22, "CLUB"));
		this.buttonList.add(new GuiButton(7, GuiScreen.width - 136, 147, 136, 22, "RAP"));
		
		this.buttonList.add(new GuiButton(2, GuiScreen.width / 2 - 150 / 2, this.height / 6 + 100, 146, 22, I18n.format("gui.done", new Object[0])));(this.nameOrId = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, this.height / 5 + 30, 200, 20)).setText("");
		nameOrId.setMaxStringLength(Integer.MAX_VALUE);
		nameOrId.setText("");
		
	}

	@Override
	public void updateScreen() {
		this.nameOrId.updateCursorCounter();
		
		
	
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 11){
			
			URL = nameOrId.getText();
			
			if(!URL.isEmpty() && URL.contains("streams.ilovemusic.de")) {
				radio.setStream(new java.net.URL(URL).openStream());
				radio.start();
			}
			
		}
		if (button.id == 0){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio24.mp3").openStream());
			radio.start();
		}
		if (button.id == 1){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio17.mp3").openStream());
			radio.start();
		}
		if (button.id == 8){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio3.mp3").openStream());
			radio.start();
		}
		if (button.id == 3){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio2.mp3").openStream());
			radio.start();
		}
		if (button.id == 4){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio1.mp3").openStream());
			radio.start();
		}
		if (button.id == 5){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio8.mp3").openStream());
			radio.start();
		}
		if (button.id == 6){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio20.mp3").openStream());
			radio.start();
		}
		if (button.id == 7){
			radio.stop();
			radio.setStream(new java.net.URL("https://streams.ilovemusic.de/iloveradio13.mp3").openStream());
			radio.start();
		}
		if (button.id == 12){
			
			radio.stop();
			
		}
		if (button.id == 2){
			this.mc.displayGuiScreen(this.previous);

		}
	}
	public static String withColors(String identifier, String input) {
		String output = input;
		int index = output.indexOf(identifier);
		while (output.indexOf(identifier) != -1) {
			output = output.replace(identifier, "\247");
			index = output.indexOf(identifier);
		}
		return output;
	}



	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.nameOrId.textboxKeyTyped(typedChar, keyCode);
		
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.nameOrId.mouseClicked(mouseX, mouseY, mouseButton);
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		this.nameOrId.drawTextBox();
		
		
		mc.FontRendererLarge.drawCenteredString("URL", this.width / 2,
				this.nameOrId.yPosition - 20, -1);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}

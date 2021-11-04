package toniqx.vailware.main.util.impl.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import toniqx.vailware.main.util.impl.ColorUtil;

public class ParticleUtil {

    private final List<Particle> particles;
    private int width, height, count;

    public ParticleUtil(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.count = 100;
        this.particles = new ArrayList<Particle>();
        for (int count = 0; count <= this.count; ++count) {
            this.particles.add(new Particle(new Random().nextInt(width), new Random().nextInt(height)));
        }
    }

    public void drawParticles() {
        this.particles.forEach(particle -> particle.drawParticle());
    }

    public class Particle {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        float xPos;
		private float yPos;

        public Particle(final int xPos, final int yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public void drawParticle() {
            this.xPos += new Random().nextInt(1);
            this.yPos += new Random().nextInt((int) 10f);
            final float particleSize = 0.4f;

            if(this.xPos > ParticleUtil.this.width) {
                this.xPos = -particleSize;
            }

            if(this.yPos > ParticleUtil.this.height) {
                this.yPos = -particleSize;
            }

            Gui.drawRect(this.xPos, this.yPos, this.xPos + particleSize, this.yPos + particleSize, ColorUtil.astolfoColors(6, 100));
        }

    }


}
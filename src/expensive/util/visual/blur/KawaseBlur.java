package expensive.util.visual.blur;

import expensive.util.visual.main.display.CustomFramebuffer;
import expensive.util.visual.components.Stencil;
import expensive.util.visual.shader.main.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class KawaseBlur {

    public static KawaseBlur blur = new KawaseBlur();

    public final CustomFramebuffer BLURRED;
    public final CustomFramebuffer ADDITIONAL;
    CustomFramebuffer blurTarget = new CustomFramebuffer(false).setLinear();

    public KawaseBlur() {
        BLURRED = new CustomFramebuffer(false).setLinear();
        ADDITIONAL = new CustomFramebuffer(false).setLinear();
       
    }
    public void render(Runnable run) {

        Stencil.initStencilToWrite();
        run.run();
        Stencil.readStencilBuffer(1);
         BLURRED.draw();
        Stencil.uninitStencilBuffer();
    }
    public void updateBlur(float offset, int steps) {

        Minecraft mc = Minecraft.getInstance();
        Framebuffer mcFramebuffer = mc.getFramebuffer();
        ADDITIONAL.setup();
        mcFramebuffer.bindFramebufferTexture();
        ShaderUtil.kawaseDown.attach();
        ShaderUtil.kawaseDown.setUniform("offset", offset);
        ShaderUtil.kawaseDown.setUniformf("resolution", 1f / mc.getMainWindow().getWidth(),
                1f / mc.getMainWindow().getHeight());
        CustomFramebuffer.drawTexture();
        CustomFramebuffer[] buffers = {this.ADDITIONAL, this.BLURRED };
        for (int i = 1; i < steps; ++i) {
            int step = i % 2;
            buffers[step].setup();
            buffers[(step + 1) % 2].draw();
        }
        ShaderUtil.kawaseUp.attach();
        ShaderUtil.kawaseUp.setUniform("offset", offset);
        ShaderUtil.kawaseUp.setUniformf("resolution", 1f / mc.getMainWindow().getWidth(),
                1f / mc.getMainWindow().getHeight());
        for (int i = 0; i < steps; ++i) {
            int step = i % 2;
            buffers[(step + 1) % 2].setup();
            buffers[step].draw();
        }
        ShaderUtil.kawaseUp.detach();
        mcFramebuffer.bindFramebuffer(false);
    }

}

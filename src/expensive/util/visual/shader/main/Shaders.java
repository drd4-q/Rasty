package expensive.util.visual.shader.main;

import expensive.util.visual.shader.main.shaders.AlphaGlsl;
import expensive.util.visual.shader.main.shaders.ContrastGlsl;
import expensive.util.visual.shader.main.shaders.FontGlsl;
import expensive.util.visual.shader.main.shaders.GaussianBloomGlsl;
import expensive.util.visual.shader.main.shaders.KawaseDownGlsl;
import expensive.util.visual.shader.main.shaders.KawaseUpGlsl;
import expensive.util.visual.shader.main.shaders.MaskGlsl;
import expensive.util.visual.shader.main.shaders.OutlineGlsl;
import expensive.util.visual.shader.main.shaders.VertexGlsl;
import expensive.util.visual.shader.main.shaders.WhiteGlsl;
import lombok.Getter;
import expensive.util.visual.shader.main.shaders.RoundedGlsl;
import expensive.util.visual.shader.main.shaders.RoundedOutGlsl;
import expensive.util.visual.shader.main.shaders.SmoothGlsl;

public class Shaders {
    @Getter
    private static Shaders Instance = new Shaders();
    @Getter
    private IShader font = new FontGlsl();
    @Getter
    private IShader vertex = new VertexGlsl();
    @Getter
    private IShader rounded = new RoundedGlsl();
    @Getter
    private IShader roundedout = new RoundedOutGlsl();
    @Getter
    private IShader smooth = new SmoothGlsl();
    @Getter
    private IShader white = new WhiteGlsl();
    @Getter
    private IShader alpha = new AlphaGlsl();
    @Getter
    private IShader gaussianbloom = new GaussianBloomGlsl();
    @Getter
    private IShader kawaseUp = new KawaseUpGlsl();
    @Getter
    private IShader kawaseDown = new KawaseDownGlsl();
    @Getter
    private IShader outline = new OutlineGlsl();
    @Getter
    private IShader contrast = new ContrastGlsl();
    @Getter
    private IShader mask = new MaskGlsl();
}

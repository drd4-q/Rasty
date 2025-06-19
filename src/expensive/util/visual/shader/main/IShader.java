package expensive.util.visual.shader.main;

public interface IShader {

    String glsl();

    default String getName() {
        return "SHADERNONAME";
    }

}

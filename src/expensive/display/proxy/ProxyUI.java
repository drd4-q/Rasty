package expensive.display.proxy;

import com.mojang.blaze3d.matrix.MatrixStack;
import expensive.main.proxy.ProxyConnection;
import expensive.main.proxy.ProxyType;
import expensive.util.visual.main.fonts.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.net.InetSocketAddress;
import java.util.Locale;

public class ProxyUI extends Screen {

    ProxyConnection pc = new ProxyConnection();
    private TextFieldWidget proxy;


    public ProxyUI() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        super.init();

        float[] center = {width / 2f, height / 2f};

        this.addButton(new Button((int) center[0] - 100 - 5, (int) center[1] + 30, 100, 20, new TranslationTextComponent("Apply"), (ppp) ->
                parse(proxy.getText())));

        this.addButton(new Button((int) center[0] + 5, (int) center[1] + 30, 100, 20, new TranslationTextComponent("Back"), (ppp) ->
                Minecraft.getInstance().displayGuiScreen(new MultiplayerScreen(null))
        ));

        this.proxy = new TextFieldWidget(this.font, (int) center[0] - 100, (int) center[1] - 10, 200, 20, new TranslationTextComponent("��� ������"));
        this.proxy.setMaxStringLength(32);
        this.children.add(this.proxy);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        String cProxy = pc.getProxyAddr() != null ? pc.getProxyType().name().toLowerCase(Locale.ROOT) + "://" + pc.getProxyAddr().getHostString() + ":" + pc.getProxyAddr().getPort() : "��� ������";

        float[] center = {width / 2f, height / 2f};

        Fonts.sfbold.drawCenteredText(matrixStack, "Active proxies: " + cProxy, center[0], center[1] - 26, -1, 10);

        Fonts.sfbold.drawCenteredText(matrixStack, "Example: socks4://123.123.123.123:1234", center[0], center[1] + 60, -1, 10);

        proxy.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void parse(String input) {
        input = input.toLowerCase(Locale.ROOT);

        try {
            ProxyType type = input.startsWith("http://") ? ProxyType.HTTP : input.startsWith("socks4://") ? ProxyType.SOCKS4 : input.startsWith("socks5://") ? ProxyType.SOCKS5 : ProxyType.DIRECT;
            String addr = input.split("//")[1];

            pc.setup(type, new InetSocketAddress(addr.split(":")[0], Integer.parseInt(addr.split(":")[1])));
        } catch (Exception e) {
            pc.reset();
        }
    }

    @Override
    public void tick() {
        super.tick();

        proxy.tick();
    }
}

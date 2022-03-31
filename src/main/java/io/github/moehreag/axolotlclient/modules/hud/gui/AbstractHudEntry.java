package io.github.moehreag.axolotlclient.modules.hud.gui;

import io.github.moehreag.axolotlclient.config.options.BooleanOption;
import io.github.moehreag.axolotlclient.config.options.DoubleOption;
import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.util.Color;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawPosition;
import io.github.moehreag.axolotlclient.modules.hud.util.DrawUtil;
import io.github.moehreag.axolotlclient.modules.hud.util.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHudEntry extends DrawUtil {

    public int width;
    public int height;

    protected BooleanOption enabled = new BooleanOption("enabled",false);
    public DoubleOption scale = new DoubleOption("scale", 1, 0.1F, 2);
    //protected KronColor textColor = new KronColor("textcolor", null, "#FFFFFFFF");
    protected BooleanOption shadow = new BooleanOption("shadow",  getShadowDefault());
    protected BooleanOption background = new BooleanOption("background",  true);
    //protected KronColor backgroundColor = new KronColor("backgroundcolor", null, "#64000000");
    private DoubleOption x = new DoubleOption("x", getDefaultX(), 0, 1);
    private DoubleOption y = new DoubleOption("y", getDefaultY(), 0, 1);

    private List<Option> options;

    protected boolean hovered = false;
    protected MinecraftClient client = MinecraftClient.getInstance();
    protected Window window;


    public AbstractHudEntry(int width, int height) {
        this.width = width;
        this.height = height;
        //window=new Window(client);
    }

    public static int floatToInt(float percent, int max, int offset) {
        return MathHelper.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathHelper.clamp((float) (current) / (max - offset), 0, 1);
    }

    public void renderHud() {
        render();
    }

    public abstract void render();

    public abstract void renderPlaceholder();

    public void renderPlaceholderBackground() {
        if (hovered) {
            fillRect(getScaledBounds(), Color.SELECTOR_BLUE);
        } else {
            fillRect(getScaledBounds(), Color.WHITE);
        }
        outlineRect( getScaledBounds(), Color.BLACK);
    }

    public abstract Identifier getId();

    public abstract boolean movable();

    public boolean tickable() {
        return false;
    }

    public void tick() {
    }

    public void setXY(int x, int y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return getScaledPos().x;
    }

    public void setX(int x) {
        this.x.set(intToFloat(x, (int)new Window(client).getScaledWidth(),
                Math.round(width * getScale())));
    }

    public int getY() {
       return getScaledPos().y;
    }

    public void setY(int y) {
        this.y.set(intToFloat(y, (int) new Window(client).getScaledHeight(),
                Math.round(height * getScale())));
    }

    protected double getDefaultX() {
        return 0;
    }

    protected float getDefaultY() {
        return 0;
    }

    protected boolean getShadowDefault() {
        return true;
    }

    public Rectangle getScaledBounds() {
        return new Rectangle(getX(), getY(), Math.round(width * (float) scale.get()),
                Math.round(height * (float) scale.get()));
    }

    /**
     * Gets the hud's bounds when the matrix has already been scaled.
     * @return The bounds.
     */
    public Rectangle getBounds() {
        return new Rectangle(getPos().x, getPos().y, width, height);
    }

    public float getScale() {
        return (float) scale.get();
    }

    public void scale() {

    }

    public DrawPosition getPos() {
        return getScaledPos().divide(getScale());
    }

    public DrawPosition getScaledPos() {
        return getScaledPos(getScale());
    }

    public DrawPosition getScaledPos(float scale) {
        int scaledX = floatToInt((float) x.get(), (int) new Window(client).getScaledWidth(),
                Math.round(width * scale));
        int scaledY = floatToInt((float) y.get(), (int) new Window(client).getScaledHeight(),
                Math.round(height * scale));
        return new DrawPosition(scaledX, scaledY);
    }

    /*public List<GuiConfigsBase.ConfigOptionWrapper> getOptionWrappers() {
        return GuiConfigsBase.ConfigOptionWrapper.createFor(getOptions());
    }*/

    public List<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
            addConfigOptions(options);
        }
        return options;
    }

    public List<Option> getAllOptions() {
        List<Option> options = new ArrayList<>(getOptions());
        options.add(x);
        options.add(y);
        return options;
    }

    public void addConfigOptions(List<Option> options) {
        options.add(enabled);
        options.add(scale);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public String getNameKey() {
        return "hud." + getId().getNamespace() + "." + getId().getPath();
    }

    public String getName() {
        return I18n.translate(getNameKey());
    }

    public void toggle() {
        enabled.toggle();
    }

    public void setHovered(boolean value) {
        hovered=value;
    }
}
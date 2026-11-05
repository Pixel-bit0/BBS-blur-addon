package dev.bbsblur.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Full-screen blur pass. Runs right after the world is rendered (before the HUD),
 * driven by whatever the Blur clip submitted to {@link BlurState}.
 */
public final class BlurRenderer
{
    /** Set by CoreShaderRegistrationCallback once the shader is loaded. */
    public static ShaderProgram program;

    private static SimpleFramebuffer copy;

    private static final Matrix4f VIEW = new Matrix4f();
    private static final Matrix4f PROJECTION = new Matrix4f();
    private static Vec3d cameraPos = Vec3d.ZERO;
    private static boolean haveMatrices;

    private BlurRenderer() {}

    /** Called from WorldRendererMixin with the matrices the world is actually drawn with. */
    public static void captureMatrices(Matrix4f modelView, Matrix4f projection, Vec3d camera)
    {
        VIEW.set(modelView);
        PROJECTION.set(projection);
        cameraPos = camera;
        haveMatrices = true;
    }

    public static void render()
    {
        BlurState.Snapshot state = BlurState.active();

        if (state == null || program == null || state.amount() <= 0F || state.strength() <= 0F)
        {
            return;
        }

        float[] rect = new float[] {0F, 0F, 0F, 0F};

        if (state.mode() == BlurState.ACTOR)
        {
            if (!state.hasActor() || !haveMatrices || !projectActor(state, rect))
            {
                return;
            }
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        Framebuffer main = mc.getFramebuffer();
        int w = main.textureWidth;
        int h = main.textureHeight;

        if (w <= 0 || h <= 0)
        {
            return;
        }

        if (copy == null || copy.textureWidth != w || copy.textureHeight != h)
        {
            if (copy != null)
            {
                copy.delete();
            }

            copy = new SimpleFramebuffer(w, h, false, MinecraftClient.IS_SYSTEM_MAC);
            copy.setTexFilter(GL11.GL_LINEAR);
        }

        /* Snapshot the finished frame so the shader can sample it while writing back to the same target */
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.fbo);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, copy.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, w, h, 0, 0, w, h, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        main.beginWrite(false);

        float scale = h / 1080F;

        set("Params0", state.mode(), state.strength() * scale, state.radius(), state.softness());
        set("Params1", 0.5F, 0.5F, (float) w / (float) h, state.amount());
        set("Params2", rect[0], rect[1], rect[2], rect[3]);
        set("Params3", w, h, Math.max(1F, state.softness() * h * 0.08F), 0F);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, copy.getColorAttachment());
        RenderSystem.setShader(() -> program);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        buffer.vertex(-1, -1, 0).next();
        buffer.vertex(1, -1, 0).next();
        buffer.vertex(1, 1, 0).next();
        buffer.vertex(-1, 1, 0).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void set(String name, float a, float b, float c, float d)
    {
        GlUniform uniform = program.getUniform(name);

        if (uniform != null)
        {
            uniform.set(a, b, c, d);
        }
    }

    /**
     * Projects the actor's box to a screen-space rectangle in uv (0..1).
     * @return false when the actor is completely behind the camera.
     */
    private static boolean projectActor(BlurState.Snapshot s, float[] out)
    {
        double hw = s.actorHalfWidth();
        double minX = Float.MAX_VALUE;
        double minY = Float.MAX_VALUE;
        double maxX = -Float.MAX_VALUE;
        double maxY = -Float.MAX_VALUE;
        int visible = 0;
        boolean behind = false;
        Vector4f v = new Vector4f();

        for (int i = 0; i < 8; i++)
        {
            double x = s.actorX() + ((i & 1) == 0 ? -hw : hw) - cameraPos.x;
            double y = s.actorY() + ((i & 2) == 0 ? 0 : s.actorHeight()) - cameraPos.y;
            double z = s.actorZ() + ((i & 4) == 0 ? -hw : hw) - cameraPos.z;

            v.set((float) x, (float) y, (float) z, 1F);
            VIEW.transform(v);
            PROJECTION.transform(v);

            if (v.w <= 0.001F)
            {
                behind = true;
                continue;
            }

            double nx = v.x / v.w * 0.5 + 0.5;
            double ny = v.y / v.w * 0.5 + 0.5;

            minX = Math.min(minX, nx);
            maxX = Math.max(maxX, nx);
            minY = Math.min(minY, ny);
            maxY = Math.max(maxY, ny);
            visible++;
        }

        if (visible == 0)
        {
            return false;
        }

        if (behind)
        {
            /* Camera is inside or right next to the actor: keep the whole screen sharp */
            out[0] = -1F;
            out[1] = -1F;
            out[2] = 2F;
            out[3] = 2F;

            return true;
        }

        out[0] = (float) minX;
        out[1] = (float) minY;
        out[2] = (float) maxX;
        out[3] = (float) maxY;

        return true;
    }
}

package org.jrgss.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Data;
import lombok.ToString;
import org.jrgss.JRGSSLogger;
import static org.jrgss.JRGSSLogger.LogLevels.*;

/**
 * Created by matty on 6/27/14.
 */
@Data
@ToString(callSuper = true)
public class Plane extends Sprite {


    public Plane() {
        super();
    }

    public Plane(Viewport viewport) {
        super(viewport);
    }

    @Override
    public void render(SpriteBatch _) {
        if (bitmap != null && visible && opacity > 0 && (viewport == null || viewport.isVisible())) {
            JRGSSLogger.println(PEDANTIC, String.format("Rendering: %s, %d, %d, %d, %d", viewport, x, y, ox, oy));

            batch.enableBlending();
            if(viewport != null) viewport.begin(batch);
            int viewportX = viewport == null?0:(viewport.rect.x - viewport.ox);
            int viewportY = viewport == null?0:(viewport.rect.y - viewport.oy);
            int viewportWidth = viewport == null?Graphics.getWidth():viewport.rect.getWidth();
            int viewportHeight = viewport == null?Graphics.getHeight():viewport.rect.getHeight();
            com.badlogic.gdx.graphics.Color gdxBlend = color.toGDX();
            batch.setColor(1f , 1f, 1f, (opacity / 255f));

            getAlphaBlendingShader().begin();
            getAlphaBlendingShader().setUniformf("blend_color", gdxBlend.r, gdxBlend.g, gdxBlend.b, gdxBlend.a);
            setShaderTone(tone);
            switch (blend_type) {
                case 0:
                    Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    break;
                case 1:
                    Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                    break;
                case 2:
                    Gdx.gl.glBlendEquation(GL20.GL_FUNC_REVERSE_SUBTRACT);
                    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
                    break;
            }
            int tileWidth = (int)(src_rect.getWidth()*zoom_x);
            int tileHeight = (int)(src_rect.getHeight()*zoom_y);

            int startX = (viewportX - ox);
            int startY = (viewportY - oy);





            batch.begin();
            for(int x = startX; x < viewportWidth + viewportX; x+=tileWidth) {
                for(int y = startY; y < viewportHeight + viewportY; y+=tileHeight) {
                    bitmap.render(batch, x, y,
                            (int)(src_rect.getWidth()*zoom_x),
                            (int)(src_rect.getHeight()*zoom_y),
                            src_rect);
                }
            }
            for(int x = startX; x > -tileWidth; x-=tileWidth) {
                for(int y = startY; y > -tileHeight; y-=tileHeight) {
                    if(x == startX && y == startY) continue;
                    bitmap.render(batch, x, y,
                            (int)(src_rect.getWidth()*zoom_x),
                            (int)(src_rect.getHeight()*zoom_y),
                            src_rect);
                }
            }



            batch.end();
            Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
            getAlphaBlendingShader().end();
            if(viewport != null) viewport.end();
        }
    }

}

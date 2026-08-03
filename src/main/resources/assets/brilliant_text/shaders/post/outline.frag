#version 120

uniform sampler2D u_texture;
uniform vec4 u_outlineColor;
uniform vec4 u_wiperColor;
uniform vec4 u_glowColor;
uniform vec4 u_textColor;
uniform vec2 u_scaledScreenSize;
uniform vec2 u_stringTopLeft;
uniform vec2 u_stringBottomRight;
uniform int u_wiperSlowdown;
uniform int u_time;

int wiperWidth = 5;

bool isTextAtRelative(vec2 position) {
    vec2 offset = position / u_scaledScreenSize;
    vec4 texColor = texture2D(u_texture, gl_TexCoord[0].st + offset);
    return texColor.a != 0.;
}

void main() {
    vec4 texColor = texture2D(u_texture, gl_TexCoord[0].st);

    if (texColor.a == 0.) {
        bool aboveText = isTextAtRelative(vec2(0., 1.));
        bool rightText = isTextAtRelative(vec2(1., 0.));
        bool belowText = isTextAtRelative(vec2(0., -1.));
        bool leftText = isTextAtRelative(vec2(-1., 0.));

        if (aboveText || rightText || belowText || leftText) {
            // Only draw the outline if it will be visible
            if (u_outlineColor.a != 0.) {
                gl_FragColor = u_outlineColor;
                return;
            }
        }

        vec2 pixelPos = vec2(gl_TexCoord[0].s, 1.0 - gl_TexCoord[0].t) * u_scaledScreenSize;
        if (pixelPos.x >= u_stringTopLeft.x && pixelPos.x <= u_stringBottomRight.x &&
            pixelPos.y >= u_stringTopLeft.y && pixelPos.y <= u_stringBottomRight.y) {

            vec2 center = (u_stringTopLeft + u_stringBottomRight) / 2.0;
            vec2 halfSize = (u_stringBottomRight - u_stringTopLeft) / 2.0;

            // Calculate distance from center, normalized so the edges equal 1.0
            vec2 normalizedDiff = (pixelPos - center) / halfSize;
            float dist = length(normalizedDiff);

            // Alpha decreases as distance from the center increases
            float alpha = clamp(1.0 - dist, 0.0, 1.0);

            if (alpha > 0.0) {
                gl_FragColor = vec4(u_glowColor.rgb, clamp(u_glowColor.a * alpha, 0, 0.8));
                return;
            }
        }

        gl_FragColor = vec4(0.);
        return;
    }

    // Do a screen wiper effect on the text
    if (u_wiperColor.a > 0.) {
        vec2 pixelPos = vec2(gl_TexCoord[0].s, 1.0 - gl_TexCoord[0].t) * u_scaledScreenSize;
        vec2 textureSize = u_stringBottomRight - u_stringTopLeft;
        float delta = mod(u_time / u_wiperSlowdown, textureSize.x + wiperWidth * 2);
        vec2 localCoords = vec2(pixelPos.x - u_stringTopLeft.x, pixelPos.y - u_stringTopLeft.y);

        if (abs(localCoords.x + localCoords.y - delta) <= wiperWidth) {
            gl_FragColor = u_wiperColor;
            return;
        }
    }

    gl_FragColor = u_textColor;
}
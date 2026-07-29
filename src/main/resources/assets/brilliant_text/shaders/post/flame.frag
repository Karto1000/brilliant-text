#version 120

uniform sampler2D u_texture;
uniform vec2 u_textureSize;
uniform vec2 u_stringTopLeft;
uniform vec2 u_stringBottomRight;
uniform float u_time;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

bool isTextAtRelative(vec2 position) {
    vec2 offset = position / u_textureSize;
    vec4 texColor = texture2D(u_texture, gl_TexCoord[0].st + offset);
    return texColor.a != 0.;
}

void main() {
    vec2 st = gl_TexCoord[0].st;
    vec4 texColor = texture2D(u_texture, st);

    if (texColor.a == 0.) {
        bool aboveText = isTextAtRelative(vec2(0., 1.));
        bool rightText = isTextAtRelative(vec2(1., 0.));
        bool belowText = isTextAtRelative(vec2(0., -1.));
        bool leftText = isTextAtRelative(vec2(-1., 0.));

        if (aboveText || rightText || belowText || leftText) {
            vec2 firePos = st * u_textureSize * 0.12 + vec2(0.0, -u_time * 4.0);
            float heatPattern = noise(firePos);

            vec3 deepRed = vec3(0.85, 0.10, 0.00);
            vec3 brightOrange = vec3(1.00, 0.45, 0.00);
            vec3 hotYellow = vec3(1.00, 0.95, 0.40);

            vec3 fireColor = mix(deepRed, brightOrange, smoothstep(0.1, 0.5, heatPattern));
            fireColor = mix(fireColor, hotYellow, smoothstep(0.5, 0.85, heatPattern));

            gl_FragColor = vec4(fireColor, 1.);
            return;
        }
    }

    gl_FragColor = vec4(0., 0., 0., 1.);
}
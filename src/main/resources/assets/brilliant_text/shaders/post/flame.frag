#version 120

uniform sampler2D u_texture;
uniform vec2 u_textureSize;
uniform vec2 u_stringTopLeft;
uniform vec2 u_stringBottomRight;
uniform float u_time;

// --- Noise Functions ---

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

void main() {
    vec2 st = gl_TexCoord[0].st;

    // 1. Calculate subtle organic displacement (heat distortion)
    vec2 noisePos = st * u_textureSize * 0.08 + vec2(0.0, u_time * 3.5);

    // Convert noise displacement into pixel offset
    float offsetX = (noise(noisePos) - 0.5) * (1.8 / u_textureSize.x);
    float offsetY = (noise(noisePos + vec2(5.2, 1.3)) - 0.5) * (1.2 / u_textureSize.y);

    vec2 distortedST = st + vec2(offsetX, offsetY);

    // 2. Single-sample the texture at distorted position (Prevents ghost/duplicate layers)
    vec4 texColor = texture2D(u_texture, distortedST);

    // If there is no text at this distorted spot, draw nothing
    if (texColor.a < 0.1) {
        gl_FragColor = vec4(0.0);
        return;
    }

    // 3. Apply flame color animation WITHIN the character shapes
    vec2 firePos = st * u_textureSize * 0.12 + vec2(0.0, -u_time * 4.0);
    float heatPattern = noise(firePos);

    // Flame Palette: Hot yellow/white core to deep red
    vec3 deepRed      = vec3(0.85, 0.10, 0.00);
    vec3 brightOrange = vec3(1.00, 0.45, 0.00);
    vec3 hotYellow    = vec3(1.00, 0.95, 0.40);

    vec3 fireColor = mix(deepRed, brightOrange, smoothstep(0.1, 0.5, heatPattern));
    fireColor = mix(fireColor, hotYellow, smoothstep(0.5, 0.85, heatPattern));

    // Output clean, highly readable fire text
    gl_FragColor = vec4(fireColor, texColor.a);
}
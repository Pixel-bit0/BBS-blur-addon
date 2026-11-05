#version 150

uniform sampler2D Sampler0;

// x = mode (0 circle, 1 full, 2 actor), y = blur radius in pixels, z = circle radius, w = softness
uniform vec4 Params0;
// xy = circle centre (uv), z = aspect ratio, w = overall amount (fade in / out)
uniform vec4 Params1;
// actor rectangle in uv: minX, minY, maxX, maxY
uniform vec4 Params2;
// xy = screen size in pixels, z = actor edge feather in pixels
uniform vec4 Params3;

in vec2 texCoord;

out vec4 fragColor;

// 0 = sharp, 1 = fully blurred
float blurMask() {
    int mode = int(Params0.x + 0.5);

    if (mode == 1) {
        return 1.0;
    }

    if (mode == 0) {
        vec2 d = texCoord - Params1.xy;
        d.x *= Params1.z;
        float dist = length(d);
        return smoothstep(Params0.z, Params0.z + max(Params0.w, 0.0001), dist);
    }

    // actor focus: rounded rectangle signed distance, in pixels
    vec2 px = texCoord * Params3.xy;
    vec2 mn = Params2.xy * Params3.xy;
    vec2 mx = Params2.zw * Params3.xy;
    vec2 c = (mn + mx) * 0.5;
    vec2 h = (mx - mn) * 0.5;
    vec2 q = abs(px - c) - h;
    float sd = length(max(q, 0.0)) + min(max(q.x, q.y), 0.0);
    return smoothstep(0.0, max(Params3.z, 1.0), sd);
}

void main() {
    float m = blurMask() * Params1.w;
    float r = Params0.y * m;

    if (r < 0.5) {
        fragColor = texture(Sampler0, texCoord);
        return;
    }

    vec2 texel = 1.0 / Params3.xy;
    vec4 acc = vec4(0.0);
    const int N = 48;

    for (int i = 0; i < N; i++) {
        float t = (float(i) + 0.5) / float(N);
        float rad = sqrt(t) * r;
        float ang = float(i) * 2.39996323;
        vec2 off = vec2(cos(ang), sin(ang)) * rad * texel;
        acc += texture(Sampler0, texCoord + off);
    }

    fragColor = acc / float(N);
}

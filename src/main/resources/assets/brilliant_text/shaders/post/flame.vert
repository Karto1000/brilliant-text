#version 120

void main() {
    // Pass standard texture coordinates
    gl_TexCoord[0] = gl_MultiTexCoord0;

    // Transform vertex position
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
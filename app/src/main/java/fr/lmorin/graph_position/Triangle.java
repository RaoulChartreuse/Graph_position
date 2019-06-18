package fr.lmorin.graph_position;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle{


        private final String vertexShaderCode =
                        // This matrix member variable provides a hook to manipulate
                        // the coordinates of the objects that use this vertex shader
                        "precision mediump float;" +
                        "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        // the matrix must be included as a modifier of gl_Position
                        // Note that the uMVPMatrix factor *must be first* in order
                        // for the matrix multiplication product to be correct.
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";


        private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";
        // Use to access and set the view transformation
        private int vPMatrixHandle;


private FloatBuffer vertexBuffer;

// number of coordinates per vertex in this array
static final int COORDS_PER_VERTEX = 3;
        static float[] triangleCoords = {
                // in counterclockwise order:

                0.0f, .8f, -0.9f, // top
                -.8f, -0.711004243f,- 0.5f, // bottom left
                .8f, -0.711004243f,- 0.2f,  // bottom right
                .7f,0.6f,-0.21f,
                -.7f, 0.2f,-0.21f
                /*
                -1,-1, 1,  // X1,Y1,Z1
                1,-1, 1,  // X2,Y2,Z2
                -1, 1, 1,  // X3,Y3,Z3
                1, 1, 1,  // X4,Y4,Z4*/


        };
private final int mProgram;
        private int positionHandle;
        private int colorHandle;


        // Set color with red, green, blue and alpha (opacity) values
        float[] color = {0.63671875f, 0.76953125f, 0.12265625f, 1.0f};

public Triangle() {

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);





        int vertexShader = opengl_arrow.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = opengl_arrow.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);


        int[] link = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] != GLES20.GL_TRUE) {
                throw new RuntimeException("Program couldn't be loaded");
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        }


private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

public void draw(float[] mvpMatrix) {



        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


// Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);


        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);


        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        }

}



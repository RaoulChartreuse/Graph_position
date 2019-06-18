package fr.lmorin.graph_position;

import android.content.Context;
import android.opengl.GLES20;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class fleche {

        private List<String> verticesList;
        private List<String> facesList;
        private FloatBuffer verticesBuffer;
        private ShortBuffer facesBuffer;
        private int program;
        private int positionHandle;
        private int vPMatrixHandle;
        private int colorHandle;


        public fleche(Context context)  {
                verticesList = new ArrayList<>();
                facesList = new ArrayList<>();

                // Open the OBJ file with a Scanner
                Scanner scanner = null;
                try {
                        scanner = new Scanner(context.getAssets().open("Ma_fleche.obj"));
                } catch (IOException e) {
                        e.printStackTrace();
                }

                // Loop through all its lines
                while(scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if(line.startsWith("v ")) {
                                // Add vertex line to list of vertices
                                verticesList.add(line);
                        } else if(line.startsWith("f ")) {
                                // Add face line to faces list
                                facesList.add(line);
                        }
                }

                // Close the scanner
                scanner.close();


                // Create buffer for vertices
                ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
                buffer1.order(ByteOrder.nativeOrder());
                verticesBuffer = buffer1.asFloatBuffer();
                // Create buffer for faces
                ByteBuffer buffer2 = ByteBuffer.allocateDirect(facesList.size() * 3 * 2);
                buffer2.order(ByteOrder.nativeOrder());
                facesBuffer = buffer2.asShortBuffer();

                for(String vertex: verticesList) {
                        String[] coords = vertex.split(" "); // Split by space
                        float x = Float.parseFloat(coords[1]);
                        float y = Float.parseFloat(coords[2]);
                        float z = Float.parseFloat(coords[3]);
                        verticesBuffer.put(x);
                        verticesBuffer.put(z);
                        verticesBuffer.put(-y);
                }
                verticesBuffer.position(0);

                // because the indices start from one instead of zero, you must remember to subtract
                // one from them before putting them inside the buffer
                for(String face: facesList) {
                        String[] vertexIndices = face.split(" ");
                        short vertex1 = Short.parseShort(vertexIndices[1]);
                        short vertex2 = Short.parseShort(vertexIndices[2]);
                        short vertex3 = Short.parseShort(vertexIndices[3]);
                        facesBuffer.put((short)(vertex1 - 1));
                        facesBuffer.put((short)(vertex2 - 1));
                        facesBuffer.put((short)(vertex3 - 1));
                }
                facesBuffer.position(0);

                // Convert vertex_shader.txt to a string
                InputStream vertexShaderStream =
                        context.getResources().openRawResource(R.raw.vertex_shader);
                String vertexShaderCode =
                        null;
                try {
                        vertexShaderCode = IOUtils.toString(vertexShaderStream, Charset.defaultCharset());
                        vertexShaderStream.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }


                // Convert fragment_shader.txt to a string
                InputStream fragmentShaderStream =
                        context.getResources().openRawResource(R.raw.fragment_shader);
                String fragmentShaderCode =
                        null;
                try {
                        fragmentShaderCode = IOUtils.toString(fragmentShaderStream, Charset.defaultCharset());
                        fragmentShaderStream.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }


                int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
                GLES20.glShaderSource(vertexShader, vertexShaderCode);

                int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
                GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

                GLES20.glCompileShader(vertexShader);
                GLES20.glCompileShader(fragmentShader);

                program = GLES20.glCreateProgram();
                GLES20.glAttachShader(program, vertexShader);
                GLES20.glAttachShader(program, fragmentShader);
                GLES20.glLinkProgram(program);

                int[] link = new int[1];
                GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, link, 0);
                if (link[0] != GLES20.GL_TRUE) {
                        throw new RuntimeException("Program couldn't be loaded");
                }

                GLES20.glUseProgram(program);

                // get handle to shape's transformation matrix
                vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
                positionHandle = GLES20.glGetAttribLocation(program, "position");
                colorHandle = GLES20.glGetUniformLocation(program, "vColor");


        }

        public void draw(float[] mvpMatrix) {

                GLES20.glEnableVertexAttribArray(positionHandle);
                GLES20.glVertexAttribPointer(positionHandle,
                        3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);

                // Pass the projection and view transformation to the shader
                GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);



                GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                facesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);
                GLES20.glDisableVertexAttribArray(positionHandle);

        }

}

package com.freeme.view3d;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class MainActivity extends AppCompatActivity{
    private GLSurfaceView glSurfaceView;
    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    float baseSize = 2f;
    // x,y,z  y 为纵坐标
    float[] verticesTop = { baseSize, baseSize, baseSize,      baseSize, baseSize, -baseSize,       -baseSize, baseSize, -baseSize,    -baseSize, baseSize, baseSize };
    float[] verticesMiddle = { baseSize, 0f, baseSize,         baseSize, 0f, -baseSize,             -baseSize, 0f, -baseSize,          -baseSize, 0f, baseSize };
    float[] verticesBottom = { baseSize, -baseSize, baseSize,   baseSize, -baseSize, -baseSize,     -baseSize, -baseSize, -baseSize,    -baseSize, -baseSize, baseSize };

    private ArrayList<FloatBuffer> mLineVertices = new ArrayList<FloatBuffer>();
    float[] line1 = {-baseSize, baseSize, baseSize,   -baseSize, -baseSize, baseSize};
    float[] line2 = {baseSize, baseSize,baseSize,    baseSize, -baseSize, baseSize};
    float[] line3 = {baseSize,baseSize, -baseSize,   baseSize, -baseSize, -baseSize};
    float[] line4 = {-baseSize, baseSize,-baseSize,   -baseSize, -baseSize, -baseSize};

    private float angle = 1f;

    //存储历史运动轨迹
    private ArrayList<FloatBuffer> mMiddleHistoryVertices = new ArrayList<>();
    private ArrayList<FloatBuffer> mTopHistoryVertices = new ArrayList<>();
    private ArrayList<FloatBuffer> mBottomHistoryVertices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.main_gl);
        initVertexs(); // 初始化立方体的顶点集合
        init();
        findViewById(R.id.main_gl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a == 0) {
                    startRotate();
                }
            }
        });
    }

    private int a = 0;
    private void startRotate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(a == 200){
                        break;
                    }
                    mMiddleHistoryVertices.add(getFloatBuffer(new float[]{0f, 0f, 0.01f*a}));
                    mTopHistoryVertices.add(getFloatBuffer(new float[]{0f, baseSize, -0.01f*a}));
                    mBottomHistoryVertices.add(getFloatBuffer(new float[]{0f+0.01f*a, -baseSize, 0f}));
                    a++;
                    SystemClock.sleep(100);
                }
            }
        }).start();
    }

    private void initVertexs() {
        mVertices.add(getFloatBuffer(verticesMiddle));
        mVertices.add(getFloatBuffer(verticesTop));
        mVertices.add(getFloatBuffer(verticesBottom));

        mLineVertices.add(getFloatBuffer(line1));
        mLineVertices.add(getFloatBuffer(line2));
        mLineVertices.add(getFloatBuffer(line3));
        mLineVertices.add(getFloatBuffer(line4));
    }

    public void init() {
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            //可在此进行三维绘图的初始化操作
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                // 背景：白色
                gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                // 启动阴影平滑
                gl.glShadeModel(GL10.GL_SMOOTH);

            }

            //定义三维空间的大小 定义三维物体的方位
            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                // 设置输出屏幕大小
                gl.glViewport(0, 0, width, height);
                // 设置投影矩阵，对应gluPerspective（调整相机）、glFrustumf（调整透视投影）、glOrthof（调整正投影）
                gl.glMatrixMode(GL10.GL_PROJECTION);
                // 重置投影矩阵，即去掉所有的平移、缩放、旋转操作
                gl.glLoadIdentity();
                // 设置透视图视窗大小
                GLU.gluPerspective(gl, 40, (float) width / height, 0.1f, 20.0f);
                // 选择模型观察矩阵，对应gluLookAt（人动）、glTranslatef/glScalef/glRotatef（物动）
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                // 重置模型矩阵
                gl.glLoadIdentity();
            }

            //绘制三维图形的具体形状
            @Override
            public void onDrawFrame(GL10 gl) {
                // 清除屏幕和深度缓存
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
                // 重置当前的模型观察矩阵
                gl.glLoadIdentity();
                // 设置画笔颜色
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
                //绘制线的画笔粗细
                gl.glLineWidth(2f);
                //绘制点的画笔粗细
                //gl.glPointSize(2f);
                // 设置镜头的方位
                GLU.gluLookAt(gl, 10.0f, 8.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
                // 旋转图形
//                 gl.glRotatef(90f, 0, 0, -1);
//                 gl.glRotatef(90f, 0, -1, 0);
                //画面旋转
                //gl.glRotatef(angle, 0, -1, 0);
                // 沿x轴方向移动1个单位
                // gl.glTranslatef(1, 0, 0);
                // x，y，z方向缩放0.1倍
                // gl.glScalef(0.1f, 0.1f, 0.1f);
                // 绘制三层
                drawCube(gl);
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                //绘制动点
                drawCenterPoint(gl);
                gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                drawTopPoint(gl);
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
                drawBottomPoint(gl);
                //绘制4条竖线
                drawLines(gl);
            }
        });
    }

    private void drawLines(GL10 gl) {
        gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        // 启用顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for (FloatBuffer buffer : mLineVertices) {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
            gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        }
        // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void drawCenterPoint(GL10 gl) {
        if(a == 0)return;
        gl.glPointSize(2f);
        // 启用顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for (int i = 0; i < mMiddleHistoryVertices.size(); i++) {
            //左下方向动点
            if(i == mMiddleHistoryVertices.size()-1){
                gl.glPointSize(10f);
            }
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mMiddleHistoryVertices.get(i));
            gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
        }
            // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void drawTopPoint(GL10 gl) {
        if(a == 0)return;
        gl.glPointSize(2f);
        // 启用顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for (int i = 0; i < mTopHistoryVertices.size(); i++) {
            if(i == mTopHistoryVertices.size()-1){
                gl.glPointSize(10f);
            }
            //左下方向动点
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTopHistoryVertices.get(i));
            gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
        }
        // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void drawBottomPoint(GL10 gl) {
        if(a == 0)return;
        gl.glPointSize(2f);
        // 启用顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for (int i = 0; i < mBottomHistoryVertices.size(); i++) {
            if(i == mBottomHistoryVertices.size()-1){
                gl.glPointSize(10f);
            }
            //左下方向动点
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mBottomHistoryVertices.get(i));
            gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
        }
        // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }


    private void drawCube(GL10 gl) {
        int colorCount = 0;
        // 启用顶点开关
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 立方体由六个正方形平面组成
        for (FloatBuffer buffer : mVertices) {
            colorCount++;
            if(colorCount == 1){
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            }else if(colorCount == 2) {
                gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            }else if(colorCount == 3){
                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
            }
            // 将顶点坐标传给 OpenGL 管道
            //size: 每个顶点有几个数值描述。必须是2，3 ，4 之一。
            //type: 数组中每个顶点的坐标类型。取值：GL_BYTE, GL_SHORT, GL_FIXED, GL_FLOAT。
            //stride：数组中每个顶点间的间隔，步长（字节位移）。取值若为0，表示数组是连续的
            //pointer：即存储顶点的Buffer
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
            // 用画线的方式将点连接并画出来
            //GL_POINTS ————绘制独立的点
            //GL_LINE_STRIP————绘制连续的线段，不封闭
            //GL_LINE_LOOP————绘制连续的线段，封闭
            //GL_LINES————顶点两两连接，为多条线段构成
            //GL_TRIANGLES————每隔三个顶点构成一个三角形
            //GL_TRIANGLE_STRIP————每相邻三个顶点组成一个三角形
            //GL_TRIANGLE_FAN————以一个点为三角形公共顶点，组成一系列相邻的三角形
            gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4);
        }


        // 禁用顶点开关
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public static FloatBuffer getFloatBuffer(float[] array) {
        //初始化字节缓冲区的大小=数组长度*数组元素大小。float类型的元素大小为Float.SIZE，
        //int类型的元素大小为Integer.SIZE，double类型的元素大小为Double.SIZE。
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * Float.SIZE);
        //以本机字节顺序来修改字节缓冲区的字节顺序
        //OpenGL在底层的实现是C语言，与Java默认的数据存储字节顺序可能不同，即大端小端问题。
        //因此，为了保险起见，在将数据传递给OpenGL之前，需要指明使用本机的存储顺序
        byteBuffer.order(ByteOrder.nativeOrder());
        //根据设置好的参数构造浮点缓冲区
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        //把数组数据写入缓冲区
        floatBuffer.put(array);
        //设置浮点缓冲区的初始位置
        floatBuffer.position(0);
        return floatBuffer;
    }
}

/**
 // 设置白色背景。四个参数依次为透明度alpha、红色red、绿色green、蓝色blue
 gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
 // 设置画笔颜色为橙色
 gl.glColor4f(0.0f, 1.0f, 1.0f, 0.0f);

 // 沿着y轴的负方向旋转90度
 gl.glRotatef(90, 0, -1, 0);
 // 沿x轴方向移动1个单位
 gl.glTranslatef(1, 0, 0);
 // x，y，z三方向各缩放0.1倍
 gl.glScalef(0.1f, 0.1f, 0.1f);

 // 设置输出屏幕大小
 gl.glViewport(0, 0, glSurfaceView.getWidth(), glSurfaceView.getHeight());


 // 设置投影矩阵，对应gluPerspective（调整相机参数）、glFrustumf（调整透视投影）、glOrthof（调整正投影）
 gl.glMatrixMode(GL10.GL_PROJECTION);
 // 重置投影矩阵，即去掉所有的参数调整操作
 gl.glLoadIdentity();
 // 设置透视图视窗大小。第二个参数是焦距的角度，第四个参数是能看清的最近距离，第五个参数是能看清的最远距离
 GLU.gluPerspective(gl, 40, (float) glSurfaceView.getWidth() / glSurfaceView.getHeight(), 0.1f, 20.0f);


 // 选择模型观察矩阵，对应gluLookAt（人动）、glTranslatef/glScalef/glRotatef（物动）
 gl.glMatrixMode(GL10.GL_MODELVIEW);
 // 重置模型矩阵，即去掉所有的位置挪动操作
 gl.glLoadIdentity();
 // 设置镜头的方位。第二到第四个参数为相机的位置坐标，第五到第七个参数为相机画面中心点的坐标，第八到第十个参数为朝上的坐标方向，比如第八个参数为1表示x轴朝上，第九个参数为1表示y轴朝上，第十个参数为1表示z轴朝上
 GLU.gluLookAt(gl, 10.0f, 8.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
 */

package com.github.ruslanjava.baldagame.cellBoard.arrows;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

public enum Arrow {

    LEFT(new double[][] {
            {0.57d, 0.45d}, {0.5d, 0.45d}, {0.5d, 0.4d},
            {0.43d, 0.5d},
            {0.5d, 0.6d}, {0.5d, 0.55d}, {0.57d, 0.55d}
    }),

    RIGHT(mirrorX(LEFT.points)),

    UP(new double[][] {
            {0.45d, 0.57d}, {0.45d, 0.5d}, {0.35d, 0.5d},
            {0.5d, 0.42d},
            {0.65d, 0.5d}, {0.55d, 0.5d}, {0.55d, 0.57d}
    }),

    DOWN(mirrorY(UP.points));

    private Paint paint;
    private PercentPath path;
    private double[][] points;

    Arrow(double[]... points) {
        this.points = points;
        path = new PercentPath();

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    public void draw(Canvas canvas, int x1, int y1, int x2, int y2) {
        paint.setShader(new LinearGradient(0, y1, 0, y2, 0xAA7777FF, 0xAA0000FF, Shader.TileMode.REPEAT));

        path.setBorders(x1, y1, x2, y2);

        path.reset();
        path.moveTo(points[0]);
        for (int i = 1; i < points.length; i++) {
            path.lineTo(points[i]);
        }
        path.lineTo(points[0]);

        path.draw(canvas, paint);
    }

    private static double[][] mirrorX(double[][] points) {
        double[][] result = new double[points.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new double[] { 1.0d - points[i][0], points[i][1] };
        }
        return result;
    }

    private static double[][] mirrorY(double[][] points) {
        double[][] result = new double[points.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = new double[] { points[i][0], 1.0d - points[i][1] };
        }
        return result;
    }

    private static class PercentPath {

        private android.graphics.Path path;

        private int x1;
        private int y1;
        private double width;
        private double height;

        PercentPath() {
            path = new android.graphics.Path();
        }

        void setBorders(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.width = (double) (x2 - x1);
            this.height = (double) (y2 - y1);
        }

        void reset() {
            path.reset();
        }

        void moveTo(double[] pointXY) {
            int x = getX(pointXY[0]);
            int y = getY(pointXY[1]);
            path.moveTo(x, y);
        }

        void lineTo(double[] pointXY) {
            int x = getX(pointXY[0]);
            int y = getY(pointXY[1]);
            path.lineTo(x, y);
        }

        void draw(Canvas canvas, Paint paint) {
            canvas.drawPath(path, paint);
        }

        private int getX(double percentX) {
            return (int) (x1 + percentX * width);
        }

        private int getY(double percentY) {
            return (int) (y1 + percentY * height);
        }

    }

}

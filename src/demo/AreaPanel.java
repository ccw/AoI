package demo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * The area to contain the given number of points
 */
public class AreaPanel extends JPanel {

    private int intPickedIndex = 0;
    private int intAreaRadius, intBoundary, intFrequency, intMaxDistance;
    private Point[] aryPoints;

    private AbstractTableModel tbmObserver;

    public AreaPanel(int aPOI, int aBoundary, int aRadius,
                     int aUpdateFrequency, int aMaximumMovingDistance) {
        intAreaRadius = aRadius;
        intBoundary = aBoundary;
        intFrequency = aUpdateFrequency;
        intMaxDistance = aMaximumMovingDistance;
        aryPoints = new Point[aPOI];

        setBackground(Color.black);
        setPreferredSize(new Dimension(aBoundary, aBoundary));

        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < aryPoints.length; i++) {
            aryPoints[i] = new Point(i, r.nextInt(aBoundary), r.nextInt(aBoundary));
        }
        PointWalker walker = new PointWalker(this);
        walker.execute();
    }

    public Point[] getPoints() {
        return aryPoints;
    }

    public Point[] getPointsInArea() {
        ArrayList<Point> lstPoints = new ArrayList<Point>();
        for (int i = 0, aryPointsLength = aryPoints.length; i < aryPointsLength; i++) {
            Point point = aryPoints[i];
            if (point.inArea) lstPoints.add(point);
        }
        return lstPoints.toArray(new Point[lstPoints.size()]);
    }

    public void registerObserverTableModel(AbstractTableModel tableModel) {
        tbmObserver = tableModel;
    }

    public void setPickedIndex(int index) {
        if (index <= aryPoints.length) {
            intPickedIndex = index;
        }
    }

    public int getPickedIndex() {
        return intPickedIndex;
    }

    public Point getPickedPoint() {
        return aryPoints[intPickedIndex];
    }

    @Override
    protected void paintComponent(Graphics aGraphics) {
        Graphics2D g2d = (Graphics2D) aGraphics;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, intBoundary, intBoundary);

        if (intPickedIndex >= 0) {
            Composite originalComposite = g2d.getComposite();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setPaint(Color.GRAY);
            g2d.fillOval(aryPoints[intPickedIndex].x - intAreaRadius,
                         aryPoints[intPickedIndex].y - intAreaRadius,
                         intAreaRadius * 2,
                         intAreaRadius * 2);

            g2d.setComposite(originalComposite);
        }

        for (int i = 0; i < aryPoints.length; i++) {
            Point point = aryPoints[i];
            int intRadius = 2;
            if (i == intPickedIndex) {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Courier New", Font.BOLD, 12));
                intRadius = 5;
            } else {
                if (point.inArea(aryPoints[intPickedIndex], intAreaRadius)) {
                    g2d.setColor(Color.BLUE);
                    g2d.setFont(new Font("Dialog", Font.ITALIC, 10));
                    g2d.drawLine(aryPoints[intPickedIndex].x, aryPoints[intPickedIndex].y, point.x, point.y);
                    intRadius = 3;
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Dialog", Font.PLAIN, 8));
                }
            }
            g2d.drawString(String.valueOf(i), point.x, point.y);
            g2d.fillOval(point.x, point.y, intRadius, intRadius);
        }
    }

    /**
     * The worker to walk the given points in required frequent time
     */
    class PointWalker extends SwingWorker<Point[], Integer> {
        AreaPanel pnlArea;

        public PointWalker(AreaPanel pnlArea) {
            this.pnlArea = pnlArea;
        }

        @Override
        protected Point[] doInBackground() throws Exception {
            Random r = new Random(System.currentTimeMillis());
            while (true) {
                for (Point point : aryPoints) {
                    point.move(r, intMaxDistance, intBoundary);
                }
                pnlArea.repaint();
                if (tbmObserver != null) tbmObserver.fireTableDataChanged();
                try {
                    Thread.sleep(intFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //return aryPoints;
        }
    }

    class Point {

        final public int index;
        public int x = 0;
        public int y = 0;

        public boolean inArea = false;

        public Point(int aIndex, int aX, int aY) {
            this.index = aIndex;
            this.x = aX;
            this.y = aY;
        }

        public void move(Random aRandom, int aStep, int aBoundary) {
            x = calMovingPoint(aRandom, x, aStep, aBoundary);
            y = calMovingPoint(aRandom, y, aStep, aBoundary);
        }

        private int calMovingPoint(Random aRandom, int aCurrentPoint,
                                   int aStep, int aBoundary) {
            int movingPoint = aCurrentPoint + aRandom.nextInt(aStep) * (aRandom.nextBoolean() ? 1 : -1);
            if (movingPoint < 0)
                movingPoint = Math.abs(movingPoint);
            else if (movingPoint > aBoundary)
                movingPoint = aBoundary * 2 - movingPoint;
            return movingPoint;
        }

        public boolean inArea(Point aPoint, int aRadius) {
            inArea = false;

            int a = Math.abs(aPoint.x - x);
            int b = Math.abs(aPoint.y - y);
            if (a == 0) {
                inArea = (b <= aRadius);
            } else if (b == 0) {
                inArea = (a <= aRadius);
            } else {
                inArea = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)) <= aRadius;
            }
            return inArea;
        }

        @Override
        public String toString() {
            return "[" + x + ":" + y + "]";
        }
    }

}

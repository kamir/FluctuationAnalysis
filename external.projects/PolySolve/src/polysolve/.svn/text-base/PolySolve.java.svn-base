/***************************************************************************
 *   Copyright (C) 2009 by Paul Lutus                                      *
 *   lutusp@arachnoid.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package polysolve;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/*
 * PolySolve.java
 *
 * Created on Oct 16, 2009, 10:35:14 AM
 */
/**
 *
 * @author lutusp
 */
public final class PolySolve extends java.applet.Applet {

    private boolean m_fStandAlone = false;
    String nameVersion = "PolySolve 3.3";
    private String copyright = "Copyright \251 2009, P. Lutus -- http://www.arachnoid.com";
    public Color gridColor = new Color(192, 240, 192);
    public Color zeroColor = new Color(192, 192, 192);
    public Color lineColor = new Color(0, 0, 255);
    public Color dataColor = new Color(255, 0, 0);
    MatrixFunctions mfunct;
    public double dotScale = 4.0;
    public String pageData;
    public String errorMsg = "";
    public boolean data_valid = false;
    public int poly_order = 2; // default order
    public double xmin, xmax, ymin, ymax;
    public double dataXmin, dataXmax;
    public double dataYmin, dataYmax;
    public double eps;
    public int listingForm = 0;
    public boolean error;
    private String defaultData = "-1 -1\n" + "0 3\n" + "1 2.5\n" + "2 5\n" + "3 4\n" + "5 2\n" + "7 5\n" + "9 4\n";
    Vector<Pair> userDataVector;
    Pair[] userData;
    double[] terms;
    double result_cc;
    double result_se;

    public static void main(String[] args) throws Exception {
        final PolySolve theApplet = new PolySolve();
        theApplet.m_fStandAlone = true;
        JFrame theFrame;
        theFrame = new JFrame(theApplet.nameVersion);
        theFrame.addWindowListener(new FrameListener());
        theFrame.setSize(700, 800);
        theFrame.add("Center", theApplet);
        theApplet.init();
        theApplet.start();
        theFrame.setVisible(true);
    }

    /** Initializes the applet PolySolve */
    @Override
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {

                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!m_fStandAlone) {
            topPanel.remove(pasteButton);
            bottomPanel.remove(copyButton);
        }
        mfunct = new MatrixFunctions(this);
        dataTextArea.getDocument().addDocumentListener(new MyDocumentListener());
        dataTextArea.setText(defaultData);
        process();
    }

    class MyDocumentListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            process();
        }

        public void removeUpdate(DocumentEvent e) {
            process();
        }

        public void changedUpdate(DocumentEvent e) {
            process();
        }
    };

    void readData() {
        userDataVector = new Vector<Pair>();
        double x = 0, y = 0, q = 0;
        String s = pageData;
        try {
            // filter all but numerical entry characters
            // and trim beginning and end ws
            s = s.replaceAll("[^\\.0-9eE+-]+", " ").trim();
            // create array of numerical values
            String[] num_array = s.split(" ");
            boolean paired = true;
            // parse array using new iterator syntax
            for (String qs : num_array) {
                try {
                    q = Double.valueOf(qs).doubleValue();
                } catch (Exception e) {
                    String es = "";
                    if (qs.length() == 0) {
                        es = "No data.";
                    } else {
                        es = "Cannot parse \"" + qs + "\" in input.";
                    }
                    throw new Exception(es);
                }
                if (paired) {
                    x = q;
                } else {
                    y = q;
                    userDataVector.add(new Pair(x, y));
                    ymin = Math.min(y, ymin);
                    ymax = Math.max(y, ymax);
                    xmin = Math.min(x, xmin);
                    xmax = Math.max(x, xmax);
                }
                paired = !paired;
            }
            if (!paired) {
                throw new Exception("Data not in x,y pairs (odd number of entries) ... add more data.");
            }
        } catch (Exception e) {
            data_valid = false;
            errorMsg = e.getMessage();
            userDataVector.clear();
        }
    }

    void getData() {
        eps = 1e-12;
        xmin = 1e30;
        ymin = xmin;
        xmax = 1e-30;
        ymax = xmax;
        double x, y;
        readData();
        int n = userDataVector.size();
        if (n > 0) {
            if (Math.abs(xmin - xmax) < 1e-3) {
                xmin -= 1e-3;
                xmax += 1e-3;
            }
            if (Math.abs(ymin - ymax) < 1e-3) {
                ymin -= 1e-3;
                ymax += 1e-3;
            }

            dataXmax = xmax;
            dataXmin = xmin;
            dataYmax = ymax;
            dataYmin = ymin;
            double q = (ymax - ymin) / 6;
            ymin -= q;
            ymax += q;
            q = (xmax - xmin) / 6;
            xmin -= q;
            xmax += q;
        }
    }

    void p(String s) {
        System.out.println(s);
    }

    public void process() {
        pageData = dataTextArea.getText();
        data_valid = false;
        poly_order = (poly_order < 0) ? 0 : poly_order;
        poly_order = (poly_order > 512) ? 512 : poly_order;
        degreeTextField.setText("" + poly_order);
        errorMsg = "";
        getData();
        int size = userDataVector.size();
        if (size > 1) {
            userData = userDataVector.toArray(new Pair[]{});
            terms = mfunct.polyregress(userData, poly_order);
            result_cc = mfunct.corr_coeff(userData, terms);
            result_se = mfunct.std_error(userData, terms);
            String r = showResult();
            resultText.setText(r);
            data_valid = true;
        } else {
            data_valid = false;
            if (errorMsg.length() == 0) {
                errorMsg = "Insufficient Data (minimum of 2 data pairs needed).";
            }
            resultText.setText("");
        }
        GraphPanel gp = (GraphPanel) middlePanel;
        gp.repaint();
    }

    void newDegree(int v) {
        poly_order += v;
        poly_order = (poly_order < 0) ? 0 : poly_order;
        process();
    }

    void changeListingStyle() {
        listingForm++;
        listingForm %= 3;
        process();
    }

    String formatNum(double n, boolean wide) {
        String w = (wide) ? "21" : "";
        return String.format("%" + w + ".12e", n);
    }

    String showResult() {

        String styleTag[] = {
            "",
            "pow",
            "Math.pow"
        };
        int n = userData.length;
        String r = "Degree " + poly_order + ", " + n + " x,y pairs. ";
        r += "Corr. coeff. (r^2) = " + formatNum(result_cc, false) + ". ";
        r += "SE = " + formatNum(result_se, false) + "\n\n";
        r += (listingForm > 0) ? "double f(double x) {\n    return" : "f(x) =";
        for (int i = 0; i <= poly_order; i++) {
            double a = terms[i];
            if (i > 0) {
                if (listingForm > 0) {
                    r += "    ";
                }
                r += "     +";
            }
            r += formatNum(a, true);
            if (i == 1) {
                r += " * x";
            }
            if (i > 1) {
                if (listingForm > 0) {
                    r += (" * " + styleTag[listingForm] + "(x," + i + ")");
                } else {
                    r += (" * x^" + i);
                }
            }
            if (i < poly_order) {
                r += "\n";
            }
        }
        if (listingForm > 0) {
            r += ";\n}";
        }
        if (poly_order > n - 1) {
            r += "\n\nWarning: Polynomial degree exceeds data size - 1.";
        }
        r += "\n\n" + copyright + ". All Rights Reserved.";
        return r;
    }

    void show_mat(double[][] data) {
        for (double[] y : data) {
            for (double x : y) {
                System.out.printf("%25.16g,", x);
            }
            System.out.println("");
        }
        System.out.println("***************");
    }

    double fx(double x, double[] terms) {
        double a = 0;
        int e = 0;
        for (double i : terms) {
            a += i * Math.pow(x, e);
            e++;
        }
        return a;
    }

    // as simple as I could make it
    // given the misbehavior of polynomials
    double findRoot2(double y, double x, double[] terms, double scale) {
        int max = 256;
        boolean positive = true;
        double epsilon = 1e-8;
        double dy = 0;
        double ody = Double.NaN;
        while (Math.abs(dy = (fx(x, terms) - y)) > epsilon && max-- > 0) {
            if (Double.isInfinite(x)) {
                break;
            }
            if (!Double.isNaN(ody)) {
                if (Math.abs(dy) > ody) {
                    positive = !positive;
                }
            }
            ody = Math.abs(dy);
            dy *= scale;
            x += (positive) ? dy : -dy;
        }
        if (max <= 0 || Double.isInfinite(x)) {
            x = Double.NaN;
        }
        
        return x;
    }

    // begin with small steps, if algorithm fails
    // gradually make them larger
    double findRoot(double y, double x, double[] terms) {
        double scale = Math.pow(2,-32);
        int max = 64;
        double rx;
        while(Double.isNaN(rx = findRoot2(y, x, terms,scale)) && max-- > 0) {
            scale *= 2.0;
        }
        return rx;
    }

    double plotFunct(double x) {
        return fx(x, terms);
    }

    void clipCopy() {
        String s = resultText.getText();
        ClipboardFunctions.clipCopy(s);
    }

    void clipPaste() {
        String data = ClipboardFunctions.clipPaste(this);
        data = dataTextArea.getText() + data;
        dataTextArea.setText(data);
    }

    void xForY(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String ys = yEntryTextfield.getText();
            String xs = yResultTextfield.getText();
            double y = Double.parseDouble(ys);
            double x = Double.parseDouble(xs);
            if (Double.isNaN(x) || Double.isInfinite(x)) {
                x = 0;
            }
            double rx = findRoot(y, x, terms);
            xs = formatNum(rx, false);
            yResultTextfield.setText(xs);
        }
    }

    void yForX(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String xs = xEntryTextfield.getText();
            double x = Double.parseDouble(xs);
            double y = fx(x, terms);
            String ys = formatNum(y, false);
            xResultTextfield.setText(ys);
        }
    }

    void trackMouse(MouseEvent evt) {
        int x = evt.getX();
        int y = evt.getY();
        Pair mp = ((GraphPanel)middlePanel).mousePos(x,y);
        String sx = String.format("%.4g",mp.x);
        String sy = String.format("%.4g",mp.y);
        posLabel.setText("x = " + sx + ", y = " + sy);
    }

    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        leftButton = new javax.swing.JButton();
        degreeTextField = new javax.swing.JTextField();
        rightButton = new javax.swing.JButton();
        solveButton = new javax.swing.JButton();
        cfuncButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        resultsPane = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultText = new javax.swing.JTextArea();
        computePane = new javax.swing.JPanel();
        xlabel = new javax.swing.JLabel();
        xEntryTextfield = new javax.swing.JTextField();
        xResultTextfield = new javax.swing.JTextField();
        ylabel = new javax.swing.JLabel();
        entryLabel = new javax.swing.JLabel();
        resultLabel = new javax.swing.JLabel();
        xlabel1 = new javax.swing.JLabel();
        yEntryTextfield = new javax.swing.JTextField();
        ylabel1 = new javax.swing.JLabel();
        yResultTextfield = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        posLabel = new javax.swing.JLabel();
        middlePanel = new GraphPanel(this);
        topPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        pasteButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        mainPanel.setLayout(new java.awt.GridBagLayout());

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Degree:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        bottomPanel.add(jLabel2, gridBagConstraints);

        leftButton.setText("<-");
        leftButton.setToolTipText("Decrease polynomial degree");
        leftButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                leftButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bottomPanel.add(leftButton, gridBagConstraints);

        degreeTextField.setEditable(false);
        degreeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        degreeTextField.setText("1");
        degreeTextField.setToolTipText("Current polynomial fit degree");
        degreeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                degreeTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bottomPanel.add(degreeTextField, gridBagConstraints);

        rightButton.setText("->");
        rightButton.setToolTipText("Increase polynomial degree");
        rightButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rightButtonMouseClicked(evt);
            }
        });
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bottomPanel.add(rightButton, gridBagConstraints);

        solveButton.setText("Solve");
        solveButton.setToolTipText("Force recalculation");
        solveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                solveButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bottomPanel.add(solveButton, gridBagConstraints);

        cfuncButton.setText("Form");
        cfuncButton.setToolTipText("Cycle through mathematical, C/C++ and Java listing styles");
        cfuncButton.setActionCommand("Listing Style");
        cfuncButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cfuncButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bottomPanel.add(cfuncButton, gridBagConstraints);

        copyButton.setText("Copy");
        copyButton.setToolTipText("Copy results to clipboard");
        copyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                copyButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        bottomPanel.add(copyButton, gridBagConstraints);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        resultsPane.setLayout(new java.awt.GridBagLayout());

        resultText.setColumns(20);
        resultText.setFont(new java.awt.Font("Courier New", 0, 12));
        resultText.setRows(5);
        resultText.setToolTipText("Read or copy (Ctrl+C) computation results here \n");
        resultText.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane2.setViewportView(resultText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        resultsPane.add(jScrollPane2, gridBagConstraints);

        jTabbedPane1.addTab("Results", resultsPane);

        computePane.setLayout(new java.awt.GridBagLayout());

        xlabel.setText("y = f(x): x");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 5, 4);
        computePane.add(xlabel, gridBagConstraints);

        xEntryTextfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        xEntryTextfield.setText("0.0");
        xEntryTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                xEntryTextfieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        computePane.add(xEntryTextfield, gridBagConstraints);

        xResultTextfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        xResultTextfield.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        computePane.add(xResultTextfield, gridBagConstraints);

        ylabel.setText("y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 5, 4);
        computePane.add(ylabel, gridBagConstraints);

        entryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        entryLabel.setText("Argument");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        computePane.add(entryLabel, gridBagConstraints);

        resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultLabel.setText("Result");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        computePane.add(resultLabel, gridBagConstraints);

        xlabel1.setText("x = f(y): y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 7, 4);
        computePane.add(xlabel1, gridBagConstraints);

        yEntryTextfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        yEntryTextfield.setText("0.0");
        yEntryTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                yEntryTextfieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        computePane.add(yEntryTextfield, gridBagConstraints);

        ylabel1.setText("x");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 7, 4);
        computePane.add(ylabel1, gridBagConstraints);

        yResultTextfield.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        yResultTextfield.setText("0.0");
        yResultTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                yResultTextfieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        computePane.add(yResultTextfield, gridBagConstraints);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("Courier New", 0, 12));
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("First row: type an X argument and press Enter for a Y result (y = f(x)).\nSecond row: type a Y argument and press Enter for an X result (x = f(y)).\nBecause x= f(y) may have multiple solutions, it may be necessary to enter an X estimate in the \"Result\" window.\nBeyond degree 2, it's not easy to obtain results for the x = f(y) case, regardless of the method used.\n");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane3.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        computePane.add(jScrollPane3, gridBagConstraints);

        jTabbedPane1.addTab("Compute", computePane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        bottomPanel.add(jTabbedPane1, gridBagConstraints);

        posLabel.setText("x = 0.0, y = 0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        bottomPanel.add(posLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        mainPanel.add(bottomPanel, gridBagConstraints);

        middlePanel.setBackground(new java.awt.Color(255, 255, 255));
        middlePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        middlePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                middlePanelMouseMoved(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 5);
        mainPanel.add(middlePanel, gridBagConstraints);

        topPanel.setLayout(new java.awt.GridBagLayout());

        dataTextArea.setColumns(20);
        dataTextArea.setFont(new java.awt.Font("Courier New", 0, 12));
        dataTextArea.setRows(5);
        dataTextArea.setToolTipText("Enter or paste (Ctrl+V) x,y data pairs here");
        dataTextArea.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane1.setViewportView(dataTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        topPanel.add(jScrollPane1, gridBagConstraints);

        jLabel1.setText("Data x,y Pairs (any format)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topPanel.add(jLabel1, gridBagConstraints);

        pasteButton.setText("Paste");
        pasteButton.setToolTipText("Paste from clipboard to input");
        pasteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pasteButtonMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topPanel.add(pasteButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 4);
        mainPanel.add(topPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(mainPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void solveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_solveButtonMouseClicked
        // TODO add your handling code here:
        process();
    }//GEN-LAST:event_solveButtonMouseClicked

    private void rightButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rightButtonMouseClicked
        // TODO add your handling code here:
        newDegree(1);
    }//GEN-LAST:event_rightButtonMouseClicked

    private void leftButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_leftButtonMouseClicked
        // TODO add your handling code here:
        newDegree(-1);
    }//GEN-LAST:event_leftButtonMouseClicked

    private void cfuncButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cfuncButtonMouseClicked
        // TODO add your handling code here:
        changeListingStyle();
    }//GEN-LAST:event_cfuncButtonMouseClicked

    private void pasteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pasteButtonMouseClicked
        // TODO add your handling code here:
        clipPaste();
    }//GEN-LAST:event_pasteButtonMouseClicked

    private void copyButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_copyButtonMouseClicked
        // TODO add your handling code here:
        clipCopy();
    }//GEN-LAST:event_copyButtonMouseClicked

    private void xEntryTextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_xEntryTextfieldKeyReleased
        // TODO add your handling code here:
        yForX(evt);
    }//GEN-LAST:event_xEntryTextfieldKeyReleased

    private void yEntryTextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_yEntryTextfieldKeyReleased
        // TODO add your handling code here:
        xForY(evt);
    }//GEN-LAST:event_yEntryTextfieldKeyReleased

    private void yResultTextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_yResultTextfieldKeyReleased
        // TODO add your handling code here:
        xForY(evt);
    }//GEN-LAST:event_yResultTextfieldKeyReleased

    private void middlePanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_middlePanelMouseMoved
        // TODO add your handling code here:
        trackMouse(evt);
    }//GEN-LAST:event_middlePanelMouseMoved

    private void degreeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_degreeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_degreeTextFieldActionPerformed

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rightButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cfuncButton;
    private javax.swing.JPanel computePane;
    private javax.swing.JButton copyButton;
    public javax.swing.JTextArea dataTextArea;
    private javax.swing.JTextField degreeTextField;
    private javax.swing.JLabel entryLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton leftButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel middlePanel;
    private javax.swing.JButton pasteButton;
    private javax.swing.JLabel posLabel;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JTextArea resultText;
    private javax.swing.JPanel resultsPane;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton solveButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JTextField xEntryTextfield;
    private javax.swing.JTextField xResultTextfield;
    private javax.swing.JLabel xlabel;
    private javax.swing.JLabel xlabel1;
    private javax.swing.JTextField yEntryTextfield;
    private javax.swing.JTextField yResultTextfield;
    private javax.swing.JLabel ylabel;
    private javax.swing.JLabel ylabel1;
    // End of variables declaration//GEN-END:variables
}




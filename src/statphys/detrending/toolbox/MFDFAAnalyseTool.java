package statphys.detrending.toolbox;

import charts.MultiChart;
import data.series.Messreihe;
import data.series.MessreiheFFT;
import data.series.MesswertTabelle;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;
import statphys.detrending.DetrendingMethodFactory;
import statphys.detrending.MultiDFATool;
import statphys.detrending.TestDataFactory;
import statphys.detrending.methods.DFACore;
import statphys.detrending.methods.IDetrendingMethod;
import statphys.detrending.methods.MFDFA;
import stdlib.StdDraw;
import stdlib.StdStats;

public class MFDFAAnalyseTool {

    public static int max = 24 * 7 * 51 * 9 ;

    public static int binning = 1; //24 * 7;
    static String dt = "hour";

//    public static int binning = 24 * 7;  
//    public static String dt = "week";
     
//    public static int binning = 24 * 7 * 4;  
//    static String dt = "month";
    
    static boolean DOlog = true;
    
    public static int jahr_min = 2001;
    public static int jahr_max = 2010;
    
    static boolean justMFDFA = true;

    public static void main(String args[]) throws Exception {

        int[] langs = {60, 52, 88, 109, 122, 166, 222};
        
//         int[] langs = {52};
        
        Color[] colors = new Color[7];
        colors[0] = Color.gray;
        colors[1] = Color.blue;
        //colors[2] = Color.cyan;
        colors[2] = Color.lightGray;
        colors[3] = Color.white;
        colors[4] = Color.green;
        colors[5] = Color.orange;
        colors[6] = Color.red;
        
        /**
         * aus dem NORCO, gemountet an X:
         */
        DataFactory2 df = new DataFactory2();

        Vector<Messreihe> rowsA1 = new Vector<Messreihe>();
        Vector<Messreihe> rowsA2 = new Vector<Messreihe>();
        Vector<Messreihe> rowsA3 = new Vector<Messreihe>();
        Vector<Messreihe> rowsA4 = new Vector<Messreihe>();

        Vector<Messreihe> rowsB2 = new Vector<Messreihe>();
        Vector<Messreihe> rowsB1 = new Vector<Messreihe>();
        Vector<Messreihe> rowsB4 = new Vector<Messreihe>();
        Vector<Messreihe> rowsB3 = new Vector<Messreihe>();

        Vector<Messreihe> ratioA = new Vector<Messreihe>();
        Vector<Messreihe> ratioB = new Vector<Messreihe>();
        Vector<Messreihe> ratioC = new Vector<Messreihe>();
        Vector<Messreihe> ratioD = new Vector<Messreihe>();
        Vector<Messreihe> ratioE = new Vector<Messreihe>();
        Vector<Messreihe> ratioF = new Vector<Messreihe>();

        Vector<Messreihe> FSQS1 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS2 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS3 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS4 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS5 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS6 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS7 = new Vector<Messreihe>();
        Vector<Messreihe> FSQS8 = new Vector<Messreihe>();
       
            double fmin1 = 1.0;
            double fmax1 = 3.5;
            
            double fmin2 = 1.0;
            double fmax2 = 3.5;
            
        for (int lang : langs) {
            
            // Stufe 1 
            //
            // Wachstum der Seiten
            df.init_for_WikipediaWachstumsreihen(lang);

            Messreihe rBMM1 = df.getWikiRow("NN", 6, false, false, DataFactory2.binMode_AV);
            Messreihe rBMM2 = df.getWikiRow("nN", 7, true, DOlog, DataFactory2.binMode_SUM);

            Messreihe rBMM3 = df.getWikiRow("LN", 8, false, false, DataFactory2.binMode_AV);
            Messreihe rBMM4 = df.getWikiRow("lN", 9, true, DOlog, DataFactory2.binMode_SUM);
            
            rowsA1.add(rBMM1);
            rowsA2.add(rBMM2);

            rowsA3.add(rBMM3);
            rowsA4.add(rBMM4);

            // Stufe 2 
            //
            // Edit count und Volumen Wachstum der Seiten

            Messreihe r1 = df.getRevisionsRow("EN", 6, false, false, DataFactory2.binMode_AV);
            Messreihe r2 = df.getRevisionsRow("eN", 7, true, DOlog, DataFactory2.binMode_SUM);

            Messreihe r3 = df.getRevisionsRow("TV", 8, false, false, DataFactory2.binMode_AV);
            Messreihe r4 = df.getRevisionsRow("tV", 9, true, DOlog, DataFactory2.binMode_SUM);

            ratioA.add( rBMM1.divide_by( rBMM3 ) );
            ratioB.add( rBMM1.divide_by( r1 ) );
            ratioC.add( rBMM1.divide_by( r3 ) );
            ratioD.add( rBMM3.divide_by( r1 ) );
            ratioE.add( rBMM3.divide_by( r3 ) );
            ratioF.add( r3.divide_by( r1 ) );

            // change
            rowsB2.add(r2);
            rowsB4.add(r4);

            // total
            rowsB1.add(r1);
            rowsB3.add(r3);
            
            /**
             * CUT ROWS according to the life cycle stages ...
             */            
            
         
            
            FSQS1.add( processRow( rBMM2 , lang+"-nN_0" , fmin1, fmax1) );
            FSQS2.add( processRow( rBMM4 , lang+"-lN_0" , fmin1, fmax1) );
            FSQS3.add( processRow( r2 , lang+"-eN_0" , fmin1, fmax1) );
            FSQS4.add( processRow( r4 , lang+"-tV_0" , fmin1, fmax1) );

            FSQS5.add( processRow( rBMM1 , lang+"-NN_1" , fmin2, fmax2) );
            FSQS6.add( processRow( rBMM3 , lang+"-LN_1" , fmin2, fmax2) );
            FSQS7.add( processRow( r1 , lang+"-EN_1" , fmin2, fmax2) );
            FSQS8.add( processRow( r3 , lang+"-TV_1" , fmin2, fmax2) );

            
        }
        
        MultiChart.open( FSQS1, "1 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true );
        MultiChart.open( FSQS2, "2 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true );
        MultiChart.open( FSQS3, "3 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true );
        MultiChart.open( FSQS4, "4 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true );
        MultiChart.open( FSQS5, "5 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true );
        MultiChart.open( FSQS6, "6 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true );
        MultiChart.open( FSQS7, "7 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true );
        MultiChart.open( FSQS8, "8 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true );

        String folder = "./RUN2/";
        String c1 = "no comment";

        
        MultiChart.openAndStore( FSQS1, "1 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true, folder, "FSQ1", c1  );
        MultiChart.openAndStore( FSQS2, "2 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true, folder, "FSQ2", c1  );
        MultiChart.openAndStore( FSQS3, "3 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true, folder, "FSQ3", c1   );
        MultiChart.openAndStore( FSQS4, "4 alpha( s,q ) (" + fmin1 +", " +fmax1+")" , "q", "alpha", true, folder, "FSQ4", c1   );
        MultiChart.openAndStore( FSQS5, "5 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true, folder, "FSQ5", c1   );
        MultiChart.openAndStore( FSQS6, "6 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true, folder, "FSQ6", c1   );
        MultiChart.openAndStore( FSQS7, "7 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true, folder, "FSQ7", c1   );
        MultiChart.openAndStore( FSQS8, "8 alpha( s,q ) (" + fmin2 +", " +fmax2+")" , "q", "alpha", true, folder, "FSQ8", c1   );
 
        if( ! justMFDFA ) {

            String fn1a = "nodes_total"+ "_" + DOlog;
            String fn2a = "nodes"+ "_" + DOlog;
            String fn3a = "links_total"+ "_" + DOlog;
            String fn4a = "links"+ "_" + DOlog;

            String fn1 = "edits_total" + "_" + DOlog;
            String fn2 = "edits"+ "_" + DOlog;
            String fn3 = "volume_total"+ "_" + DOlog;
            String fn4 = "volume"+ "_" + DOlog;


            String fnR1 = "ratioA_NN_LN"+ "_" + DOlog;
            String fnR2 = "ratioB_NN_EN"+ "_" + DOlog;
            String fnR3 = "ratioC_NN_TV"+ "_" + DOlog;
            String fnR4 = "ratioD_LN_EN"+ "_" + DOlog; 
            String fnR5 = "ratioE_LN_TV"+ "_" + DOlog;
            String fnR6 = "ratioF_TV_EN"+ "_" + DOlog;

            MultiChart.setDefaultRange = false;
            MultiChart.initColors(colors);

            MultiChart.openAndStore( ratioA, "ratio NN LN", "t in " + dt + "s", "#", true, folder, fnR1, c1 );
            MultiChart.openAndStore( ratioB, "ratio NN EN", "t in " + dt + "s", "#", true, folder, fnR2, c1 );
            MultiChart.openAndStore( ratioC, "ratio NN TV", "t in " + dt + "s", "#", true, folder, fnR3, c1 );
            MultiChart.openAndStore( ratioD, "ratio LN EN", "t in " + dt + "s", "#", true, folder, fnR4, c1 );
            MultiChart.openAndStore( ratioE, "ratio LN TV", "t in " + dt + "s", "#", true, folder, fnR5, c1 );
            MultiChart.openAndStore( ratioF, "ratio TV EN", "t in " + dt + "s", "#", true, folder, fnR6, c1 );

            MultiChart.open( ratioA, "A ratio NN LN", "t in " + dt + "s", "#", true );
            MultiChart.open( ratioB, "B ratio NN EN", "t in " + dt + "s", "#", true );
             MultiChart.open( ratioC, "C ratio NN TV", "t in " + dt + "s", "#", true );
            MultiChart.open( ratioD, "D ratio LN EN", "t in " + dt + "s", "#", true );
             MultiChart.open( ratioE, "E ratio LN TV", "t in " + dt + "s", "#", true );
            MultiChart.open( ratioF, "F ratio TV EN", "t in " + dt + "s", "#", true );

            MultiChart.openAndStore( rowsA1, "number of nodes", "t in " + dt + "s", "#", true, folder, fn1a, c1 );
            MultiChart.openAndStore( rowsA2, "growht (nodes per " + dt + ")", "t in " + dt + "s", "#", true, folder, fn2a, c1);
            MultiChart.openAndStore( rowsA3, "number of links", "t in " + dt + "s", "#", true, folder, fn3a, c1 );
            MultiChart.openAndStore( rowsA4, "growth (links per " + dt + ")", "t in " + dt + "s", "#", true, folder, fn4a, c1);

            MultiChart.openAndStore(rowsB1, "total edits", "t in " + dt + "s", "#", true, folder, fn1, c1);
            MultiChart.openAndStore(rowsB2, "edits per " + dt + " (activity)", "t in " + dt + "s", "#", true, folder, fn2, c1);
            MultiChart.openAndStore(rowsB3, "total text volume (size)", "t in " + dt + "s", "#", true, folder, fn3, c1);
            MultiChart.openAndStore(rowsB4, "text volume change", "t in " + dt + "s", "#", true, folder, fn4, c1);

            MultiChart.open( rowsA1, "number of nodes", "t in " + dt + "s", "#", true );
            MultiChart.open( rowsA2, "growht (nodes per " + dt + ")", "t in " + dt + "s", "#", true);
            MultiChart.open( rowsA3, "number of links", "t in " + dt + "s", "#", true );
            MultiChart.open( rowsA4, "growth (links per " + dt + ")", "t in " + dt + "s", "#", true);

            MultiChart.open(rowsB1, "total edits", "t in " + dt + "s", "#", true );
            MultiChart.open(rowsB2, "edits per " + dt , "t in " + dt + "s", "#", true  );
            MultiChart.open(rowsB3, "total text volume (size)", "t in " + dt + "s", "#", true );
            MultiChart.open(rowsB4, "text volume change", "t in " + dt + "s", "#", true );
    //        
            MesswertTabelle mwt = new MesswertTabelle();
            mwt.singleX = false;
            mwt.fill_UP_VALUE = 0.0;

            File fp = new File( "./TABS" );
            mwt.createParrentFile(fp);

            mwt.setMessReihen(rowsA1);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn1a + ".csv" ) );

            mwt.setMessReihen(rowsA2);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn2a + ".csv" ) );

            mwt.setMessReihen(rowsA3);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn3a + ".csv" ) );

            mwt.setMessReihen(rowsA4);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn4a + ".csv" ) );

            mwt.setMessReihen(rowsB1);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn1 + ".csv" ) );

            mwt.setMessReihen(rowsB2);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn2 + ".csv" ) );

            mwt.setMessReihen(rowsB3);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn3 + ".csv" ) );

            mwt.setMessReihen(rowsB4);
            mwt.writeToFile( new File( "./TABS/TAB_" + fn4 + ".csv" ) );


                    mwt.setMessReihen(ratioA);
            mwt.writeToFile( new File( "./TABS/TAB_" + fnR1 + ".csv" ) );

                    mwt.setMessReihen(ratioB);
            mwt.writeToFile( new File( "./TABS/TAB_" + fnR2 + ".csv" ) );

                    mwt.setMessReihen(ratioC);
            mwt.writeToFile( new File( "./TABS/TAB_" + fnR3 + ".csv" ) );

                    mwt.setMessReihen(ratioD);
            mwt.writeToFile( new File( "./TABS/TAB_" + fnR4 + ".csv" ) );

                    mwt.setMessReihen(ratioE);
            mwt.writeToFile( new File( "./TABS/TAB_" + fnR5 + ".csv" ) );

                    mwt.setMessReihen(ratioF);
            mwt.writeToFile( new File( "./TABS/TAB_" + fnR6 + ".csv" ) );



            DataFactory2.dump();
        
        }
        
        

    }

    public static void _test2(Vector<Messreihe> mr) {
        MultiDFATool dfaTool = new MultiDFATool();
        dfaTool.runDFA(mr, 2);
        System.out.println("done... ");
    }

    private static Messreihe processRow(Messreihe rBMM1, String label, double fMIN, double fMAX) throws Exception {
        stdlib.StdRandom.initRandomGen(1);
        
        StringBuffer hsb = new StringBuffer();

        Messreihe FSQ = new Messreihe();
        
        IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(
                DetrendingMethodFactory.MFDFA2);

        int order = dfa.getPara().getGradeOfPolynom();

        //        dfa.getPara().setzSValues(6000);

        System.out.print(dfa.getClass());

//        int N = (int) Math.pow(2.0, 18);
        int N = rBMM1.xValues.size();
        dfa.setNrOfValues(N);

        
        // die Werte für die Fensterbreiten sind zu wählen ...
        dfa.initIntervalS_version5();
//        dfa.showS();

        StringBuffer calcLog = new StringBuffer();

        Vector<Messreihe> v = new Vector<Messreihe>();

        calcLog.append("[" + fMIN + ", " + fMAX + "]\n");

        FSQ.setLabel("FSQ_"+rBMM1.getLabel() );


        // for (int i = -10; i < 11; i = i + 1) {
        for (int i = 1; i < 11; i = i + 1) {

            // Messreihe rBMM1 = (Messreihe) TestDataFactory.getDataSeriesBinomialMultifractalValues( N , 0.75 );
            // Messreihe rBMM2 = (Messreihe) TestDataFactory.getDataSeriesRandomValues( 65536 );

//            MFDFA.debug = true;

            System.out.println(i + " : " + rBMM1.getLabel());

            //MessreiheFFT temp5 = mr5_NEW.getModifiedFFT_INV( 0.1 * i );

            dfa.setZR(rBMM1.getData()[1]);
            double q = i * 1.0;


            DecimalFormat df = new DecimalFormat("0.000");
            
            if (i != 0) {

                dfa.getPara().setQ(q);
                dfa.calc();

                dfa.setLogBinning( true );
                
                Messreihe mr5 = dfa.getResultsMRLogLog();

                
                
                int mToStore = 20;
                for( int m = 10; m < 35; m = m + 1 ) {
                
                    double fMINc = m / 10.0;
                    double fMAXc = m / 10.0 + 0.5;
                    double sMIDDLE = (fMAXc - fMINc) / 2.0;
                    
                    SimpleRegression sr = mr5.linFit( fMINc, fMAXc);
                    String line = " alpha( q=" + q + ", ("+fMINc+"..."+fMAXc+")) = " + df.format( sr.getSlope() );
                    System.out.println(line);

                    hsb.append( df.format( sr.getSlope() ) + "\t" );
                    
                    if( m == mToStore ) {

                        mr5.setLabel(rBMM1.getLabel() + " (" + q + ")");
                        v.add(mr5);

                        calcLog.append(line + "\n");
                        FSQ.addValuePair(q, sr.getSlope());
                    }
                }
                hsb.append("\n");
                
                //        k.add(dfa.getZeitreiheMR());
                //        k.add(dfa.getProfilMR());

                String status = dfa.getStatus();
                double[][] results = dfa.getResults();

                System.out.println("> DFA-Status: " + "\n" + status + "\n#s=" + results[1].length);

            }

        }

        String fn = "FS_" + label;
        
        MultiChart.open(v, label + " : fluctuation function F(s,q)", "log(s)", "log(F(s))", true, calcLog.toString());
        MultiChart.openAndStore(v, label + " : fluctuation function F(s,q)", "log(s)", "log(F(s))", true, "./RUN2", fn, "n.c." );
        //MultiChart.open(k, "Kontrolldaten", "t", "y(t)", false, "?");

        String fnH = fn + "_HURST_MATRIX.csv";
        BufferedWriter bw = new BufferedWriter( new FileWriter( new File( "./RUN2/"+ fnH ) ) );

        bw.write( "# " + fnH + "\n");
        bw.write( "# X : s Werte (Spalten) " + "\n" );
        bw.write( "# Y : q Werte (Zeilen) " + "\n" );
        bw.write( hsb.toString());

        bw.flush();
        bw.close();
               
        System.out.println( ">>>" + fnH + "<<<" );
        System.out.println( ">>> X : s Werte (Spalten) " );
        System.out.println( ">>> Y : q Werte (Zeilen) " );
        System.out.println( hsb.toString() );
        
        return FSQ;
    }

    
}

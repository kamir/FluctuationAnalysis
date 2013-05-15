package statphys.detrending;

import charts.MultiChart;
import data.series.Messreihe;
import data.series.MessreiheFFT;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;
import statphys.detrending.methods.DFACore;
import statphys.detrending.methods.IDetrendingMethod;
import statphys.detrending.methods.MFDFA;
import stdlib.StdDraw;
import stdlib.StdStats;

public class MFDFATester {

    public static void main(String args[]) throws Exception {

        stdlib.StdRandom.initRandomGen(1);

        IDetrendingMethod dfa = DetrendingMethodFactory.getDetrendingMethod(
                DetrendingMethodFactory.MFDFA2);
        
        int order = dfa.getPara().getGradeOfPolynom();
        dfa.getPara().setzSValues( 6000 );
        
        System.out.print( dfa.getClass() );

        int N = (int)Math.pow(2.0, 18);
        dfa.setNrOfValues(N);

        // die Werte für die Fensterbreiten sind zu wählen ...
        dfa.initIntervalS();
        dfa.showS();

        StringBuffer calcLog = new StringBuffer();
        
        Vector<Messreihe> v = new Vector<Messreihe>();
        
        for( int i = -6; i < 6; i=i+2 ) {
        
            Messreihe rBMM1 = (Messreihe) TestDataFactory.getDataSeriesBinomialMultifractalValues( N , 0.75 );
            //Messreihe rBMM2 = (Messreihe) TestDataFactory.getDataSeriesRandomValues( 65536 );
            
//            MFDFA.debug = true;
            
            System.out.println( i + " : " + rBMM1.getLabel() );
            
            //MessreiheFFT temp5 = mr5_NEW.getModifiedFFT_INV( 0.1 * i );
            
            dfa.setZR(rBMM1.getData()[1]);
            double q = i*1.0;
            
            if ( i != 0 ) {

                dfa.getPara().setQ( q );
                dfa.calc();

                Messreihe mr5 = dfa.getResultsMRLogLog();

                SimpleRegression sr = mr5.linFit(1.5, 3.0);

                String line = " alpha = " + sr.getSlope();

                System.out.println( line );
                calcLog.append( line + "\n");

                mr5.setLabel( rBMM1.getLabel() + " ("+q+")" );
                v.add(mr5);

                //        k.add(dfa.getZeitreiheMR());
                //        k.add(dfa.getProfilMR());
                
                String status = dfa.getStatus();
                double[][] results = dfa.getResults();

                System.out.println("> DFA-Status: " + "\n" + status + "\n#s=" + results[1].length);

            }    

        }    


        MultiChart.open(v, "fluctuation function F(s,q)", "log(s)", "log(F(s))", true, calcLog.toString() );
        //MultiChart.open(k, "Kontrolldaten", "t", "y(t)", false, "?");
    }
    
    public static void _test2( Vector<Messreihe> mr ) { 
        MultiDFATool dfaTool = new MultiDFATool();
        dfaTool.runDFA(mr, 2);
        System.out.println( "done... ");
    };
}

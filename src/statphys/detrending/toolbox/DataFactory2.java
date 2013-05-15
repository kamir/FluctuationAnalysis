/*
 * : TODO :
 * 
 * KORREKTUR der EINLESE FUNKTION ...
 * 
 */
package statphys.detrending.toolbox;

import data.io.MessreihenLoader;
import data.series.Messreihe;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 *
 * @author kamir
 */
class DataFactory2 {
    
    public static final int binMode_SUM = 0;
    public static final int binMode_AV = 1;
    
    long tmax = 0;
    long tNull = 0;
            
    static void dump() {
        System.out.println( sb.toString() );
    }

    String delim = "\t";
    int lang = 0;
    MessreihenLoader loader = new MessreihenLoader();
    String folder = "X:/DATA/data.out/";
    
    
    Calendar cal = new GregorianCalendar();
    Calendar cal2 = new GregorianCalendar();

    void initFolder()  {
        
        String f = javax.swing.JOptionPane.showInputDialog("Pfad : " + "?" );
        folder = f + "/DATA/data.out/";
        
        System.out.println( folder );
    }
        
    void init_for_WikipediaWachstumsreihen(int i) {

        lang = i;
        
        cal = new GregorianCalendar();
        cal.set( Calendar.YEAR, MFDFAAnalyseTool.jahr_min );
        cal.set( Calendar.MONTH, 0 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        cal.set( Calendar.HOUR, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        
        cal2 = new GregorianCalendar();
        cal2.set( Calendar.YEAR, MFDFAAnalyseTool.jahr_max );
        cal2.set( Calendar.MONTH, 11 );
        cal2.set( Calendar.DAY_OF_MONTH, 31 );
        cal2.set( Calendar.HOUR, 23 );
        cal2.set( Calendar.MINUTE, 59 );
        cal2.set( Calendar.SECOND, 59 );
        
        
        tNull = cal.getTimeInMillis() / (3600 * 1000);

        tmax = cal2.getTimeInMillis() / (3600 * 1000);
    }
        
    static StringBuffer sb = new StringBuffer();
    
    int mode = 0;
    
    Messreihe getWikiRow(String NN, int skip, boolean dodelta, boolean dolog, int bm) throws FileNotFoundException, IOException {
        mode = 0;
        File f = new File( folder + lang + "-full_wiki.process.dat" );
        return loadData( NN, skip, dodelta, f, dolog, bm);
    }    
    
    Messreihe getRevisionsRow(String NN, int skip, boolean dodelta, boolean dolog, int bm) throws FileNotFoundException, IOException {
        mode = 1;
        File f = new File( folder + lang + "-full_revisions.process.dat" );
        return loadData( NN, skip, dodelta, f, dolog, bm);
    }    
        
    Messreihe loadData( String NN, int skip, boolean dodelta, File f , boolean dolog, int bm) throws FileNotFoundException, IOException {
    
        Messreihe mr = new Messreihe();
        mr.setLabel( lang + "_" + NN );
        
        BufferedReader br = new BufferedReader( new FileReader( f ));
        String l = br.readLine();
        System.out.println( l );
        int i = 0;

        long current_s = 0;
        long last_s = 0;
        double last_v = 0;
        
        boolean goOn = true;
        
        long FIRSTct = 0;
        
        double maxV = 0;
        
        while( br.ready() && goOn ) {
            
            if ( i > MFDFAAnalyseTool.max ) goOn = false;
            
            String line = br.readLine();
            
//            if ( skip == 6 && mode == 0 ) System.out.println( line );
            
            StringTokenizer st = new StringTokenizer( line );
//            System.out.println( f.getName() + " : " + st.countTokens() + " : " + line );
            
            // current time (UNIX)
            long ct = Long.parseLong( st.nextToken() );

            // skip some fields
            for( int s = 0; s < skip; s++ ) {
                st.nextToken();
            }

            // Value
            String a = st.nextToken();
            Double v = 1.0 * Long.parseLong(a);
            
            Double vv = v;
            if ( dolog && v != 0.0 ) vv = Math.log(v);
            
            if ( v > maxV ) maxV = v;
            
            // erster Wert ...
            if( i == 0 ) {
                
                Date tOFF = cal.getTime();
                
                // Hochzähl-Calendar
                Calendar ctr = new GregorianCalendar();
                ctr.setTime(tOFF);
                long TOFFSET = tOFF.getTime();
                
                // ZWISCHEN OFFSET und erstem Wert NULL einfügen
                while( ct > TOFFSET  && goOn ) { 

                    mr.addValuePair( scaleT( TOFFSET  / (1000*3600) ) , 0 );
      
                    ctr.add(Calendar.HOUR, 1);
                    TOFFSET = ctr.getTimeInMillis();
                    
                    if ( i > MFDFAAnalyseTool.max ) goOn = false;
                    
                    i++;
                }
                
                FIRSTct = ct;
                
                current_s = ( ct - tNull ) / (1000 * 3600);
                last_s = ( ct - tNull ) / (1000 * 3600);
                mr.addValuePair( scaleT( current_s ) , vv );
                i++;
            }
            else {
                current_s = ( ct - tNull ) / (1000 * 3600);
            
                long delta = current_s - last_s;
                int shift = (int) (delta);
                 
//                System.out.println( "shift: " + shift + "   " + v );
 
                // Lücke füllen ...
                if ( shift > 1 ) {
                    for( int j = 0; j < shift; j++) {
                        
                        long ttt = last_s + j + 1;
                        
                        if ( ttt > ( cal2.getTimeInMillis() / (3600 * 1000) ) ) 
                            goOn = false;
                        
                        if ( dodelta ) vv = 0.0;

                        mr.addValuePair( ttt, vv );
                        i++;
                    } 
                }
                
                if ( current_s > ( cal2.getTimeInMillis() / (3600 * 1000) ) ) goOn = false;
                    
                mr.addValuePair(current_s , v);
                i++;
               
                last_s = current_s;
                last_v = v;
            } 
        } 
        
        System.out.println( "\n\n>" + f.getAbsolutePath() );
        System.out.println( "> maxV=" + maxV );
        System.out.println( "> skip=" + skip );
        
        if ( skip == 6 && maxV > 8E6 && mode == 0 ) System.exit( -1 );
        
        sb.append( f.getName() + " " + FIRSTct + " " + new Date(FIRSTct) + "\n" );

        if ( MFDFAAnalyseTool.binning > 1 ) {
            
            switch( bm ) {
                case binMode_SUM : {
                    return mr.setBinningX_sum( MFDFAAnalyseTool.binning ); 
                }
                    
                case binMode_AV : {
                    return mr.setBinningX_average( MFDFAAnalyseTool.binning );
                }    
            }
        }
        
        return mr;
        
    }

    private double scaleT(long t) {
        return t / MFDFAAnalyseTool.binning;
    }
    
    
    
}

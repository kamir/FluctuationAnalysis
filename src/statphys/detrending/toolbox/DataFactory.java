/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
class DataFactory {

    static void dump() {
        System.out.println( sb.toString() );
    }

    String delim = "\t";
    int lang = 0;
    MessreihenLoader loader = new MessreihenLoader();
    String folder = "X:/DATA/data.out/";
    
    Calendar cal = new GregorianCalendar();
    Calendar cal2 = new GregorianCalendar();

        
    void init_for_WikipediaWachstumsreihen(int i) {

        lang = i;
        
        cal = new GregorianCalendar();
        cal.set( Calendar.YEAR, 2000 );
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
    }
        
    static StringBuffer sb = new StringBuffer();
    

    Messreihe getWikiRow(String NN, int skip, boolean dodelta) throws FileNotFoundException, IOException {
        Messreihe mr = new Messreihe();
        mr.setLabel( lang + "_" + NN );
        
        File f = new File( folder + lang + "-full_wiki.process.dat" );
        
        BufferedReader br = new BufferedReader( new FileReader( f ));
        String l = br.readLine();
        System.out.println( l );
        int i = 0;

        long current_s = 0;
        long last_s = 0;
        double last_v = 0;
        
        Date t0 = cal.getTime();
        Date t1 = cal2.getTime();
        
        long TNULL = t0.getTime();
        
        System.out.println( ">>>  t0=" +( TNULL / (3600 * 1000) ));
        
        boolean goOn = true;
        long FIRSTct = 0;
        while( br.ready() && goOn ) {
            
////            if ( i > 10 ) goOn = false;
            
            String line = br.readLine();
//            System.out.println( f.getName() + " " + line );
            
            StringTokenizer st = new StringTokenizer( line );
//            System.out.println( st.countTokens() );
            
            long ct = Long.parseLong( st.nextToken() );

            for( int s = 0; s < skip; s++ ) {
//                System.out.print( + " " )
                st.nextToken();
            }
//            System.out.println();
            
            String a = st.nextToken();
            double v = Double.parseDouble(a);
            
//            System.out.println( "  i=" + i + "  v=" + v );
            
            if( i == 0 ) {
                
                Date tOFF = new Date( t0.getTime() );
                
                Calendar ctr = new GregorianCalendar();
                ctr.setTime(tOFF);
                long TOFFSET = tOFF.getTime();
                
                
                while( ct > TOFFSET  ) { 
                    mr.addValuePair( TOFFSET / (1000*3600) , 0 );
                    ctr.add(Calendar.HOUR, 1);
                    TOFFSET = ctr.getTimeInMillis();
                } 
//                    
                
                
                FIRSTct = ct;
                
                current_s = ( ct - TNULL ) / (1000 * 3600);
                last_s = ( ct - TNULL ) / (1000 * 3600);
                mr.addValuePair(current_s , v );
                i++;
            }
            else {
                current_s = ( ct -TNULL ) / (1000 * 3600);
            
                long delta = current_s - last_s;
                int shift = (int) (delta);
                 
//                System.out.println( "shift: " + shift + "   " + v );
                
                double vv = v;
            
                if ( shift > 1 ) {
                    for( int j = 0; j < shift; j++) {
                        long ttt = last_s + j + 1;
                        if ( ttt > ( t1.getTime() / (3600 * 1000) ) ) 
                            goOn = false;
                        if ( dodelta ) vv = 0;

                        mr.addValuePair( last_s + j + 1, vv );
                    } 
                }
                
                if ( current_s > ( t1.getTime() / (3600 * 1000) ) ) goOn = false;
                    
                mr.addValuePair(current_s , v);
                i++;
               
                last_s = current_s;
                last_v = v;
            } 
        } 
        
        sb.append( f.getName() + " " + FIRSTct + " " + new Date(FIRSTct) + "\n" );

        if ( MFDFAAnalyseTool.binning > 1 ) return mr.setBinningX_sum( MFDFAAnalyseTool.binning ); 
        else return mr;

    }
    
    
    Messreihe getRevisionsRow(String NN, int skip, boolean dodelta) throws FileNotFoundException, IOException {
        Messreihe mr = new Messreihe();
        mr.setLabel( lang + "_" + NN );
        
        File f = new File( folder + lang + "-full_revisions.process.dat" );
        
        BufferedReader br = new BufferedReader( new FileReader( f ));
        String head = br.readLine();

        System.out.println( head );
        
        int i = 0;

        long current_s = 0;
        long last_s = 0;
        double last_v = 0;
        
        Date t0 = cal.getTime();
        Date t1 = cal2.getTime();
       
        long TNULL = t0.getTime();
        
        boolean goOn = true;
        while( br.ready() && goOn ) {
            
//            if ( i > 10 ) goOn = false;
            
            String line = br.readLine();
//            System.out.println( f.getName() + " " + line );
            
            StringTokenizer st = new StringTokenizer( line );
            long ct = Long.parseLong( st.nextToken() );
//            System.out.println( st.countTokens() );

            for( int s = 0; s < skip; s++ ) {
//                System.out.print( st.nextToken() + " " );
                st.nextToken() ;
            }
//            System.out.println();
            
            String a = st.nextToken();
            double v = Double.parseDouble(a);

            
            if( i == 0 ) {
                
                Date tOFF = new Date( t0.getTime() );
                
                Calendar ctr = new GregorianCalendar();
                ctr.setTime(tOFF);
                long TOFFSET = tOFF.getTime();
         
                while( ct > TOFFSET  ) { 
                    mr.addValuePair( TOFFSET / (1000*3600) , 0 );
                    ctr.add(Calendar.HOUR, 1);
                    TOFFSET = ctr.getTimeInMillis();
                } 
                
                current_s = ( ct - TNULL ) / (1000 * 3600);
                last_s = ( ct - TNULL ) / (1000 * 3600);
                mr.addValuePair( current_s , v );
                i++;
            }
            else {
                current_s = ( ct - TNULL ) / (1000 * 3600);
            
                long delta = current_s - last_s;
                int shift = (int) (delta);
                 
//                System.out.println( "shift: " + shift + "   " + v );

                double vv = v;
                

                if ( shift > 1 ) {
                    for( int j = 0; j < shift; j++) {
                        
                        if ( dodelta ) vv = 0;
                        long ttt = last_s + j + 1;
                        if ( ttt > ( t1.getTime() / (3600 * 1000) ) ) goOn = false;
                        mr.addValuePair( last_s + j + 1, vv );
                    } 
                }
                
                if ( current_s > ( t1.getTime() / (3600 * 1000) ) ) goOn = false;
                    
                mr.addValuePair(current_s , vv);
                
                i++;
               
                last_s = current_s;
                last_v = v;
            } 
        } 

        

        if ( MFDFAAnalyseTool.binning > 1 ) return mr.setBinningX_sum( MFDFAAnalyseTool.binning ); 
        else return mr;

    }

    
}

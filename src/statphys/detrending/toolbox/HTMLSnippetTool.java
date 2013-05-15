/**
 * 
 * Wandle ein data-array in Java-Script Code für ein Google-Chart.
 * 
 */
package statphys.detrending.toolbox;

import java.text.DecimalFormat;

/**
 *
 * @author kamir
 */
public class HTMLSnippetTool {
    
    
    
    public static String getHeatmapForHURST( int i, double[][] data, 
            double lsmin, double lsmax, double lds, 
            int smin, int smax, int ds,
            int qmin, int qmax, int dq,
            String title ) {

        StringBuffer sb = new StringBuffer();
        
        DecimalFormat df = new DecimalFormat("0.0");
        
        sb.append( "var data"+i+" = new google.visualization.DataTable(); \n" );

        for( double ls = lsmin; ls <= lsmax; ls = ls+lds ) {
            sb.append( "data"+i+".addColumn('string', 's="+ df.format(ls)+"' ); \n" );
            
        }

        int rows = (int)(qmax - qmin + 1);
        
        sb.append( "data"+i+".addRows("+rows+"); \n" );
        
        int Q = 0;
        int S = 0;
        
         
        for( int q = qmin; q <= qmax; q = q+dq ) {
            for( int s = smin; s <= smax; s = s+ds ) {
                sb.append( "data"+i+".setCell( " + (q-1) + ", " + (s-1) + ", " + data[s-1][q-1] + ");\n"  ); 
            }
        }    
        
        sb.append( "heatmap"+i+" = new org.systemsbiology.visualization.BioHeatMap(document.getElementById('heatmapContainer'));\n");
        sb.append( "heatmap"+i+".draw(data1, {});\n");
        
        
        return sb.toString();
    }

    public static void main(String[] args) {
    
        double[][] data = new double[15][10];
        
        for( int s = 0; s < 15; s++ ) {
            for( int q = 0; q < 10; q++ ) {
                data[s][q] = Math.random();
            }
        }  
        
        System.out.println( getHeatmapForHURST(1, data, 2.0, 3.5, 0.1, 1, 15, 1,1,10,1, null)  );
       
        
    }
 

    
    
    
}

cd ..
cd dist
java -cp FluctuationAnalysis.jar statphys.detrending.MultiDFA4Buckets ./../data/ en_ons0_IDS.dat.tsb.vec.seq 5 1 5 1.2 -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl -Xms1024m 

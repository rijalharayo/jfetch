module io.github.rijalharayo.jfetch {
     requires transitive java.net.http;
     requires transitive com.fasterxml.jackson.databind;

     requires com.fasterxml.jackson.datatype.jsr310;

     exports com.jfetch;
}
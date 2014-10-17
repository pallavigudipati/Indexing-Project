import java.io.IOException;

import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;


public class Parser {

    static public void main(String arg[]) {
        ProgressLogger pl = new ProgressLogger();
        try {
            ImmutableGraph graph = ImmutableGraph.loadMapped("datasets/uk-2006-08/uk-2006-08", pl);
                    //ImmutableGraph.load("datasets/uk-2006-08/uk-2006-08", pl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

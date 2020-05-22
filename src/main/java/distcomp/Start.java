/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distcomp;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

public class Start {

    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                e.printStackTrace(); // replace with more robust error handling
                return TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if (e != null) return handleException(e);
                Files.delete(dir);
                return CONTINUE;
            }
        });
    }

    ;

    public static void main(String[] args) throws Exception {

        File activemqData = new File("activemq-data");
        if (activemqData.exists()) {
            System.out.println("Deleting ActiveMQ database...");
            deleteFileOrFolder(activemqData.toPath());
        }

        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E", "F");

        Map<String, Map<String, Integer>> topologyMap = Topology.generateTopologyMap(nodes);

        Dijkstra dijkstra = Dijkstra.getInstance(topologyMap);

        NodeA a = new NodeA(topologyMap);
        NodeB b = new NodeB(topologyMap);
        NodeC c = new NodeC(topologyMap);
        NodeD d = new NodeD(topologyMap);
        NodeE e = new NodeE(topologyMap);
        NodeF f = new NodeF(topologyMap);

        b.start();
        c.start();
        d.start();
        e.start();
        f.start();
        a.start();

        a.sendToNode("A", "F");
    }
}

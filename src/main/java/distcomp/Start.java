/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distcomp;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Start {
    
public static void deleteFileOrFolder(final Path path) throws IOException {
  Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
    @Override public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
      throws IOException {
      Files.delete(file);
      return CONTINUE;
    }

    @Override public FileVisitResult visitFileFailed(final Path file, final IOException e) {
      return handleException(e);
    }

    private FileVisitResult handleException(final IOException e) {
      e.printStackTrace(); // replace with more robust error handling
      return TERMINATE;
    }

    @Override public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
      throws IOException {
      if(e!=null)return handleException(e);
      Files.delete(dir);
      return CONTINUE;
    }
  });
};

    public static void main (String[] args) throws Exception {
        
        File activemqData = new File("activemq-data");
        if(activemqData.exists())
        {
            System.out.println("Deleting ActiveMQ database...");
            deleteFileOrFolder(activemqData.toPath());
        }

        NodeA a = new NodeA();
        NodeB b = new NodeB();
        NodeC c = new NodeC();
        NodeD d = new NodeD();
        NodeE e = new NodeE();
        NodeF f = new NodeF();

        f.setAsRoot();

        b.start();
        c.start();
        d.start();
        e.start();
        f.start();
        a.start();

        /*
        final ProcessE processE = new ProcessE();
        processE.start();
        
        final ProcessA processA = new ProcessA();
        final ProcessB processB = new ProcessB();
        final ProcessC processC = new ProcessC();
        final ProcessD processD = new ProcessD();
        processA.start();
        processB.start();
        processC.start();
        processD.start();

        System.out.println("Press enter to exit.");
        System.in.read();

        processA.destroy();
        processB.destroy();
        processC.destroy();
        processD.destroy();
        processE.destroy();*/


    }
}

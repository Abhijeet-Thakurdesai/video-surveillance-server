package server;

import util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import swinghelper.SwingUtilities;



public class FileTree extends JPanel {
  /** Construct a FileTree */  
  public FileTree(File dir) {
    setLayout(new BorderLayout());

    // Make a tree list with all the nodes, and make it a JTree
    JTree tree = new JTree(addNodes(null, dir));

    // Add a listener
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
            .getPath().getLastPathComponent();
                    String p = e.getPath().getParentPath().toString();
                    
                    p = p.replaceAll("\\]", "");
                    p = p.replaceAll("\\[", "");
                 
                      System.out.println("You selected " +node.getParent().toString()+"\\"+ node.toString() );
//                      Runtime r=Runtime.getRuntime();
//                      
//                   r.exec();  
               
                      
                  StringHelper.runcommand("cmd /c  "+node.getParent().toString()+"\\"+ node.toString() );
//        Desktop.getDesktop().open(new File())
                } catch (Exception ex) {
                    Logger.getLogger(FileTree.class.getName()).log(Level.SEVERE, null, ex);
                }
//        Desktop.getDesktop().open(new File())
      }
    });

    // Lastly, put the JTree into a JScrollPane.
    JScrollPane scrollpane = new JScrollPane();
    scrollpane.getViewport().add(tree);
    add(BorderLayout.CENTER, scrollpane);
  }

  /** Add nodes from under "dir" into curTop. Highly recursive. */
  DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
    String curPath = dir.getPath();
    DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
    if (curTop != null) { // should only be null at root
      curTop.add(curDir);
    }
    Vector ol = new Vector();
    String[] tmp = dir.list();
    for (int i = 0; i < tmp.length; i++)
      ol.addElement(tmp[i]);
    Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
    File f;
    Vector files = new Vector();
    // Make two passes, one for Dirs and one for Files. This is #1.
    for (int i = 0; i < ol.size(); i++) {
      String thisObject = (String) ol.elementAt(i);
      String newPath;
      if (curPath.equals("."))
        newPath = thisObject;
      else
        newPath = curPath + File.separator + thisObject;
      if ((f = new File(newPath)).isDirectory())
        addNodes(curDir, f);
      else
        files.addElement(thisObject);
    }
    // Pass two: for files.
    for (int fnum = 0; fnum < files.size(); fnum++)
      curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
    return curDir;
  }

  public Dimension getMinimumSize() {
    return new Dimension(200, 400);
  }

  public Dimension getPreferredSize() {
    return new Dimension(200, 400);
  }

  /** Main: make a Frame, add a FileTree */
  public static void main(String[] av) {

  FileTree d=new FileTree(new File("E:/info"));
  d.showFileViewer("E:/info");
  }
    
   /** Main: make a Frame, add a FileTree */
  public void showFileViewer(String filePath) {

    JFrame frame = new JFrame("FileTree");

    frame.setForeground(Color.black);
    frame.setBackground(Color.lightGray);
    frame.setSize(100,400);
     frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/img/browsevideo.png"));
    SwingUtilities.setScreenCenter(frame);
    Container cp = frame.getContentPane();

    //if (av.length == 0) {
      cp.add(new FileTree(new File(filePath)));
//    } else {
//      cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
//      for (int i = 0; i < av.length; i++)
//        cp.add(new FileTree(new File(av[i])));
//    }
 
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
  }
  
}

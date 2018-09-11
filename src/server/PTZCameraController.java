package server;

import java.awt.Toolkit;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import message.Sender;
import util.ConnectionManager;
import util.FaceDetection;
import util.OpenCVHelper;
import util.ServerConstants;
import util.StringHelper;

public class PTZCameraController extends javax.swing.JFrame {

    public static CvCapture capture = null;
    boolean breakLoop = false;
    SwingWorker sw = null;
    public static BufferedImage buffimg = null;
    private int cameraId = 0;
    private int quality = 0;

    public PTZCameraController() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Security Systems - Home Screen");
        setIconImage(Toolkit.getDefaultToolkit().getImage("src/img/smart.png"));
        initComponents();

    }
    int i = 0;

    public PTZCameraController(int cameraId, int quality) {

        System.out.println("one");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage("src/img/smart.png"));
        setTitle("User Registration");
        setTitle("Security Systems - Home Screen");
        initComponents();
        this.cameraId = cameraId;
        this.quality = quality;
        jTextField3.setText(cameraId+"");
        jComboBox2.setSelectedIndex(quality);
        jCheckBox1.setSelected(ServerConstants.NORMAL_MODE);
    }

    public void startCamera(int cameraId) {

        breakLoop = false;
        capture = cvCreateCameraCapture(cameraId);
        System.out.println("Camera  ===> " + cameraId);
        sw = new SwingWorker() {

            IplImage frame = null;
            boolean flag = false;

            @Override
            protected Object doInBackground() {
                try {
//                    int index = 0;
                    CvVideoWriter writer = null;
                    CvSize cs = new CvSize();
//                    int isColor = 1;
                    int fps = 5;  // or 30
                    cs.height(ServerConstants.IMG_HEIGHT);
                    cs.width(ServerConstants.IMG_WIDTH);
                    String path = ServerConstants.VIDEOSTREAMING_FOLDER_NAME + System.currentTimeMillis() + ".avi";
//                    writer = cvCreateVideoWriter(path,
//                            CV_FOURCC('F', 'L', 'V', '1'), fps, cs, 1);

                    while (!breakLoop) {
                        if (capture != null) {

                            long startTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - startTime) < 1 * ServerConstants.VIDEO_DURATION_LENGTH * 1000) {
                                if (breakLoop) {
                                    break;
                                }
                           
                                cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, ServerConstants.IMG_WIDTH);
                                cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, ServerConstants.IMG_HEIGHT);
                                try {
                                    frame = cvRetrieveFrame(capture);

                                    if (frame != null) {
                                        OpenCVHelper.writeFrame(frame, "Video : " + StringHelper.formatdate(new Date()));
                                        buffimg = frame.getBufferedImage();
//                                        cvWriteFrame(writer, frame);

                                        

                                        BufferedImage bi = new FaceDetection().face_detect(frame);
                                        int fc = 0;
                                        if (ServerConstants.NORMAL_MODE) {  // day mode
                                            cvWriteFrame(writer, frame);
                                            fc = 3;
                                        } else {
                                            fc = 0;
                                        }
                                        if (FaceDetection.face_count > fc) {
                                            if (!flag) {
                                                JOptionPane.showMessageDialog(null, "Notifying Admin "+FaceDetection.face_count);
                                                notifyAdmin();
                                                flag = true;
                                            }
                                        }
                                        jLabel1.setIcon(new ImageIcon(bi));
                                    }
                                } catch (Exception e) {
                                    System.out.println("Error 2");
                                    e.printStackTrace();
                                }
                            }

                            cvReleaseVideoWriter(writer);
                            System.out.println("Path is " + path);
                            if (!breakLoop) {
                                i++;
                                path = ServerConstants.VIDEOSTREAMING_FOLDER_NAME + System.currentTimeMillis() + ".avi";
                                cs.height(ServerConstants.IMG_HEIGHT);
                                cs.width(ServerConstants.IMG_WIDTH);

//                                writer = cvCreateVideoWriter(path,
//                                        CV_FOURCC('F', 'L', 'V', '1'), fps, cs, 1);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        if (frame != null) {
                            frame.release();
                            frame = null;
                        }
                        if (capture != null) {
                            cvReleaseCapture(capture);
                            capture = null;
                        }
                    } catch (Exception ex) {
                        System.out.println("Release error handled.");
                        ex.printStackTrace();

                    }
                } finally {
                    System.out.println("Finally here .");
                    return (null);
                }
            }

            @Override
            protected void done() {
                System.out.println("Swingworker Job Done............... ");
            }
        };
        sw.execute();

    }

    public void exitApp() {
        System.out.println("Window Closing Event.....");
        stopCamera();
        this.setVisible(false);
        this.dispose();



    }

    public void notifyAdmin() {

        new Thread() {

            public void run() {
                try {
                    ConnectionManager.saveActivityLog("Intrusion - Sending SMS on " + ServerConstants.SECURITY_PHONE);
new Sender(ServerConstants.SECURITY_PHONE,
 ServerConstants.MESSAGE ).send();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();


        Thread playAlarm = new Thread() {

            public void run() {
                try {
                    ConnectionManager.saveActivityLog("Intrusion - Playing Alarm");
                       StringHelper.runcommand("cmd /k \""+new java.io.File (ServerConstants.VOICE_CLIP).getCanonicalPath()+"\"");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        playAlarm.start();


    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Window Capture");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14));
        jLabel3.setText("Height");

        jLabel2.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14));
        jLabel2.setText("Width");

        jComboBox2.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "320x240", "640x480", "800x600", "1280x720", "1280x1024", "2048x1536", " " }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14));
        jLabel5.setText("Camera ");

        jTextField3.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 12)); // NOI18N
        jTextField3.setText("0");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Copperplate Gothic Light", 1, 12));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/start.png"))); // NOI18N
        jButton2.setText("Start");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Copperplate Gothic Light", 1, 12));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/stop.png"))); // NOI18N
        jButton1.setText("Stop");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Copperplate Gothic Light", 1, 12));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/exit1.png"))); // NOI18N
        jButton3.setText("Exit");
        jButton3.setPreferredSize(new java.awt.Dimension(93, 41));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 14));
        jCheckBox1.setText("Normal Mode");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(171, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jCheckBox1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel3)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
                .addGap(63, 63, 63))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        stopCamera();
        jButton2.setEnabled(true);
        jCheckBox1.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    public void startApp() {
        final boolean windowShowing = this.isShowing();
//        int cameraId = 0;
        if (windowShowing) {
            jLabel1.setSize(ServerConstants.IMG_WIDTH, ServerConstants.IMG_HEIGHT);
            jButton2.setEnabled(false);
            jCheckBox1.setEnabled(false);
            cameraId = StringHelper.n2i(jTextField3.getText());
        }
        System.out.println("ServerConstants.DAY_MODE " + ServerConstants.NORMAL_MODE);



        ConnectionManager.saveActivityLog("Camera Mode Started");
        startCamera(cameraId);

    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        System.out.println("Starting Camera ");

        startApp();

    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitApp();
    }//GEN-LAST:event_formWindowClosing

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        exitApp();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        int selectedResolution = jComboBox2.getSelectedIndex();
        String wt = jComboBox2.getSelectedItem().toString();
        ServerConstants.IMG_WIDTH = StringHelper.n2i(wt.substring(0, wt.indexOf("x")));
        ServerConstants.IMG_HEIGHT = StringHelper.n2i(wt.substring(wt.indexOf("x") + 1));
        this.quality = selectedResolution;
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        System.out.println(jCheckBox1.isSelected());        // TODO add your handling code here:
        ServerConstants.NORMAL_MODE = jCheckBox1.isSelected();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    public void setMode(boolean mode) {
        ServerConstants.NORMAL_MODE = mode;
        jCheckBox1.setSelected(mode);
    }

    public static void main(String args[]) {
        Main.setTheme();
        PTZCameraController ig = new PTZCameraController();
        ig.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    public static javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    public static javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

    public void stopCamera() {
        try {
            System.out.println("In here stop");
            if (sw != null) {
                sw.cancel(true);
            }
            buffimg = null;
            sw = null;
            breakLoop = true;
            if (ServerConstants.NORMAL_MODE) {
                jCheckBox1.setSelected(false);
                ServerConstants.NORMAL_MODE = false;
            } else {
                if (capture != null) {
                    cvReleaseCapture(capture);
                    capture = null;
                }
            }




        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param cameraId the cameraId to set
     */
    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;


import com.googlecode.javacv.cpp.opencv_core;
import java.util.Date;
import static com.googlecode.javacv.cpp.opencv_core.*;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import server.PTZCameraController;

/**
 *
 * @author rajesh
 */
public class OpenCVHelper {  

    CvCapture capture = null;
    IplImage frame = null;

    public static void main(String args[]) {

  
        int width = 640, height = 480;
        new OpenCVHelper().recordVideoCVVideo(0, width, height);

    }

//    public void recordVideo(int cameraid) {
//
//        try {
//            int imageWidth = 1280;
//            int imageHeight = 720;
//            FrameRecorder recorder = new FFmpegFrameRecorder("video.mp4", imageWidth, imageHeight);
//
//            recorder.setCodecID(10);
//
//            recorder.setFormat("mp4");
//            recorder.init();
//            recorder.setPixelFormat(1);
//            recorder.start();
//            Thread.sleep(5000);
//            recorder.stop();
//
//        } catch (InterruptedException ex) {
//            Logger.getLogger(OpenCVHelper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(OpenCVHelper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void recordVideoCVVideo(int cameraid, int width, int height) {
        capture = cvCreateCameraCapture(cameraid);

        CvVideoWriter writer = null;
        CvSize cs = new CvSize();
        int isColor = 1;
        int fps = 5;  // or 30
        cs.height(height);
        cs.width(width);
        int i = 0;
        while (ServerConstants.NORMAL_MODE) {
            long startTime = System.currentTimeMillis();
            writer = cvCreateVideoWriter(ServerConstants.VIDEOSTREAMING_FOLDER_NAME + "new" + i + ".avi",
                    CV_FOURCC('F', 'L', 'V', '1'), fps, cs, 1);
            while ((System.currentTimeMillis() - startTime) < 1 * ServerConstants.VIDEO_DURATION_LENGTH * 1000) {
                cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, width);
                cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, height);
                frame = cvRetrieveFrame(capture);
                writeFrame(frame, new Date().toGMTString());
                cvWriteFrame(writer, frame);
                System.out.println("i= " + i);
            }
            cvReleaseVideoWriter(writer);
            i++;
        }
    }

    public void recordShowCVVideo(JLabel img, int cameraid) {
        capture = cvCreateCameraCapture(cameraid);

        CvVideoWriter writer = null;
       
        int i = 0;
        while (ServerConstants.NORMAL_MODE) {
             CvSize cs = new CvSize();
        int isColor = 1;   
        int fps = 5;  // or 30
        cs.height(ServerConstants.IMG_HEIGHT);
        cs.width(ServerConstants.IMG_WIDTH);
            long startTime = System.currentTimeMillis();
            String fname=ServerConstants.VIDEOSTREAMING_FOLDER_NAME + "new" + i + ".avi";
            writer = cvCreateVideoWriter(fname,
                    CV_FOURCC('F', 'L', 'V', '1'), fps, cs, 1);
            System.out.println("fname "+fname);
            while ((System.currentTimeMillis() - startTime) < 1 * ServerConstants.VIDEO_DURATION_LENGTH * 1000) {
                cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, ServerConstants.IMG_WIDTH);
                cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, ServerConstants.IMG_HEIGHT);
                    
                frame = cvRetrieveFrame(capture);  
                writeFrame(frame,"Video:"+StringHelper.formatdate(new Date()));
                if (img != null) {
                    img.setSize(ServerConstants.IMG_WIDTH, ServerConstants.IMG_HEIGHT);
                    PTZCameraController.buffimg=frame.getBufferedImage();
                    img.setIcon(new ImageIcon(PTZCameraController.buffimg));
                }
                 cvWriteFrame(writer, frame);
              
            }
            cvReleaseVideoWriter(writer);
            i++;
        }  
    } 

    public static void writeFrame(IplImage frame, String text) {
        CvPoint v = new CvPoint();
        v.x(frame.width() - 250);
        v.y(100);
        CvArr c = frame;
        CvFont font = new CvFont();
        double hScale = 0.1;
        double vScale = 0.1;  
        int lineWidth = 1;
        cvInitFont(font, CV_FONT_NORMAL, 1.0, hScale, vScale, 0, lineWidth);
        cvPutText(c, text, v, font, opencv_core.CvScalar.YELLOW);
    }
}


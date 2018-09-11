package util;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_objdetect;


import java.awt.image.BufferedImage;
import swinghelper.FileChooserHelper;
import swinghelper.image.operations.ImageHelper;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

public class FaceDetection {
    // The cascade definition to be used for detection.

    private static final String CASCADE_FILE =
            "C:\\opencv\\data\\haarcascades\\haarcascade_frontalface_alt.xml";
    public static String str = "";
public static int face_count = 0;

    public static void main(String[] args) throws Exception {
        System.out.println(new java.io.File("./sounds/alarm.mp3").getCanonicalPath());
        FaceDetection f = new FaceDetection();
        opencv_core.IplImage originalImage = opencv_highgui.cvLoadImage(FileChooserHelper.selectFile("D:\\work\\Virgil\\body", "image"), 1);
        BufferedImage bi = f.face_detect(originalImage);
    }

    public BufferedImage face_detect(opencv_core.IplImage originalImage) {
        // Load the original image.
        long start = System.currentTimeMillis();
        //opencv_core.IplImage originalImage = opencv_highgui.cvLoadImage(str, 1);
        FaceDetection.face_count=0;
        // We need a grayscale image in order to do the recognition, so we
        // create a new image of the same size as the original one.
        opencv_core.IplImage grayImage = opencv_core.IplImage.create(
                originalImage.width(),
                originalImage.height(),
                opencv_core.IPL_DEPTH_8U, 1);

        // We convert the original image to grayscale.
        cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);

        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

        // We instantiate a classifier cascade to be used for detection,
        // using the cascade definition.
        opencv_objdetect.CvHaarClassifierCascade cascade =
                new opencv_objdetect.CvHaarClassifierCascade(
                cvLoad(CASCADE_FILE));

        // We detect the faces.
        opencv_core.CvSeq faces = cvHaarDetectObjects(
                grayImage, cascade, storage, 1.1, 1, 0);

        // We iterate over the discovered faces and draw yellow rectangles
        // around them.
        int actualFaceCount=0;
        for (int i = 0; i < faces.total(); i++) {
            opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(faces, i));  
//               System.out.println(" w h "+(r.width()-r.x())+" "+(r.height()-r.y()));
            int facecount = new ImageHelper().skinColorDetectionTechnique1Pixels(originalImage.getBufferedImage(),r.x(),r.y(),r.x()+r.width(),r.y()+r.height());
          

                    if (facecount > 100) {

                        cvRectangle(originalImage, cvPoint(r.x(), r.y()),
                        cvPoint(r.x() + r.width(), r.y() + r.height()),
                        opencv_core.CvScalar.YELLOW, 1, CV_AA, 0);
                        actualFaceCount++;

                      
                    }

           

        }
  try{
          if(faces.total()==1){ // ENABLE_FACE_TRACKING
                opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(faces, 0));
                            if(ServerConstants.ENABLE_FACE_TRACKING){
                                int faceCenterX=r.x()+(r.width()/2);
                                int faceCenterY=r.y()+(r.height()/2);
                                  int diffx=faceCenterX-(originalImage.getBufferedImage().getWidth()/2);
                                //int diffy=faceCenterY-originalImage.getBufferedImage().getHeight()/2;
                                boolean isLeft=false;
                                if(diffx<0){
                                    isLeft=true;
                                }  
                                if(diffx!=0){
                                    int diffAbs=Math.abs(diffx);
                                    int perAnglePixels=originalImage.getBufferedImage().getWidth()/18;
                                    final int totalRotations=diffAbs/perAnglePixels;
                                   
                                    System.out.println("Rotations are "+diffAbs+"    "+isLeft);
                                    

                                    final boolean isL=isLeft;
                  
                    new Thread() {
                        public void run() {
                            super.run();
                            System.out.println("Sending Rotations "+totalRotations);
                            for(int i=0;i<totalRotations;i++){
                            if(isL)     // Right
                                SendRotationsMotor.sendData("R");
                            else
                                SendRotationsMotor.sendData("L");
                            }
                        }
                    }.start();
                                }
   


                            }

                        }
           }catch(Error e){
                e.printStackTrace();
            }
        FaceDetection.face_count=actualFaceCount;
        long end = System.currentTimeMillis();
//        System.out.println("time is " + (end - start));

//        Picture pic = new Picture(originalImage.getBufferedImage(), "Image");
//        pic.show();
        try{
            if(!storage.isNull())
   storage.release();
        }catch(Error e){
            e.printStackTrace();
        }

        return (originalImage.getBufferedImage());
    // Save the image to a new file.
    //cvSaveImage("D:\\work\\Virgil\\body\\cvface.jpg", originalImage);

    }
}

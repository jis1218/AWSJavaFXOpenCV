package application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import aws.FaceRekognition;
import camera.Camera;
import image.ImageRekognition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class SampleController implements Initializable {
	@FXML
	private Button button;
	@FXML
	private ImageView currentFrame;
	
	@FXML
	private BorderPane borderPane;
	
	private ScheduledExecutorService timer;
	
	private VideoCapture capture = new VideoCapture();
	
	private static int cameraId = 0;
	
	double absoluteFaceSize = 0;
	
	CascadeClassifier faceCascade = new CascadeClassifier();
	
	public static final String FRONTAL_FACE_ALT = "dependencies/opencv_algorithm/haarcascades/haarcascade_frontalface_alt.xml";
	
	FaceRekognition faceRekognition;
	
	Camera camera;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub		
		faceRekognition = new ImageRekognition();		
		camera = new Camera();		
		camera.getWebcam();
		camera.startWebcam();
		
	}

	
	@FXML
	protected void startCamera(ActionEvent event) {
		if(this.button.getText().equals("Start Camera")) {
			System.out.println("hello");
			faceCascade.load(FRONTAL_FACE_ALT);
			capture.open(cameraId);			
			//timer = Executors.newSingleThreadScheduledExecutor();
			timer = Executors.newScheduledThreadPool(2);
			
			timer.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Mat frame = grabFrame(false);
					
					Image imageToShow = Utils.matToFXImage(frame);
					
					updateImageView(currentFrame, imageToShow);
					
				}

			}, 0, 33, TimeUnit.MILLISECONDS);
			
			timer.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					//opencv에서 frame을 Mat 형태로 잡아줌
					Mat frame = grabFrame(true);
					
					Image imageToShow = Utils.matToFXImage(frame);
					//Frame에 이미지 올리는 과정
					updateImageView(currentFrame, imageToShow);	
					
					faceRekognition.requestRekognition(image);
					
				}	
			}, 0, 1000, TimeUnit.MILLISECONDS);			
			
			this.button.setText("Stop Camera");
		}else {
			capture.release();
			timer.shutdown();
			this.button.setText("Start Camera");
		}
	}
	
	// 카메라를 통해 frame을 가져오는 과정
	private Mat grabFrame(boolean check) {
		
		camera.getImage();
		
		Mat frame = new Mat();
		
		if(this.capture.isOpened()) {
			try {
				this.capture.read(frame);
				
				if(!frame.empty() && check) {
					detectAndDisplay(frame); //여기서 frame에 초록색 상자 씌워짐
				}
			}catch(Exception e) {
				System.err.println("Exception");
			}
		}		
		return frame;
	}
	
	//frame에 초록색 상자 씌워짐
	private void detectAndDisplay(Mat frame) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		if(this.absoluteFaceSize == 0) {
			int height = grayFrame.rows();
			if(Math.round(height*0.2f)>0) {
				absoluteFaceSize = Math.round(height*0.2f);
			}
		}
		
		faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE
				, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
		
		Rect[] facesArray = faces.toArray();
		if(facesArray.length!=0) {
			System.out.println("Hello it's me");
		}
		for(int i=0; i<facesArray.length; i++) {
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
		}
	}
	
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	
}

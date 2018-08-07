package image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import aws.FaceRekognition;
import aws.OnResultListener;
import auth.AWSAuth;

public class ImageRekognition implements FaceRekognition {
	AmazonRekognition rekognitionClient;
	AWSCredentials credentials;
	OnResultListener onResultListener;
	AWSAuth awsAuth;	
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub		
		awsAuth = new AWSAuth();

		credentials = awsAuth.getAWSCredentials();
		rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}
	
	//An image detected by OpenCV will be a parameter of this methods
	@Override
	public void requestRekognition(BufferedImage image) {
		// TODO Auto-generated method stub
		searchFacesMatch(image, "MyCollection");
	}

	@Override
	public void getRekognitionResult() {
		// TODO Auto-generated method stub
		
	}
	
	public void searchFacesMatch(BufferedImage bufferedImage, String collectionId) {
		
		ByteBuffer imageBytes = null;
		
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			ImageIO.write(bufferedImage,  "jpg", bs);
			bs.flush();
			byte[] imageIn = bs.toByteArray();
			
			imageBytes = ByteBuffer.wrap(imageIn);
			
		}catch(Exception e) {
			
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		Image image = new Image().withBytes(imageBytes);
		
		SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
				.withCollectionId(collectionId)
				.withImage(image)
				.withFaceMatchThreshold(90F)
				.withMaxFaces(3);
		
		List<FaceMatch> faceImageMatches = null;
		
		
		try {
			SearchFacesByImageResult searchFacesByImageResult = rekognitionClient.searchFacesByImage(searchFacesByImageRequest);
			faceImageMatches = searchFacesByImageResult.getFaceMatches();
			//after parsing, pass the person name to onResult() parameter
			
			
		}catch(Exception e) {
			System.out.println("cannot recognize human face");
		}
		
		if(faceImageMatches.size()==0) {
			System.out.println("There is no matched face");
			
			onResultListener.onDismatched();
			return;
		}
		
		String name = faceImageMatches.get(0).getFace().getExternalImageId();
		
		if(onResultListener != null) {
			System.out.println("Detected!!!! " + name);
			onResultListener.onMatched(name);
		}
		
//		for(FaceMatch face: faceImageMatches) {
//			try {
//				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
//				System.out.println(face.getSimilarity());
//			}catch(JsonProcessingException e) {
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void setOnResultListener(OnResultListener onResultListener) {
		this.onResultListener = onResultListener;
	}
}

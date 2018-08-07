package aws;

import java.awt.image.BufferedImage;

public interface FaceRekognition {
	public void initialize();
	public void release();
	public void requestRekognition(BufferedImage image);
	public void getRekognitionResult();
	public void setOnResultListener(OnResultListener onResultListener);
}

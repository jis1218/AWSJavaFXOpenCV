package application;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Utils {
	
	public static Image matToFXImage(Mat frame) {
		try	{
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		}catch(Exception e) {
			System.err.println("Cannot convert the Mat object: " + e);
			return null;
		}
	}
	
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				property.set(value);
			}			
		});
	}
	
	private static BufferedImage matToBufferedImage(Mat original) {
		/*//Mat을 받아서 sourcePixels에 옮긴다.
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width*height*channels];
		original.get(0, 0, sourcePixels);
		
		//create an empty image in matching format
		if(original.channels()>1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		
		//targetPixel에 Mat의 정보가 담겨있는 sourcePixel과 BufferedImage의 타입 정보를 담는다.
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);*/
		
		BufferedImage image = null;
		int type;
		if(original.channels()>1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}else {
			type = BufferedImage.TYPE_BYTE_GRAY;
		}
		
		image = new BufferedImage(original.width(), original.height(), type);
		//width*height 그리고 type 정보까지 들어갈 수 있는 길이의 byte array 생성
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		//byte array와 image가 연결이 되어 있는 것 같다.
		original.get(0, 0, data);
		
		return image;
	}

}

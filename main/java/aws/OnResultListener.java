package aws;

public interface OnResultListener {
	public void onMatched(String data);
	public void onDismatched();
	public void onError();
}

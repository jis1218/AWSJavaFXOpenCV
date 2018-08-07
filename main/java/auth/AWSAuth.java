package auth;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class AWSAuth {
    public AWSCredentials getAWSCredentials() {
    	AWSCredentials credentials;
    	
    	try {
    		credentials = new ProfileCredentialsProvider().getCredentials();
    		System.out.println(credentials.toString());    		
    		return credentials;
    	}catch(Exception e) {
    		throw new AmazonClientException("cannot");
    	}
    }

}

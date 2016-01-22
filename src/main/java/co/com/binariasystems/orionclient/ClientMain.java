package co.com.binariasystems.orionclient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import co.com.binariasystems.orion.model.dto.AccessTokenDTO;
import co.com.binariasystems.orion.model.dto.AuthenticationDTO;
import co.com.binariasystems.orion.model.dto.UserCredentialsDTO;
import co.com.binariasystems.orion.model.enumerated.Application;
import co.com.binariasystems.orionclient.dto.ClientCredentialsDTO;
import co.com.binariasystems.orionclient.security.SecurityClient;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		String servicesBaseURL = "http://127.0.0.1:8080/OrionSecurityWeb/services/";
		ClientCredentialsProvider credentialsProvider = new ClientCredentialsProvider() {
			public ClientCredentialsDTO getClientCredentials() {
				return new ClientCredentialsDTO("gestpymesoc", "Gana1111");
			}
		};
		ClientBuilder clientBuilder = ClientBuilder.getInstance(servicesBaseURL, credentialsProvider);
		SecurityClient securityClient = clientBuilder.createService(SecurityClient.class);
//		for(int i = 0; i < 4; i++){
		String usuarioAutenticar = "superadmin";
			Call<UserCredentialsDTO> serviceCaller = securityClient.findUserCredentials(usuarioAutenticar);
			Response<UserCredentialsDTO> serviceResponse = serviceCaller.execute();
			UserCredentialsDTO credentials = serviceResponse.body();
			if (credentials != null) {
				System.out.println("Credenciales.password: {" + credentials.getPassword() + "}");
				System.out.println("Credenciales.salt: {" + credentials.getPasswordSalt() + "}");
				AuthenticationDTO auth = new AuthenticationDTO();
				auth.setApplicationCode(Application.GESTPYMESOC.name());
				auth.setPassword("Gana1111");
				auth.setUsername(usuarioAutenticar);
				Call<AccessTokenDTO> saveAuthCall = securityClient.saveAuthentication(auth);
				AccessTokenDTO accessToken = saveAuthCall.execute().body();
				if(accessToken != null){
					System.out.println("TokenString: "+accessToken.getTokenString());
					System.out.println("User: "+accessToken.getUser());
					System.out.println("IsActive: "+accessToken.getIsActive());
					System.out.println("User: "+accessToken.getUser());
					System.out.println("CreationDate: "+accessToken.getCreationDate());
				}
			}
//			try{
//				Thread.sleep(5000);
//			}catch(InterruptedException ex){}
//		}
		
	}
	public static void mainsas(String[] args) {
		System.out.println(System.currentTimeMillis());
	}
}

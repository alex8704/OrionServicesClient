package co.com.binariasystems.orionclient;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import co.com.binariasystems.fmw.util.codec.Base64;
import co.com.binariasystems.orion.model.enumerated.SecurityExceptionType;
import co.com.binariasystems.orionclient.dto.ClientCredentialsDTO;


public class BasicHttpAuthenticationInterceptor implements Interceptor{
	private ClientCredentialsProvider credentialsProvider;
	public static final String ORION_ERROR_MSG_HEADER_NAME = "orion-service-errormsg";
	public static final String ORION_ERROR_TYPE_HEADER_NAME = "orion-service-errortype";
	private final static String ORION_ACCESSTOKEN_HEADER = "orionsec-access-token";
	private String authSchema = "Basic";
	private static final String PREFERENCE_KEY = "OrionSecurityWeb.AuthCookies";
	private static final String SESSION_COOKIE_PREFIX = "JSESSIONID";
	private final ConcurrentMap<String, Object> inMemoryPreferences = new ConcurrentHashMap<String, Object>();
	
	public Response intercept(Chain chain) throws IOException {
		Request original = chain.request();
		Request.Builder requestBuilder = original.newBuilder();
		if(credentialsProvider != null){
			ClientCredentialsDTO credentialsDTO = credentialsProvider.getClientCredentials();
			String basicAuthInfo = new StringBuilder(authSchema)
			.append(" ")
			.append(Base64.byteArrayToBase64((credentialsDTO.getClientUserAlias()+":"+credentialsDTO.getClientPassword()).getBytes()))
			.toString();
			requestBuilder.header("Authorization", basicAuthInfo)
			.method(original.method(), original.body());
			handleRequestCookies(requestBuilder);
		}
		Request request = requestBuilder.build();
		Response originalResponse = chain.proceed(request);
		handleResponseCookies(originalResponse);
		boolean hasErrorMsg = originalResponse.headers().get(ORION_ERROR_MSG_HEADER_NAME) != null;
		boolean hasErrorType = originalResponse.headers().get(ORION_ERROR_TYPE_HEADER_NAME) != null;
		if(!originalResponse.isSuccessful()){
			String errorMsg = hasErrorMsg ? originalResponse.header(ORION_ERROR_MSG_HEADER_NAME) : originalResponse.message();
			SecurityExceptionType exceptionType = hasErrorType ? SecurityExceptionType.valueOf(originalResponse.header(ORION_ERROR_TYPE_HEADER_NAME)) : null;
			throw new OrionClientException(exceptionType, errorMsg);
		}
		
		return originalResponse;
	}
	
	private void handleRequestCookies(Request.Builder requestBuilder){
		Set<String> cookies = (Set<String>) inMemoryPreferences.get(PREFERENCE_KEY);
		if(cookies == null)
			return;
		for (String cookie : cookies) 
			requestBuilder.addHeader("Cookie", cookie);
		if(inMemoryPreferences.get(ORION_ACCESSTOKEN_HEADER) != null)
			requestBuilder.addHeader(ORION_ACCESSTOKEN_HEADER, (String)inMemoryPreferences.get(ORION_ACCESSTOKEN_HEADER));
	}
	
	/**
	 * Almacena en memoria las cookies para el manejo de session contra los servicios
	 * de OrionSecurity, y retorna un booleano que indica si la respuesta es exitosa
	 * basandose en la cookie indicadora de error de Orion
	 * @param originalResponse
	 * @return
	 */
	private void handleResponseCookies(Response originalResponse){
		if (!originalResponse.headers("Set-Cookie").isEmpty()) {
			Set<String> cookies = (Set<String>) inMemoryPreferences.get(PREFERENCE_KEY);
			if(!inMemoryPreferences.containsKey(PREFERENCE_KEY))
				cookies = new HashSet<String>();
			List<String> headers = originalResponse.headers("Set-Cookie");
			/**
			 * Cada vez que la respuesta traiga el SESSIONID es porque
			 * se ha creado una nueva session y por tanto se reemplazan
			 * las cookies anteriores con las nuevas
			 */
            for (String header : headers) {
            	if(header.startsWith(SESSION_COOKIE_PREFIX)){
            		cookies.clear();
            		cookies.addAll(headers);
            		break;
            	}
            }
            
            if(originalResponse.header(ORION_ACCESSTOKEN_HEADER) != null){
            	inMemoryPreferences.put(ORION_ACCESSTOKEN_HEADER, originalResponse.header(ORION_ACCESSTOKEN_HEADER));
            }
            inMemoryPreferences.put(PREFERENCE_KEY, cookies);
        }
	}

	/**
	 * @return the credentialsProvider
	 */
	public ClientCredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	/**
	 * @param credentialsProvider the credentialsProvider to set
	 */
	public void setCredentialsProvider(ClientCredentialsProvider credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
	}
	
}

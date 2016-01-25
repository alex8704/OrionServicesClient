package co.com.binariasystems.orionclient;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class ClientBuilder {
	private static ClientBuilder instance;
	private OkHttpClient httpClient;
	private String servicesBaseURL;
	private ClientCredentialsProvider credentialsProvider;
	private Retrofit.Builder builder;
	private ConcurrentMap<Class, Object> serviceProxiesCache = new ConcurrentHashMap<Class, Object>();
	
	public static ClientBuilder getInstance(String servicesBaseURL, ClientCredentialsProvider credentialsProvider){
		if(instance == null){
			synchronized (ClientBuilder.class) {
				instance = new ClientBuilder(servicesBaseURL, credentialsProvider);
				instance.init();
			}
		}
		return instance;
	}
	
	private ClientBuilder(){}
	
	private ClientBuilder(String servicesBaseURL, ClientCredentialsProvider credentialsProvider) {
		this.servicesBaseURL = servicesBaseURL;
		this.credentialsProvider = credentialsProvider;
	}

	private void init(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>(){
			public Date deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
				return json == null ? null : new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});
		
		gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
			public JsonElement serialize(Date value, Type typeOf, JsonSerializationContext context) {
				return value == null ? null : new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(value));
			}
		});
		
		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
		if(credentialsProvider != null){
			BasicHttpAuthenticationInterceptor interceptor = new BasicHttpAuthenticationInterceptor();
			interceptor.setCredentialsProvider(credentialsProvider);
			httpClientBuilder.addInterceptor(interceptor);
		}
		
		httpClient = httpClientBuilder.build();
		builder = new Retrofit.Builder()
		.baseUrl(servicesBaseURL)
		.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
		
		
	}

	public <S> S createService(Class<S> serviceClass) {
		S serviceProxy = (S) serviceProxiesCache.get(serviceClass);
		if(serviceProxy == null){
			Retrofit retrofit = builder.client(httpClient).build();
			serviceProxy = retrofit.create(serviceClass);
			serviceProxiesCache.putIfAbsent(serviceClass, serviceProxy);
		}
		return serviceProxy;
	}
}

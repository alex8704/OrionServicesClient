package co.com.binariasystems.orionclient.dto;

public class ClientCredentialsDTO {
	private String clientUserAlias;
	private String clientPassword;
	
	public ClientCredentialsDTO(String clientUserAlias, String clientPassword) {
		this.clientUserAlias = clientUserAlias;
		this.clientPassword = clientPassword;
	}
	/**
	 * @return the clientUserAlias
	 */
	public String getClientUserAlias() {
		return clientUserAlias;
	}
	/**
	 * @param clientUserAlias the clientUserAlias to set
	 */
	public void setClientUserAlias(String clientUserAlias) {
		this.clientUserAlias = clientUserAlias;
	}
	/**
	 * @return the clientPassword
	 */
	public String getClientPassword() {
		return clientPassword;
	}
	/**
	 * @param clientPassword the clientPassword to set
	 */
	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}
	
	
}

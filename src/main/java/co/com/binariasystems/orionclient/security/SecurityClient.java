package co.com.binariasystems.orionclient.security;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import co.com.binariasystems.orion.model.dto.AccessTokenDTO;
import co.com.binariasystems.orion.model.dto.AuthenticationDTO;
import co.com.binariasystems.orion.model.dto.ResourceDTO;
import co.com.binariasystems.orion.model.dto.RoleDTO;
import co.com.binariasystems.orion.model.dto.UserCredentialsDTO;
import co.com.binariasystems.orion.model.dto.UserDTO;
import co.com.binariasystems.orion.model.enumerated.SN2Boolean;

public interface SecurityClient {
	@GET("security/userbyloginalias")
	public Call<UserDTO> findUserByLoginAlias(@Query("loginalias") String loginAlias);
	@GET("security/usercredentials")
	public Call<UserCredentialsDTO> findUserCredentials(@Query("loginalias") String loginAlias);
	@POST("security/saveauthentication")
	public Call<AccessTokenDTO>saveAuthentication(@Body AuthenticationDTO authentication);
	@POST("security/invalidatesession")
	public Call<Void> invalidateUserSession(@Body AuthenticationDTO authentication);
	@POST("security/accesstokenvalidity")
	public Call<SN2Boolean> validateAccessTokenValidity(@Body AccessTokenDTO accessTokenDTO);
	@POST("security/userroles")
	public Call<List<RoleDTO>> findUserRoles(@Body AuthenticationDTO authentication);
	@POST("security/roleresources")
	public Call<List<ResourceDTO>> findRoleResources(@Body RoleDTO role);
}

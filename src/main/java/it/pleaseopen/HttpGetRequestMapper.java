package it.pleaseopen;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpGetRequestMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper, TokenIntrospectionTokenMapper {

    public static final String PROVIDER_ID = "HTTP-GET-request-mapper";
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        ProviderConfigProperty propertyUrl = new ProviderConfigProperty();
        propertyUrl.setName("URL");
        propertyUrl.setRequired(true);
        propertyUrl.setType(ProviderConfigProperty.STRING_TYPE);
        propertyUrl.setDefaultValue("http://service-mock:5000/$userID");
        propertyUrl.setHelpText("Custom mapper URL, '$userID' tag will be replaced by the user id, '$userName' tag will be replaced by the username, '$userEmail' tag will be replaced by the user email,  ");
        propertyUrl.setLabel("URI to call via GET");
        configProperties.add(propertyUrl);
        ProviderConfigProperty propertyField = new ProviderConfigProperty();
        propertyField.setName("field");
        propertyField.setRequired(true);
        propertyField.setType(ProviderConfigProperty.STRING_TYPE);
        propertyField.setLabel("Field to map");
        propertyField.setHelpText("field to extract from the response");
        propertyField.setDefaultValue("id");
        configProperties.add(propertyField);
        ProviderConfigProperty propertyHeaders = new ProviderConfigProperty();
        propertyHeaders.setName("headers");
        propertyHeaders.setRequired(false);
        propertyHeaders.setType(ProviderConfigProperty.MAP_TYPE);
        propertyHeaders.setLabel("Headers");
        propertyHeaders.setHelpText("headers to send");
        configProperties.add(propertyHeaders);
        ProviderConfigProperty propertyTimeout = new ProviderConfigProperty();
        propertyTimeout.setName("timeout");
        propertyTimeout.setRequired(true);
        propertyTimeout.setType(ProviderConfigProperty.INTEGER_TYPE);
        propertyTimeout.setLabel("Timeout");
        propertyTimeout.setHelpText("HTTP timeout in ms");
        propertyTimeout.setDefaultValue(String.valueOf(5000));
        configProperties.add(propertyTimeout);
    }

    static {
        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        //OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        //OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, CustomMapper.class);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, FullNameMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return "Token mapper";
    }

    @Override
    public String getDisplayType() {
        return "HTTP GET request mapper";
    }

    @Override
    public String getHelpText() {
        return "A custom mapper that calls an external service";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext var5){
        Map<String, String> headers = new HashMap<>();
        JsonArray headersArray = JsonParser.parseString(mappingModel.getConfig().get("headers")).getAsJsonArray();
        headersArray.forEach(o -> {
            headers.put(o.getAsJsonObject().get("key").getAsString(), o.getAsJsonObject().get("value").getAsString());
        });
        String response = sendRequest(mappingModel.getConfig().get("URL"), mappingModel.getConfig().get("field"), headers, mappingModel.getConfig().get("timeout"),  userSession.getUser(), session);
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, response);
        return token;
    }

    @Override
    public AccessToken transformUserInfoToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext var5){
        Map<String, String> headers = new HashMap<>();
        com.google.gson.JsonArray headersArray = com.google.gson.JsonParser.parseString(mappingModel.getConfig().get("headers")).getAsJsonArray();
        headersArray.forEach(o -> {
            headers.put(o.getAsJsonObject().get("key").getAsString(), o.getAsJsonObject().get("value").getAsString());
        });
        String response = sendRequest(mappingModel.getConfig().get("URL"), mappingModel.getConfig().get("field"), headers, mappingModel.getConfig().get("timeout"), userSession.getUser(), session);
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, response);
        return token;
    }

    @Override
    public IDToken transformIDToken(IDToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext var5){
        Map<String, String> headers = new HashMap<>();
        com.google.gson.JsonArray headersArray = com.google.gson.JsonParser.parseString(mappingModel.getConfig().get("headers")).getAsJsonArray();
        headersArray.forEach(o -> {
            headers.put(o.getAsJsonObject().get("key").getAsString(), o.getAsJsonObject().get("value").getAsString());
        });
        String response = sendRequest(mappingModel.getConfig().get("URL"), mappingModel.getConfig().get("field"), headers, mappingModel.getConfig().get("timeout"), userSession.getUser(), session);
         OIDCAttributeMapperHelper.mapClaim(token, mappingModel, response);
        return token;
    }

    public AccessToken transformIntrospectionToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, ClientSessionContext var5){
        Map<String, String> headers = new HashMap<>();
        com.google.gson.JsonArray headersArray = com.google.gson.JsonParser.parseString(mappingModel.getConfig().get("headers")).getAsJsonArray();
        headersArray.forEach(o -> {
            headers.put(o.getAsJsonObject().get("key").getAsString(), o.getAsJsonObject().get("value").getAsString());
        });
        String response = sendRequest(mappingModel.getConfig().get("URL"), mappingModel.getConfig().get("field"), headers, mappingModel.getConfig().get("timeout"), userSession.getUser(), session);
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, response);
        return token;
    }

    private String sendRequest(String url, String field, Map<String, String> headers, String timeout, UserModel user, KeycloakSession session){
        String URLParsed = url.replaceAll("\\$userID", String.valueOf(user.getId())).replaceAll("\\$userName", String.valueOf(user.getUsername())).replaceAll("\\$userEmail", String.valueOf(user.getEmail()));
        try {
            SimpleHttp simpleHttp = SimpleHttp.doGet(URLParsed, session);
            for(Map.Entry<String, String> entry : headers.entrySet()){
                simpleHttp.header(entry.getKey(), entry.getValue());
            }
            simpleHttp.connectTimeoutMillis(Integer.parseInt(timeout));
            return simpleHttp.asJson().get(field).asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

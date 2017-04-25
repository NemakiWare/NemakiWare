package jp.aegif.nemaki.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import jp.aegif.nemaki.AppConfig;
import jp.aegif.nemaki.businesslogic.ContentService;
import jp.aegif.nemaki.cmis.aspect.CompileService;
import jp.aegif.nemaki.common.ErrorCode;
import jp.aegif.nemaki.model.Content;
import jp.aegif.nemaki.model.UserItem;
import jp.aegif.nemaki.plugin.action.ActionContext;
import jp.aegif.nemaki.plugin.action.JavaBackedAction;
import jp.aegif.nemaki.util.action.NemakiActionPlugin;

@Path("/repo/{repositoryId}/action/{actionId}")
public class ActionResource extends ResourceBase {
	private static final Log log = LogFactory.getLog(ActionResource.class);

	private ContentService contentService;

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	private CompileService compileService;

	public void setCompileService(CompileService compileService) {
		this.compileService = compileService;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/do/{objectId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String execute(String json, @PathParam("repositoryId") String repositoryId,
			@PathParam("actionId") String actionId, @PathParam("objectId") String objectId,
			@Context HttpServletRequest httpRequest) {
		boolean status = true;
		JSONObject result = new JSONObject();
		JSONArray list = new JSONArray();
		JSONArray errMsg = new JSONArray();

		String resultText = "";
		try (GenericApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

			NemakiActionPlugin acionPlugin = context.getBean(NemakiActionPlugin.class);
			JavaBackedAction plugin = acionPlugin.getPlugin(actionId);

			if (plugin != null) {
				CallContext callContext = (CallContext) httpRequest.getAttribute("CallContext");
				ObjectData object = compileService.compileObjectData(callContext, repositoryId,
						contentService.getContent(repositoryId, objectId), null, false, IncludeRelationships.NONE, null,
						false);
				UserItem userItem = contentService.getUserItemById(callContext.getRepositoryId(), callContext.getUsername());
				Properties props = compileService.compileProperties(callContext, callContext.getRepositoryId(), userItem);
				ActionContext actionContext = new ActionContext(callContext, props, object);
				resultText = plugin.executeAction(actionContext, json);
				result.put("action_res", resultText);
			}
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
			addErrMsg(errMsg, "" , ErrorCode.ERR_READ);
		}
		return result.toJSONString();
	}
}

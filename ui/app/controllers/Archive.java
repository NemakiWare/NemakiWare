package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.mvc.Controller;
import play.mvc.Result;
import util.ErrorMessage;
import util.Util;
import util.authentication.NemakiProfile;

import org.pac4j.play.java.Secure;
public class Archive extends Controller{

	private static String coreRestUri = Util.buildNemakiCoreUri() + "rest/";

	@Secure
	public Result index(String repositoryId, Integer page) {
		NemakiProfile profile = Util.getProfile(ctx());
		String endPoint = getEndpoint(repositoryId) + "index";

		int pageSize = Util.getNavigationPagingSize();
		endPoint += ("?limit=" + pageSize);

		Integer skip = 0;
		if(page >= 2){
			skip = (page - 1) * pageSize;
			endPoint += ("&skip=" + skip);
		}

		JsonNode json = Util.getJsonResponse(ctx(), endPoint);

		ArrayNode archives =  (ArrayNode) json.get("archives");
		Iterator<JsonNode> itr = archives.iterator();
		List<model.Archive> list = new ArrayList<model.Archive>();
		while(itr.hasNext()){
			ObjectNode archiveJson = (ObjectNode)(itr.next());
			model.Archive archive = new model.Archive(archiveJson);
			list.add(archive);
		}

		return ok(views.html.archive.index.render(repositoryId, list, page, profile));

	}

	@Secure
	public Result restore(String repositoryId, String archiveId){
		JsonNode json = Util.putJsonResponse(ctx(), getEndpoint(repositoryId) + "restore/" + archiveId, null);
		if(Util.isRestSuccess(json)){
			return ok();
		}else{
			String errorCode = json.get("error").get(0).get("archive").asText();
			return internalServerError(ErrorMessage.getMessage(errorCode));
		}
	}

	@Secure
	public Result destroy(String repositoryId, String archiveId){
		JsonNode json = Util.deleteJsonResponse(ctx(), getEndpoint(repositoryId) + "destroy/" + archiveId);
		if(Util.isRestSuccess(json)){
			return ok();
		}else{
			String errorCode = json.get("error").get(0).get("archive").asText();
			return internalServerError(ErrorMessage.getMessage(errorCode));
		}
	}

	private static String getEndpoint(String repositoryId){
		return coreRestUri + "repo/" + repositoryId + "/archive/";
	}
}
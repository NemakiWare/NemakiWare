package jp.aegif.nemaki.action.sample;

import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.aegif.nemaki.plugin.action.*;
import jp.aegif.nemaki.plugin.action.trigger.*;


public class SampleCmisObjectAction implements JavaBackedUIAction {
	private static final Logger log = LoggerFactory.getLogger(JavaBackedAction.class);

	private static UserButtonPerCmisObjcetActionTrigger _trigger;
	@Override
	public ActionTriggerBase getActionTrigger(UIActionContext context) {
		if (_trigger == null){
			_trigger = new UserButtonPerCmisObjcetActionTrigger("サンプルアクションの実行");
			_trigger.setFormHtml(""
				+ "<div>CMISオブジェクトサンプルのフォーム</div>\n"
				+ "<select class='dropdown' name='sampleFormData'>\n"
				+ "   <option value='1'>テスト1</option>\n"
				+ "   <option value='2'>テスト2</option>\n"
				+ "   <option value='3'>テスト3</option>\n"
				+ "</select><br />\n"
				+ "<input class='form-control' type='textbox' name='sampleTextboxData'></input>\n"
			);
		}
		return _trigger;
	}

	@Override
	public boolean canExecute(UIActionContext context) {
		 return ( BaseTypeId.CMIS_DOCUMENT ==  context.getCmisObject().getBaseTypeId());
	}

	@Override
	public String executeAction(UIActionContext context, String json) {
		CmisObject obj = context.getCmisObject();
		String name = (String) obj.getProperty("cmis:name").getFirstValue();

		return "{\"message\" : \"CMISオブジェクトアクションが実行されました ファイル名：" + name + "\"}";

	}

	@Override
	public boolean supports(String version) {
		return true;
	}

	@Override
	public String getActionTitle() {
		return "サンプルのアクション";
	}

	@Override
	public String getActionDiscription() {
		return "アクション機能のためのサンプルです";
	}

}

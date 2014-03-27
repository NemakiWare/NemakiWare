package jp.aegif.nemaki.service.node.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.aegif.nemaki.model.NemakiPropertyDefinition;
import jp.aegif.nemaki.model.NemakiPropertyDefinitionCore;
import jp.aegif.nemaki.model.NemakiPropertyDefinitionDetail;
import jp.aegif.nemaki.model.NemakiTypeDefinition;
import jp.aegif.nemaki.service.dao.ContentDaoService;
import jp.aegif.nemaki.service.node.TypeService;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.collections.CollectionUtils;

public class TypeServiceImpl implements TypeService{

	private ContentDaoService contentDaoService;

	public TypeServiceImpl() {


	}

	public TypeServiceImpl(ContentDaoService contentDaoService) {
		setContentDaoService(contentDaoService);
	}

	@Override
	public NemakiTypeDefinition getTypeDefinition(String typeId) {
		return contentDaoService.getTypeDefinition(typeId);
	}

	@Override
	public List<NemakiTypeDefinition> getTypeDefinitions() {
		return contentDaoService.getTypeDefinitions();
	}

	@Override
	public NemakiPropertyDefinition getPropertyDefinition(String detailNodeId) {
		NemakiPropertyDefinitionDetail detail = getPropertyDefinitionDetail(detailNodeId);
		NemakiPropertyDefinitionCore core = getPropertyDefinitionCore(detail
				.getCoreNodeId());

		NemakiPropertyDefinition npd = new NemakiPropertyDefinition(core,
				detail);
		return npd;
	}

	@Override
	public NemakiPropertyDefinitionCore getPropertyDefinitionCore(String coreId) {
		return contentDaoService.getPropertyDefinitionCore(coreId);
	}

	@Override
	public NemakiPropertyDefinitionCore getPropertyDefinitionCoreByPropertyId(
			String propertyId) {
		return contentDaoService.getPropertyDefinitionCoreByPropertyId(propertyId);
	}

	@Override
	public List<NemakiPropertyDefinitionCore> getPropertyDefinitionCores() {
		return contentDaoService.getPropertyDefinitionCores();
	}

	@Override
	public NemakiPropertyDefinitionDetail getPropertyDefinitionDetail(
			String detailId) {
		return contentDaoService.getPropertyDefinitionDetail(detailId);
	}

	@Override
	public List<NemakiPropertyDefinitionDetail> getPropertyDefinitionDetailByCoreNodeId(
			String coreNodeId){
		return contentDaoService.getPropertyDefinitionDetailByCoreNodeId(coreNodeId);
	}

	@Override
	public NemakiTypeDefinition createTypeDefinition(
			NemakiTypeDefinition typeDefinition) {
		return contentDaoService.createTypeDefinition(typeDefinition);
	}

	@Override
	public NemakiTypeDefinition updateTypeDefinition(
			NemakiTypeDefinition typeDefinition) {
		return contentDaoService.updateTypeDefinition(typeDefinition);
	}

	@Override
	public void deleteTypeDefinition(String typeId) {
		NemakiTypeDefinition ntd = getTypeDefinition(typeId);

		//Delete unnecessary property definitions
		List<String> detailIds = ntd.getProperties();
		for(String detailId : detailIds){
			NemakiPropertyDefinitionDetail detail = getPropertyDefinitionDetail(detailId);
			NemakiPropertyDefinitionCore core = getPropertyDefinitionCore(detail.getCoreNodeId());
			//Delete a detail
			contentDaoService.delete(detail.getId());

			//Delete a core only if no details exist
			List<NemakiPropertyDefinitionDetail> l =
					contentDaoService.getPropertyDefinitionDetailByCoreNodeId(core.getId());
			if(CollectionUtils.isEmpty(l)){
				contentDaoService.delete(core.getId());
			}
		}

		//Delete the type definition
		contentDaoService.deleteTypeDefinition(ntd.getId());

	}

	@Override
	public NemakiPropertyDefinitionDetail createPropertyDefinition(
			NemakiPropertyDefinition propertyDefinition) {
		NemakiPropertyDefinitionCore _core = new NemakiPropertyDefinitionCore(
				propertyDefinition);

		// Skip creating a core when it exists
		List<NemakiPropertyDefinitionCore> cores = getPropertyDefinitionCores();
		Map<String, NemakiPropertyDefinitionCore> corePropertyIds = new HashMap<String, NemakiPropertyDefinitionCore>();
		for (NemakiPropertyDefinitionCore npdc : cores) {
			corePropertyIds.put(npdc.getPropertyId(), npdc);
		}
		String coreNodeId = "";
		if (!corePropertyIds.containsKey(_core.getPropertyId())) {
			//propertyId uniqueness
			_core.setPropertyId(buildUniquePropertyId(_core.getPropertyId()));
			// Create a property core
			NemakiPropertyDefinitionCore core = contentDaoService
					.createPropertyDefinitionCore(_core);
			coreNodeId = core.getId();
		} else {
			NemakiPropertyDefinitionCore existing = corePropertyIds.get(_core
					.getPropertyId());
			coreNodeId = existing.getId();
		}

		// Create a detail
		NemakiPropertyDefinitionDetail _detail = new NemakiPropertyDefinitionDetail(
				propertyDefinition, coreNodeId);
		NemakiPropertyDefinitionDetail detail = contentDaoService
				.createPropertyDefinitionDetail(_detail);

		return detail;
	}

	@Override
	public NemakiPropertyDefinitionDetail updatePropertyDefinitionDetail(
			NemakiPropertyDefinitionDetail propertyDefinitionDetail) {
		return contentDaoService.updatePropertyDefinitionDetail(propertyDefinitionDetail);
	}





	private String buildUniquePropertyId(String propertyId){
		if(isUniquePropertyIdInRepository(propertyId)){
			return propertyId;
		}else{
			return propertyId + "_" + String.valueOf(System.currentTimeMillis());
		}
	}

	private boolean isUniquePropertyIdInRepository(String propertyId){
		//propertyId uniqueness
		List<String> list = getSystemPropertyIds();
		List<NemakiPropertyDefinitionCore>cores = getPropertyDefinitionCores();
		if(CollectionUtils.isNotEmpty(cores)){
			for(NemakiPropertyDefinitionCore core: cores){
				list.add(core.getPropertyId());
			}
		}

		return !list.contains(propertyId);
	}

	/**
	 * List up specification-default property ids
	 *
	 * @return
	 */
	private List<String> getSystemPropertyIds() {
		List<String> ids = new ArrayList<String>();

		Field[] fields = PropertyIds.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				String cmisId = (String) (field.get(null));
				ids.add(cmisId);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return ids;
	}

	public void setContentDaoService(ContentDaoService contentDaoService) {
		this.contentDaoService = contentDaoService;
	}


}

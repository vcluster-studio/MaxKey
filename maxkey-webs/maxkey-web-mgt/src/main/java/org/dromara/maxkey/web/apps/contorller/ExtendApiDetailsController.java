/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.dromara.maxkey.web.apps.contorller;

import java.util.List;

import org.dromara.maxkey.authn.annotation.CurrentUser;
import org.dromara.maxkey.constants.ConstsProtocols;
import org.dromara.maxkey.crypto.ReciprocalUtils;
import org.dromara.maxkey.entity.Message;
import org.dromara.maxkey.entity.apps.Apps;
import org.dromara.maxkey.entity.apps.AppsExtendApiDetails;
import org.dromara.maxkey.entity.idm.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value={"/apps/extendapi"})
public class ExtendApiDetailsController  extends BaseAppContorller {
	static final Logger logger = LoggerFactory.getLogger(ExtendApiDetailsController.class);

	@RequestMapping(value = { "/init" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<?> init() {
		AppsExtendApiDetails extendApiDetails=new AppsExtendApiDetails();
		extendApiDetails.setId(extendApiDetails.generateId());
		extendApiDetails.setProtocol(ConstsProtocols.EXTEND_API);
		extendApiDetails.setSecret(ReciprocalUtils.generateKey(""));
		return new Message<AppsExtendApiDetails>(extendApiDetails);
	}
	
	@RequestMapping(value = { "/get/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<?> get(@PathVariable("id") String id) {
		Apps application= appsService.get(id);
		super.decoderSecret(application);
		AppsExtendApiDetails extendApiDetails=new AppsExtendApiDetails();
		BeanUtils.copyProperties(application, extendApiDetails);
		extendApiDetails.transIconBase64();
		return new Message<AppsExtendApiDetails>(extendApiDetails);
	}
	
	@ResponseBody
	@RequestMapping(value={"/add"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<?> add(
			@RequestBody AppsExtendApiDetails extendApiDetails,
			@CurrentUser UserInfo currentUser) {
		logger.debug("-Add  :" + extendApiDetails);
		
		transform(extendApiDetails);
		extendApiDetails.setInstId(currentUser.getInstId());
		if (appsService.insertApp(extendApiDetails)) {
			return new Message<AppsExtendApiDetails>(Message.SUCCESS);
		} else {
			return new Message<AppsExtendApiDetails>(Message.FAIL);
		}
	}
	
	@ResponseBody
	@RequestMapping(value={"/update"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<?> update(
			@RequestBody AppsExtendApiDetails extendApiDetails,
			@CurrentUser UserInfo currentUser) {
		logger.debug("-update  :" + extendApiDetails);
		transform(extendApiDetails);
		extendApiDetails.setInstId(currentUser.getInstId());
		if (appsService.updateApp(extendApiDetails)) {
		    return new Message<AppsExtendApiDetails>(Message.SUCCESS);
		} else {
			return new Message<AppsExtendApiDetails>(Message.FAIL);
		}
	}
	
	@ResponseBody
	@RequestMapping(value={"/delete"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Message<?> delete(
			@RequestParam("ids") List<String> ids,
			@CurrentUser UserInfo currentUser) {
		logger.debug("-delete  ids : {} " , ids);
		if (appsService.deleteBatch(ids)) {
			 return new Message<AppsExtendApiDetails>(Message.SUCCESS);
		} else {
			return new Message<AppsExtendApiDetails>(Message.FAIL);
		}
	}

}

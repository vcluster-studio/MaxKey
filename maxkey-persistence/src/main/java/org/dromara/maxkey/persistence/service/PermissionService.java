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
 

package org.dromara.maxkey.persistence.service;

import java.util.List;

import org.dromara.maxkey.entity.permissions.Permission;
import org.dromara.maxkey.persistence.mapper.PermissionMapper;
import org.dromara.mybatis.jpa.JpaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionService  extends JpaService<Permission>{
	static final  Logger _logger = LoggerFactory.getLogger(PermissionService.class);
   
    
	public PermissionService() {
		super(PermissionMapper.class);
	}

	/* (non-Javadoc)
	 * @see com.connsec.db.service.BaseService#getMapper()
	 */
	@Override
	public PermissionMapper getMapper() {
		return (PermissionMapper)super.getMapper();
	}
	
	public boolean insertGroupPrivileges(List<Permission> rolePermissionsList) {
	    return getMapper().insertGroupPrivileges(rolePermissionsList)>0;
	};
    
	public boolean deleteGroupPrivileges(List<Permission> rolePermissionsList) {
	     return getMapper().deleteGroupPrivileges(rolePermissionsList)>=0;
	 }
	
    public List<Permission> queryGroupPrivileges(Permission rolePermissions){
        return getMapper().queryGroupPrivileges(rolePermissions);
    }    

}

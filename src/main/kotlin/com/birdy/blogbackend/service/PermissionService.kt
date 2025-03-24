package com.birdy.blogbackend.service

import com.birdy.blogbackend.domain.entity.Permission
import com.birdy.blogbackend.domain.enums.Group
import com.birdy.blogbackend.domain.vo.request.permission.PermissionAddRequest
import com.birdy.blogbackend.domain.vo.request.permission.PermissionRemoveRequest
import com.birdy.blogbackend.domain.vo.request.permission.PermissionUpdateRequest
import com.mybatisflex.core.service.IService
import jakarta.servlet.http.HttpServletRequest

/**
 * @author birdy
 */
interface PermissionService : IService<Permission?> {
  fun getUserPermissions(uid: Long): Set<Permission?>?

  fun checkPermission(uid: Long, permission: String?): Boolean

  fun checkPermission(permission: Permission?): Boolean

  fun getPermission(uid: Long, permission: String?): Permission?

  fun updatePermission(
    permissionUpdateRequest: PermissionUpdateRequest?,
    admin: Boolean,
    request: HttpServletRequest?
  )

  fun addPermission(uid: Long, permission: String?, expiry: Long, admin: Boolean)

  fun addPermission(permissionAddRequest: PermissionAddRequest?, admin: Boolean)

  fun removePermission(uid: Long, permission: String?, admin: Boolean)

  fun removePermission(id: Long, admin: Boolean)

  fun removePermission(permissionRemoveRequest: PermissionRemoveRequest?, admin: Boolean)

  fun getGroups(uid: Long): List<Permission?>?

  fun getMaxPriorityGroup(uid: Long): Group?

  fun getMaxPriorityGroupP(uid: Long): Permission?
}

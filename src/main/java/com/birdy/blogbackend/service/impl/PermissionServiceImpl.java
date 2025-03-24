package com.birdy.blogbackend.service.impl;

import com.birdy.blogbackend.domain.entity.Permission;
import com.birdy.blogbackend.domain.enums.Group;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.request.permission.PermissionAddRequest;
import com.birdy.blogbackend.domain.vo.request.permission.PermissionRemoveRequest;
import com.birdy.blogbackend.domain.vo.request.permission.PermissionUpdateRequest;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.mapper.PermissionMapper;
import com.birdy.blogbackend.service.PermissionService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author birdy
 */
@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
  @Override
  public Set<Permission> getUserPermissions(long uid) {
    Set<Permission> permissions;
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("uid", uid);
    List<Permission> lps = this.list(queryWrapper);
    permissions = Set.copyOf(lps);
    return permissions;
  }


  @Override
  public boolean checkPermission(long uid, String permission) {
    Set<Permission> permissions = getUserPermissions(uid);
    for (Permission p : permissions) {
      if (p.getExpiry() == 0 || p.getExpiry() > System.currentTimeMillis()) {
        if (p.getPermission().isBlank()) {
          return true;
        } else if (p.getPermission().equals("*")) {
          return true;
        } else if (p.getPermission().equals("group." + Group.ADMIN.name())) {
          return true;
        } else if (p.getPermission().equalsIgnoreCase(permission)) {
          return true;
        }
      } else {
        this.removeById(p.getId());
      }
    }
    return false;
  }

  @Override
  public boolean checkPermission(Permission permission) {
    return checkPermission(permission.getUid(), permission.getPermission());
  }

  @Override
  public Permission getPermission(long uid, String permission) {
    Set<Permission> permissions = getUserPermissions(uid);
    for (Permission p : permissions) {
      if (p.getPermission().equalsIgnoreCase(permission)) {
        return p;
      }
    }
    return null;

  }

  @Override
  public void updatePermission(PermissionUpdateRequest permissionUpdateRequest, boolean admin, HttpServletRequest request) {
    // id 和 uid 不能修改
    long id = permissionUpdateRequest.getId();
    String newPermission = permissionUpdateRequest.getPermission();
    long newExpiry = permissionUpdateRequest.getExpiry();
    Permission permissionQuery = this.getById(id);
    if (permissionQuery != null) {
      long uid = permissionQuery.getUid();
      String oldPermission = permissionQuery.getPermission();
      long oldExpiry = permissionQuery.getExpiry();
      // 如果newPermission不为空且不等于oldPermission，则修改
      if (!StringUtils.isAnyBlank(newPermission) && !oldPermission.equalsIgnoreCase(newPermission)) {
        permissionQuery.setPermission(newPermission);
      }
      if (oldExpiry != newExpiry) {
        permissionQuery.setExpiry(newExpiry);
      }
      this.updateById(permissionQuery);
    } else {
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "权限不存在", request);
    }
  }

  @Override
  public void addPermission(long uid, String permission, long expiry, boolean admin) {
    Permission permissionQuery = getPermission(uid, permission);
    if (permissionQuery == null) {
      permissionQuery = new Permission();
      permissionQuery.setUid(uid);
      permissionQuery.setPermission(permission);
      permissionQuery.setExpiry(expiry);
      boolean saveResult = this.save(permissionQuery);
      if (!saveResult) {
        throw new BusinessException(ReturnCode.SYSTEM_ERROR, "添加失败，数据库错误", null);
      }
    } else {
      throw new BusinessException(ReturnCode.DATA_EXISTED, "权限已存在(" + permissionQuery.getId() + ")", null);
    }
  }

  @Override
  public void addPermission(PermissionAddRequest permissionAddRequest, boolean admin) {
    addPermission(permissionAddRequest.getUid(), permissionAddRequest.getPermission(), permissionAddRequest.getExpiry(), admin);
  }

  @Override
  public void removePermission(long uid, String permission, boolean admin) {
    Permission permissionQuery = getPermission(uid, permission);
    if (permissionQuery != null) {
      this.removeById(permissionQuery.getId());
    }
  }

  @Override
  public void removePermission(long id, boolean admin) {
    Permission permissionQuery = this.getById(id);
    if (permissionQuery != null) {
      this.removeById(permissionQuery.getId());
    }
  }

  @Override
  public void removePermission(PermissionRemoveRequest permissionRemoveRequest, boolean admin) {
    removePermission(permissionRemoveRequest.getUid(), permissionRemoveRequest.getPermission(), admin);
  }

  @Override
  public List<Permission> getGroups(long uid) {
    Set<Permission> permissions = getUserPermissions(uid);
    List<Permission> groups = new ArrayList<>();
    for (Permission permission : permissions) {
      if (permission.getExpiry() == 0 || permission.getExpiry() > System.currentTimeMillis()) {
        if (permission.getPermission().startsWith("group.")) {
          groups.add(permission);
        }
        if (permission.getPermission().equals("*")) {
          groups.add(permission);
        }
      } else {
        this.removeById(permission.getId());
      }
    }
    return groups;
  }

  @Override
  public Group getMaxPriorityGroup(long uid) {
    List<Permission> groups = getGroups(uid);
    Group maxPriorityGroup = Group.DEFAULT;
    for (Permission group : groups) {
      try {
        if (group.getPermission().equals("*")) {
          return Group.ADMIN;
        }
        Group g = Group.valueOf(group.getPermission().substring(6).toUpperCase());
        if (g.getPriority() > maxPriorityGroup.getPriority()) {
          maxPriorityGroup = g;
        }
      } catch (IllegalArgumentException ignored) {
      }
    }
    return maxPriorityGroup;
  }

  @Override
  public Permission getMaxPriorityGroupP(long uid) {
    List<Permission> groups = getGroups(uid);
    Group maxPriorityGroup = Group.DEFAULT;
    Permission i = null;
    for (Permission group : groups) {
      try {
        if (group.getPermission().equals("*")) {
          return group;
        }
        Group g = Group.valueOf(group.getPermission().substring(6).toUpperCase());
        if (g.getPriority() > maxPriorityGroup.getPriority()) {
          maxPriorityGroup = g;
          i = group;
        } else if (g == Group.DEFAULT) {
          i = group;
        }
      } catch (IllegalArgumentException ignored) {
      }
    }
    return i;
  }
}

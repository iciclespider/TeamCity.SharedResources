/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.sharedResources.pages;

import jetbrains.buildServer.controllers.admin.projects.EditProjectTab;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.auth.SecurityContext;
import jetbrains.buildServer.sharedResources.SharedResourcesPluginConstants;
import jetbrains.buildServer.sharedResources.model.Lock;
import jetbrains.buildServer.sharedResources.model.LockType;
import jetbrains.buildServer.sharedResources.server.ConfigurationInspector;
import jetbrains.buildServer.sharedResources.server.feature.Resources;
import jetbrains.buildServer.sharedResources.server.feature.SharedResourcesFeature;
import jetbrains.buildServer.sharedResources.server.feature.SharedResourcesFeatures;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public class SharedResourcesPage extends EditProjectTab {

  @NotNull
  private static final String TITLE_PREFIX = "Shared Resources";

  @NotNull
  private final Resources myResources;

  @NotNull
  private final SharedResourcesFeatures myFeatures;

  @NotNull
  private final SecurityContext mySecurityContext;

  @NotNull
  private final ConfigurationInspector myInspector;

  public SharedResourcesPage(@NotNull final PagePlaces pagePlaces,
                             @NotNull final PluginDescriptor descriptor,
                             @NotNull final Resources resources,
                             @NotNull final SharedResourcesFeatures features,
                             @NotNull final SecurityContext securityContext,
                             @NotNull final ConfigurationInspector inspector) {
    super(pagePlaces, SharedResourcesPluginConstants.PLUGIN_NAME, descriptor.getPluginResourcesPath("projectPage.jsp"), TITLE_PREFIX);
    myResources = resources;
    myFeatures = features;
    mySecurityContext = securityContext;
    myInspector = inspector;
    addCssFile("/css/admin/buildTypeForm.css");
    addJsFile(descriptor.getPluginResourcesPath("js/ResourceDialog.js"));
  }

  @Override
  public void fillModel(@NotNull final Map<String, Object> model, @NotNull final HttpServletRequest request) {
    super.fillModel(model, request);
    SharedResourcesBean bean;
    final SProject project = getProject(request);
    if (project != null) {
      final List<SProject> meAndSubtree = project.getProjects();
      meAndSubtree.add(project);
      final Map<String, Map<SBuildType, LockType>> usageMap = new HashMap<String, Map<SBuildType, LockType>>();
      final Map<SBuildType, Map<Lock, String>> configurationErrors = new HashMap<SBuildType, Map<Lock, String>>();
      for (SProject p : meAndSubtree) {
        final List<SBuildType> buildTypes = p.getBuildTypes();
        for (SBuildType type : buildTypes) {
          // todo: investigate here, what happens if we use resolved settings on lock name with %%. Does it change to the value of the parameter?
          final Collection<SharedResourcesFeature> features = myFeatures.searchForFeatures(type);
          for (SharedResourcesFeature feature : features) {
            final Map<String, Lock> locksMap = feature.getLockedResources();
            for (String str : locksMap.keySet()) {
              if (usageMap.get(str) == null) {
                usageMap.put(str, new HashMap<SBuildType, LockType>());
              }
              usageMap.get(str).put(type, locksMap.get(str).getType());
            }
          }
          Map<Lock, String> inspection = myInspector.inspect(type);
          if (!inspection.isEmpty()) {
            configurationErrors.put(type, inspection);
          }
        }
      }
      final String projectId = project.getProjectId();
      bean = new SharedResourcesBean(project, myResources.asProjectResourceMap(projectId), usageMap, configurationErrors);
      model.put("bean", bean);
    }
  }

  @NotNull
  @Override
  public String getTabTitle(@NotNull final HttpServletRequest request) {
    final SProject project = getProject(request);
    String result;
    if (project != null) {
      int n = myResources.getCount(project.getProjectId());
      if (n > 0) {
        result = TITLE_PREFIX + " (" + n + ")";
      } else {
        result = TITLE_PREFIX;
      }
    } else {
      result = TITLE_PREFIX;
    }
    return result;
  }

  @Override
  public boolean isAvailable(@NotNull final HttpServletRequest request) {
    final SProject project = getProject(request);
    final SUser user = (SUser) mySecurityContext.getAuthorityHolder().getAssociatedUser();
    return  user != null && project != null &&
            user.isPermissionGrantedForProject(project.getProjectId(), Permission.EDIT_PROJECT);
  }
}

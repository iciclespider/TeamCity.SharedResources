<%--
  ~ Copyright 2000-2013 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include-internal.jsp" %>
<%@ page import="com.intellij.openapi.util.text.StringUtil" %>
<%@ page import="com.intellij.util.Function" %>
<%@ page import="jetbrains.buildServer.sharedResources.model.Lock" %>
<%@ page import="jetbrains.buildServer.web.openapi.healthStatus.HealthStatusItemDisplayMode" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<jsp:useBean id="healthStatusItem" type="jetbrains.buildServer.serverSide.healthStatus.HealthStatusItem" scope="request"/>
<jsp:useBean id="showMode" type="jetbrains.buildServer.web.openapi.healthStatus.HealthStatusItemDisplayMode" scope="request"/>

<c:set var="invalidLocks" value="${healthStatusItem.additionalData['invalid_locks']}"/>
<c:set var="buildType" value="${healthStatusItem.additionalData['build_type']}"/>
<c:set var="inplaceMode" value="<%=HealthStatusItemDisplayMode.IN_PLACE%>"/>

<c:if test="${not empty invalidLocks}">
  <c:choose>
    <c:when test="${showMode == inplaceMode}">
      <div>
        <bs:out value="${buildType.name}"/> contains invalid lock<bs:s val="${fn:length(invalidLocks)}"/>:
        <c:url var="resourcesUrl" value="/admin/editProject.html?projectId=${project.externalId}&tab=JetBrains.SharedResources"/>
        <ul>
          <c:forEach items="${invalidLocks}" var="item">
            <li><a href="${resourcesUrl}">${item.key.name}</a> &mdash; ${item.value}</li>
          </c:forEach>
        </ul>
      </div>
    </c:when>
    <c:otherwise>
      <div>
        <bs:buildTypeLink buildType="${buildType}"/>&nbsp;contains invalid lock<bs:s val="${fn:length(invalidLocks)}"/>:
        <%
          @SuppressWarnings("unchecked")
          final Set<Lock> locks = ((Map<Lock, String>)healthStatusItem.getAdditionalData().get("invalid_locks")).keySet();
        %>
        <%=StringUtil.join(locks, new Function<Lock, String>() {
          @Override
          public String fun(Lock lock) {
            return lock.getName();
          }
        }, ", ")
        %>
      </div>
    </c:otherwise>
  </c:choose>
</c:if>
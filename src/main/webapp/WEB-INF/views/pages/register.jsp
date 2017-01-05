<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div id="mainWrapper">
	<div class="login-container">
		<div class="login-card">
			<div class="login-form">
				<c:url var="loginUrl" value="/login" />
				<form:form method="POST" modelAttribute="user"
					class="form-horizontal">
					<form:input type="hidden" path="id" id="id" />

					<div class="row">
						<div class="form-group col-md-12">
							<label class="col-md-3 control-lable" for="username">Username</label>
							<div class="col-md-7">
								<c:choose>
									<c:when test="${edit}">
										<form:input type="text" path="username" id="username"
											class="form-control input-sm" disabled="true" />
									</c:when>
									<c:otherwise>
										<form:input type="text" path="username" id="username"
											class="form-control input-sm" />
										<div class="has-error">
											<form:errors path="username" class="help-inline" />
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="form-group col-md-12">
							<label class="col-md-3 control-lable" for="password">Password</label>
							<div class="col-md-7">
								<form:input type="password" path="password" id="password"
									class="form-control input-sm" />
								<div class="has-error">
									<form:errors path="password" class="help-inline" />
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="form-group col-md-12">
							<label class="col-md-3 control-lable" for="email">Email</label>
							<div class="col-md-7">
								<form:input type="text" path="email" id="email"
									class="form-control input-sm" />
								<div class="has-error">
									<form:errors path="email" class="help-inline" />
								</div>
							</div>
						</div>
					</div>
					<sec:authorize access="hasRole('ADMIN')">
						<div class="row">
							<div class="form-group col-md-12">
								<label class="col-md-3 control-lable" for="roles">Roles</label>
								<div class="col-md-7">
									<form:select path="roles" items="${roles}"
										multiple="true" itemValue="id" itemLabel="type"
										class="form-control input-sm" />
									<div class="has-error">
										<form:errors path="roles" class="help-inline" />
									</div>
								</div>
							</div>
						</div>
					</sec:authorize>
					<div class="row">
						<div class="form-actions floatRight">
							<c:choose>
								<c:when test="${edit}">
									<input type="submit" value="Update"
										class="btn btn-primary btn-sm" /> or <a
										href="<c:url value='/admin/list' />">Cancel</a>
								</c:when>
								<c:otherwise>
									<input type="submit" value="Register"
										class="btn btn-primary btn-sm" /> or <a
										href="<c:url value='/user/reviews/list' />">Cancel</a>
								</c:otherwise>
							</c:choose>
						</div>
					</div>


					<div class="alert alert-warning" style="margin: 5px">
						<p>Don't forget, that without having a League of Legends account with the same summoner name as your username here our system won't be able to register you.</p>
						<p>Also, please rename one of your runepages to "ryp.gg" (without quotes).</p>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>

<script>
$('a.regionType').click(function() {
	$("#region").val($(this).text());
});
</script>
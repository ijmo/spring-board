package ijmo.demo.springboard.user;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

// This is an alternative of org.springframework.security.core.annotation.AuthenticationPrincipal
public class CurrentUserResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        Class clazz = principal.getClass();
        if (String.class.isAssignableFrom(clazz)) {
            return null;
        }

        if (!UserPrincipal.class.isAssignableFrom(clazz)) {
            throw new ClassCastException(principal + "is not assignable to UserPrincipal");
        }

        UserPrincipal userPrincipal = (UserPrincipal) principal;
        return userPrincipal.getUser();
    }
}
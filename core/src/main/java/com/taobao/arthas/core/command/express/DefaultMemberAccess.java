package com.taobao.arthas.core.command.express;

import ognl.MemberAccess;
import ognl.OgnlContext;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author jiaming.lf
 * @date 2023/8/28 10:02
 */
public class DefaultMemberAccess implements MemberAccess {
    public boolean allowPrivateAccess;
    public boolean allowProtectedAccess;
    public boolean allowPackageProtectedAccess;

    public DefaultMemberAccess(boolean allowAllAccess) {
        this(allowAllAccess, allowAllAccess, allowAllAccess);
    }

    public DefaultMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess, boolean allowPackageProtectedAccess) {
        this.allowPrivateAccess = false;
        this.allowProtectedAccess = false;
        this.allowPackageProtectedAccess = false;
        this.allowPrivateAccess = allowPrivateAccess;
        this.allowProtectedAccess = allowProtectedAccess;
        this.allowPackageProtectedAccess = allowPackageProtectedAccess;
    }

    public boolean getAllowPrivateAccess() {
        return this.allowPrivateAccess;
    }

    public void setAllowPrivateAccess(boolean value) {
        this.allowPrivateAccess = value;
    }

    public boolean getAllowProtectedAccess() {
        return this.allowProtectedAccess;
    }

    public void setAllowProtectedAccess(boolean value) {
        this.allowProtectedAccess = value;
    }

    public boolean getAllowPackageProtectedAccess() {
        return this.allowPackageProtectedAccess;
    }

    public void setAllowPackageProtectedAccess(boolean value) {
        this.allowPackageProtectedAccess = value;
    }
    @Override
    public Object setup(OgnlContext context, Object target, Member member, String propertyName) {
        Object result = null;
        if (this.isAccessible(context, target, member, propertyName)) {
            AccessibleObject accessible = (AccessibleObject) member;
            if (!accessible.isAccessible()) {
                result = Boolean.FALSE;
                accessible.setAccessible(true);
            }
        }

        return result;
    }

    @Override
    public void restore(OgnlContext context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            ((AccessibleObject) member).setAccessible((Boolean) state);
        }
    }

    @Override
    public boolean isAccessible(OgnlContext context, Object target, Member member, String propertyName) {
        int modifiers = member.getModifiers();
        boolean result = Modifier.isPublic(modifiers);
        if (!result) {
            if (Modifier.isPrivate(modifiers)) {
                result = this.getAllowPrivateAccess();
            } else if (Modifier.isProtected(modifiers)) {
                result = this.getAllowProtectedAccess();
            } else {
                result = this.getAllowPackageProtectedAccess();
            }
        }

        return result;
    }
}

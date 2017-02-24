package com.orca.kam.rxpermission.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2017-02-21 - 오후 1:20
 */
public class PermissionUtil {

    /**
     * @param o need to check object is empty
     * @return is object Empty
     * Check the Permissions is Empty
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else {
            if ((o instanceof String) && (((String) o).trim().length() == 0)) {
                return true;
            }
            if (o instanceof Map) {
                return ((Map<?, ?>) o).isEmpty();
            }
            if (o instanceof List) {
                return ((List<?>) o).isEmpty();
            }
            if (o instanceof Set) {
                return ((Set<?>) o).isEmpty();
            }
        }
        return o instanceof Object[] && (((Object[]) o).length == 0);
    }
}
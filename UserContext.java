package cn.edu.sdua.sushe.common;

/**
 * 当前登录用户上下文（基于 ThreadLocal）。
 */
public class UserContext {
    private static final ThreadLocal<UserHolder> HOLDER = new ThreadLocal<>();

    public static void set(UserHolder h) {
        HOLDER.set(h);
    }

    public static UserHolder get() {
        return HOLDER.get();
    }

    public static Long userId() {
        UserHolder h = HOLDER.get();
        return h == null ? null : h.id;
    }

    public static String role() {
        UserHolder h = HOLDER.get();
        return h == null ? null : h.role;
    }

    public static String name() {
        UserHolder h = HOLDER.get();
        return h == null ? null : h.realName;
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static class UserHolder {
        public Long id;
        public String role;
        public String realName;
        public String username;

        public UserHolder(Long id, String role, String realName, String username) {
            this.id = id;
            this.role = role;
            this.realName = realName;
            this.username = username;
        }
    }
}

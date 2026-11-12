package cn.edu.sdua.sushe.service;

import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.dto.RegisterRequest;
import cn.edu.sdua.sushe.entity.User;
import cn.edu.sdua.sushe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务：登录 / 注册 / token 维护。
 * 采用内存 token 存储的轻量级方案，避免引入完整 Spring Security。
 */
@Service
public class AuthService {

    private final Map<String, UserContext.UserHolder> tokens = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> login(String username, String password) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User u = opt.get();
        if (!u.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        if (u.getStatus() != null && u.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        UserContext.UserHolder holder =
                new UserContext.UserHolder(u.getId(), u.getRole(), u.getRealName(), u.getUsername());
        tokens.put(token, holder);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", safeCopy(u));
        return result;
    }

    public User register(RegisterRequest req) {
        if (!"WORKER".equals(req.getRole()) && !"STUDENT".equals(req.getRole())) {
            throw new RuntimeException("仅支持注册维修工或学生");
        }
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(req.getPassword());
        u.setRealName(req.getRealName());
        u.setRole(req.getRole());
        u.setPhone(req.getPhone());
        u.setEmail(req.getEmail());
        u.setGender(req.getGender());
        u.setSkills(req.getSkills());
        u.setCollege(req.getCollege());
        u.setStatus(1);
        return userRepository.save(u);
    }

    public void logout(String token) {
        if (token != null) tokens.remove(token);
    }

    public UserContext.UserHolder resolve(String token) {
        if (token == null) return null;
        return tokens.get(token);
    }

    /** 去除密码后返回给前端。 */
    private User safeCopy(User u) {
        User c = new User();
        c.setId(u.getId());
        c.setUsername(u.getUsername());
        c.setRealName(u.getRealName());
        c.setRole(u.getRole());
        c.setPhone(u.getPhone());
        c.setEmail(u.getEmail());
        c.setGender(u.getGender());
        c.setSkills(u.getSkills());
        c.setCollege(u.getCollege());
        c.setAvgRating(u.getAvgRating());
        c.setFinishedCount(u.getFinishedCount());
        c.setTotalWorkHours(u.getTotalWorkHours());
        c.setStatus(u.getStatus());
        c.setCreateTime(u.getCreateTime());
        return c;
    }
}

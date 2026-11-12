package cn.edu.sdua.sushe.controller;

import cn.edu.sdua.sushe.common.R;
import cn.edu.sdua.sushe.common.UserContext;
import cn.edu.sdua.sushe.dto.LoginRequest;
import cn.edu.sdua.sushe.dto.RegisterRequest;
import cn.edu.sdua.sushe.entity.User;
import cn.edu.sdua.sushe.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest req) {
        return R.ok(authService.login(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/register")
    public R<User> register(@RequestBody RegisterRequest req) {
        return R.ok("注册成功，请登录", authService.register(req));
    }

    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader("X-Token"));
        return R.ok();
    }

    @GetMapping("/me")
    public R<Object> me() {
        UserContext.UserHolder h = UserContext.get();
        return R.ok(h);
    }
}

package cn.edu.sdua.sushe.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    /** WORKER / STUDENT （管理员不允许自助注册） */
    private String role;
    private String phone;
    private String email;
    private String gender;
    /** 维修工技能 */
    private String skills;
    /** 学生学院/班级 */
    private String college;
}

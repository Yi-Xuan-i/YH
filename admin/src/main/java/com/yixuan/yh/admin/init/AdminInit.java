package com.yixuan.yh.admin.init;

import com.yixuan.yh.admin.entity.Admin;
import com.yixuan.yh.admin.mapper.AdminMapper;
import com.yixuan.yh.admin.mapper.AdminPermissionMapper;
import com.yixuan.yh.admin.mapper.PermissionMapper;
import com.yixuan.yh.commom.utils.SnowflakeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AdminInit implements ApplicationRunner {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private SnowflakeUtils snowflakeUtils;

    @Autowired
    private AdminPermissionMapper adminPermissionMapper;

    @Autowired
    private PermissionInitializer permissionInitializer;

    @Override
    public void run(ApplicationArguments args) {
        Admin admin = null;
        if (adminMapper.selectCount() == 0) {
            admin = new Admin();
            admin.setId(snowflakeUtils.nextId());
            admin.setName("admin");
//            String randomPassword = UUID.randomUUID().toString();
            String randomPassword = "admin";
            admin.setEncodedPassword(passwordEncoder.encode(randomPassword));
            adminMapper.insert(admin);

            log.warn("生成默认管理员--名字：{}、密码：{}", "admin", randomPassword);
        }
        if (admin == null) {
            admin = adminMapper.selectByCond(null, "admin", null).get(0);
        }

        /* 给主管理员添加所有权限 */
        Set<String> allPermissionValues = permissionInitializer.getAllPermissionValues();
        Set<String> adminPermissions = permissionMapper.selectAdminPermissions(admin.getId());
        List<String> requireAddPermissionList = new ArrayList<>();
        for (String permission : allPermissionValues) {
            if (!adminPermissions.contains(permission)) {
                requireAddPermissionList.add(permission);
            }
        }
        if (!requireAddPermissionList.isEmpty()) {
            adminPermissionMapper.insertAdminPermissions(admin.getId(), requireAddPermissionList);
        }
    }
}

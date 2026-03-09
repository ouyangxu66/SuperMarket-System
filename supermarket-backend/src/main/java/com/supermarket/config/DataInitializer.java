package com.supermarket.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.user.entity.Menu;
import com.supermarket.user.entity.Role;
import com.supermarket.user.mapper.MenuMapper;
import com.supermarket.user.mapper.RoleMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;

    public DataInitializer(RoleMapper roleMapper, MenuMapper menuMapper) {
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
    }

    @Override
    public void run(String... args) {
        initRoles();
        initMenusAndPermissions();
    }

    private void initRoles() {
        List<Role> roles = Arrays.asList(
                createRole("管理员", "ROLE_ADMIN"),
                createRole("店长", "ROLE_STORE_MANAGER"),
                createRole("收银员", "ROLE_CASHIER"),
                createRole("库管员", "ROLE_WAREHOUSE_KEEPER"),
                createRole("采购员", "ROLE_PURCHASER")
        );

        for (Role role : roles) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getRoleKey, role.getRoleKey());
            if (roleMapper.selectCount(wrapper) == 0) {
                roleMapper.insert(role);
            }
        }
    }

    private Role createRole(String name, String key) {
        Role role = new Role();
        role.setRoleName(name);
        role.setRoleKey(key);
        role.setStatus(1);
        role.setDeleted(0);
        return role;
    }

    private void initMenusAndPermissions() {
        // 1. 初始化菜单
        // 首页
        createAndSaveMenu(0L, "首页", "/dashboard", "dashboard/index", "dashboard:view", "Dashboard", "C", 1);

        // 员工管理 (目录 + 菜单)
        Menu system = createAndSaveMenu(0L, "系统管理", "/system", null, null, "Setting", "M", 2);
        Menu user = createAndSaveMenu(system.getId(), "员工管理", "/user", "user/UserListView", "user:list", "User", "C", 1);
        createAndSaveMenu(user.getId(), "员工新增", null, null, "user:add", null, "F", 1);
        createAndSaveMenu(user.getId(), "员工修改", null, null, "user:update", null, "F", 2);
        createAndSaveMenu(user.getId(), "员工删除", null, null, "user:delete", null, "F", 3);

        // 角色管理
        createAndSaveMenu(system.getId(), "角色管理", "/role", "user/RoleListView", "role:list", "Lock", "C", 2);

        // 商品管理
        Menu productRoot = createAndSaveMenu(0L, "商品管理", "/product", null, null, "Goods", "M", 3);
        Menu productList = createAndSaveMenu(productRoot.getId(), "商品列表", "/product/list", "product/ProductListView", "product:list", "List", "C", 1);
        createAndSaveMenu(productList.getId(), "商品新增", null, null, "product:add", null, "F", 1);
        createAndSaveMenu(productList.getId(), "商品修改", null, null, "product:update", null, "F", 2);
        createAndSaveMenu(productRoot.getId(), "分类管理", "/product/category", "product/CategoryView", "category:list", "Menu", "C", 2);

        // 库存管理
        Menu inventoryRoot = createAndSaveMenu(0L, "库存管理", "/inventory", null, null, "Box", "M", 4);
        Menu inventoryList = createAndSaveMenu(inventoryRoot.getId(), "库存列表", "/inventory/list", "inventory/InventoryListView", "inventory:list", "List", "C", 1);
        createAndSaveMenu(inventoryList.getId(), "库存入库", null, null, "inventory:in", null, "F", 1);
        createAndSaveMenu(inventoryList.getId(), "库存出库", null, null, "inventory:out", null, "F", 2);
        createAndSaveMenu(inventoryRoot.getId(), "库存盘点", "/inventory/count", "inventory/InventoryCountView", "inventory:count", "Checked", "C", 2);

        // 2. 分配权限 (为了简化，先全部分配给管理员，部分分配给店长等)
        assignAllPermissionsToAdmin();
        assignPermissionsToRole("ROLE_STORE_MANAGER", Arrays.asList("dashboard:view", "product:list", "product:add", "product:update", "category:list", "inventory:list", "inventory:count", "user:list"));
        assignPermissionsToRole("ROLE_CASHIER", List.of("dashboard:view")); // 之后加收银
        assignPermissionsToRole("ROLE_WAREHOUSE_KEEPER", Arrays.asList("dashboard:view", "inventory:list", "inventory:in", "inventory:out", "inventory:count", "product:list"));
        assignPermissionsToRole("ROLE_PURCHASER", Arrays.asList("dashboard:view", "product:list", "inventory:list"));
    }

    private void assignAllPermissionsToAdmin() {
        Role role = getRoleByKey("ROLE_ADMIN");
        if (role == null) return;

        List<Menu> allMenus = menuMapper.selectList(null);
        roleMapper.deleteRoleMenuByRoleId(role.getId());
        for (Menu menu : allMenus) {
            roleMapper.insertRoleMenu(role.getId(), menu.getId());
        }
    }

    private Menu createAndSaveMenu(Long parentId, String name, String path, String component, String perms, String icon, String type, Integer order) {
        // 先检查是否存在
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (perms != null) {
            wrapper.eq(Menu::getPerms, perms);
        } else {
            wrapper.eq(Menu::getMenuName, name).eq(Menu::getParentId, parentId);
        }

        Menu exist = menuMapper.selectOne(wrapper);
        if (exist != null) {
            boolean changed = false;
            if (name != null && !name.equals(exist.getMenuName())) {
                exist.setMenuName(name);
                changed = true;
            }
            if (path != null && (exist.getPath() == null || !path.equals(exist.getPath()))) {
                exist.setPath(path);
                changed = true;
            }
            if (component != null && (exist.getComponent() == null || !component.equals(exist.getComponent()))) {
                exist.setComponent(component);
                changed = true;
            }
            if (icon != null && (exist.getIcon() == null || !icon.equals(exist.getIcon()))) {
                exist.setIcon(icon);
                changed = true;
            }
            if (type != null && (exist.getMenuType() == null || !type.equals(exist.getMenuType()))) {
                exist.setMenuType(type);
                changed = true;
            }
            if (order != null && (exist.getOrderNum() == null || !order.equals(exist.getOrderNum()))) {
                exist.setOrderNum(order);
                changed = true;
            }
            if (changed) {
                menuMapper.updateById(exist);
            }
            return exist;
        }

        Menu menu = new Menu();
        menu.setParentId(parentId);
        menu.setMenuName(name);
        menu.setPath(path);
        menu.setComponent(component);
        menu.setPerms(perms);
        menu.setIcon(icon);
        menu.setMenuType(type);
        menu.setOrderNum(order);
        menu.setStatus(1);
        menu.setDeleted(0);
        menuMapper.insert(menu);
        return menu;
    }


    private void assignPermissionsToRole(String roleKey, List<String> perms) {
        Role role = getRoleByKey(roleKey);
        if (role == null) return;

        roleMapper.deleteRoleMenuByRoleId(role.getId());

        for (String perm : perms) {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getPerms, perm);
            Menu menu = menuMapper.selectOne(wrapper);
            if (menu != null) {
                roleMapper.insertRoleMenu(role.getId(), menu.getId());
                // 还要把父菜单也加上，否则前台可能不显示
                // 这里简化处理，严谨逻辑应该递归找父级
            }
        }
    }

    private Role getRoleByKey(String key) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, key);
        return roleMapper.selectOne(wrapper);
    }
}
